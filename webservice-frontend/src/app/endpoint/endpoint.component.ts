import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatInputModule} from "@angular/material/input";
import {SelectionModel} from "@angular/cdk/collections";
import {MatCheckbox} from "@angular/material/checkbox";
import {FormsModule} from "@angular/forms";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {HeaderComponent} from "../header/header.component";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatSort, MatSortModule} from "@angular/material/sort";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {HttpClientModule} from "@angular/common/http";
import {Endpoint} from "../model/models";
import {EndpointFormComponent} from "../endpoint-form/endpoint-form.component";
import {EndpointService} from "../service/endpoint.service";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {Router} from "@angular/router";
import {ErrorDialogComponent} from "../error-dialog/error-dialog.component";

const COLUMNS_SCHEMA = [
  {
    key: 'id',
    type: 'text',
    label: '編號',
  },
  {
    key: 'publishUri',
    type: 'text',
    label: '發布名稱',
  },
  {
    key: 'beanName',
    type: 'text',
    label: 'Bean名稱',
  },
  {
    key: 'classPath',
    type: 'text',
    label: 'Class路徑',
  },
  {
    key: 'jarFileId',
    type: 'file',
    label: 'Jar檔案編號',
  },
  {
    key: 'isActive',
    type: 'boolean',
    label: '狀態',
  },
  {
    key: 'isEdit',
    type: 'isEdit',
    label: '',
  },
];

@Component({
  selector: 'app-endpoint',
  standalone: true,
  imports: [
    MatTableModule,
    MatCardModule,
    MatSortModule,
    MatButtonModule,
    MatPaginatorModule,
    MatInputModule,
    MatCheckbox,
    FormsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    NgSwitch,
    NgIf,
    NgSwitchCase,
    DatePipe,
    NgSwitchDefault,
    NgForOf,
    HeaderComponent,
    HttpClientModule,
    MatSlideToggle
  ],
  templateUrl: './endpoint.component.html',
  styleUrl: './endpoint.component.css'
})
export class EndpointComponent implements OnInit, AfterViewInit {

  pageSize = 10;
  pageSizeOptions = [10, 50, 100];
  displayedColumns: string[] = COLUMNS_SCHEMA.map((col) => col.key);
  columnsSchema: any = COLUMNS_SCHEMA;

  dataSource: MatTableDataSource<Endpoint> = new MatTableDataSource<Endpoint>();
  @ViewChild(MatSort) dataSort: MatSort = new MatSort();
  @ViewChild(MatPaginator) paginator: MatPaginator = <MatPaginator>{};

  selection = new SelectionModel<Endpoint>(true, []);

  constructor(public dialog: MatDialog,
              private endpointService: EndpointService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.fetchDataFromService();
  }

  fetchDataFromService(): void {
    this.endpointService.fetchData().then(
      (response: any) => {
        console.log('Fetched data:', response.data);
        this.dataSource = new MatTableDataSource<Endpoint>(response.data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.dataSort;
      },
      (error) => {
        console.error('Error fetching data:', error);
        // Handle error
      }
    );
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  ngAfterViewInit(): void {
    this.dataSort.disableClear = true;
    this.dataSource.sort = this.dataSort;
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(data => this.selection.select(data));
  }

  checkboxLabel(row?: any): string {
    return (!row)
      ? `${this.isAllSelected() ? 'select' : 'deselect'} all`
      : `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.position + 1}`;
  }

  removeSelectedRows() {
    const deleteItem = confirm("確定刪除?");
    if (deleteItem) {
      const selectedRows = this.dataSource.data.filter(item => this.selection.isSelected(item));
      const selectedUrls = selectedRows.map(item => item.publishUri);
      // 移除前端表格中的選中行
      this.dataSource.data = this.dataSource.data.filter(item => !this.selection.isSelected(item));

      // 發送 DELETE 請求到後端
      this.endpointService.deleteEndpoint(selectedUrls)
        .then(
          response => {
            console.log('刪除成功', response);
          },
          error => {
            console.error('刪除失敗', error);
            // 這裡你可以根據需要處理刪除失敗的情況
          }
        );

      // 清空選擇
      this.selection.clear();
    }
    console.log(this.dataSource.data);
  }

  removeRow(id: number) {
    const deleteItem = confirm("確定刪除?");
    if (deleteItem) {
      const data = this.dataSource.data;
      const rowIndex = this.paginator.pageIndex * this.paginator.pageSize + id;
      const rowData = data[id];

      data.splice(rowIndex, 1);
      this.dataSource.data = [...data]; // 使用新的數組引用來觸發 Angular 變更檢測

      // 發送 DELETE 請求到後端
      this.endpointService.deleteEndpoint([rowData.publishUri])
        .then(
          response => {
            console.log('刪除成功', response);
          },
          error => {
            console.error('刪除失敗', error);
            // 可以在這裡處理刪除失敗的情況，例如恢復已刪除的行
            this.dataSource.data.splice(rowIndex, 0, rowData); // 恢復已刪除的行
            this.dataSource.data = [...this.dataSource.data]; // 重新設置數據以觸發變更檢測
          }
        );
    }
    console.log(id);
  }

  openEndpointForm(row?: Endpoint) {
    const dialogRef = this.dialog.open(EndpointFormComponent, {
      width: '600px',
      disableClose: true,
      data: row,
    });
    dialogRef.componentInstance.endpointSaved.subscribe((newEndpoint: Endpoint) => {
      if (!row) {
        this.addRow(newEndpoint);
      } else {
        Object.assign(row, newEndpoint);
      }
    });
  }

  responseList(row: Endpoint) {
    console.log(row);
    this.router.navigate(['/response', {publishUri: row.publishUri}]).then(r => console.log(r));
  }

  addRow(newEndpoint: Endpoint) {
    this.dataSource.data = [newEndpoint, ...this.dataSource.data];
  }

  onSwitchChange(event: any, row: Endpoint): void {
    const confirmation = confirm('你確定要變更此設定嗎？');
    if (confirmation) {
      console.log('User confirmed');
      this.endpointService.switchWebservice(row.publishUri, event.checked).then(
        (response: any) => {
          console.log('Switched web service:', response.data);
          row.isActive = event.checked;
        },
        (error) => {
          this.dialog.open(ErrorDialogComponent, {
            width: '600px',
            data: error.error,
          });
        }
      );
    } else {
      // 使用者點擊取消，回退切換狀態
      event.source.checked = !event.checked;
    }
  }
}
