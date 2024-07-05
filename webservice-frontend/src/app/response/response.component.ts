import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {HeaderComponent} from "../header/header.component";
import {MatButtonModule} from "@angular/material/button";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatInputModule} from "@angular/material/input";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatSort, MatSortModule} from "@angular/material/sort";
import {SelectionModel} from "@angular/cdk/collections";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {ActivatedRoute} from "@angular/router";
import {Response} from "../model/response";
import {ResponseService} from "../service/response.service";
import {ResponseFormComponent} from "../response-form/response-form.component";
import {FormsModule} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {HttpClientModule} from "@angular/common/http";

const COLUMNS_SCHEMA = [
  {
    key: 'checkbox',
    type: '',
    label: '',
  },
  {
    key: 'publishUrl',
    type: 'text',
    label: '發布名稱',
  },
  {
    key: 'method',
    type: 'text',
    label: '呼叫方法名稱',
  },
  {
    key: 'condition',
    type: 'text',
    label: 'Response條件',
  },
  {
    key: 'responseContent',
    type: 'text',
    label: '回應內容',
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
  selector: 'app-response',
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
  templateUrl: './response.component.html',
  styleUrl: './response.component.css'
})
export class ResponseComponent implements OnInit, AfterViewInit {

  pageSize = 10;
  pageSizeOptions = [10, 50, 100];
  displayedColumns: string[] = COLUMNS_SCHEMA.map((col) => col.key);
  columnsSchema: any = COLUMNS_SCHEMA;
  publishUrl: string = '';

  dataSource: MatTableDataSource<Response> = new MatTableDataSource<Response>();
  @ViewChild(MatSort) dataSort: MatSort = new MatSort();
  @ViewChild(MatPaginator) paginator: MatPaginator = <MatPaginator>{};

  selection = new SelectionModel<Response>(true, []);

  constructor(public dialog: MatDialog,
              private responseService: ResponseService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const publishUrl = params['publishUrl'];
      this.fetchDataFromService(publishUrl);
    });
  }

  fetchDataFromService(publishUrl: string): void {
    this.publishUrl = publishUrl;
    this.responseService.fetchData(publishUrl).then(
      (response: any) => {
        console.log('Fetched data:', response.data);
        this.dataSource = new MatTableDataSource<Response>(response.data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.dataSort;
      },
      (error) => {
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
      const selectedIdss = selectedRows.map(item => item.id);
      // 移除前端表格中的選中行
      this.dataSource.data = this.dataSource.data.filter(item => !this.selection.isSelected(item));

      // 發送 DELETE 請求到後端
      this.responseService.deleteResponse(selectedIdss)
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
      this.selection.clear();    }
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
      this.responseService.deleteResponse([rowData.id])
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

  openResponseForm(input?: Response | string) {
    let dialogData: { publishUrl?: string } = {};

    if (typeof input === 'string') {
      dialogData.publishUrl = input;
    } else if (input && typeof input === 'object') {
      dialogData = input;
    }

    const dialogRef = this.dialog.open(ResponseFormComponent, {
      width: '600px',
      disableClose: true,
      data: dialogData,
    });
    dialogRef.componentInstance.responseSaved.subscribe((newResponse: Response) => {
      if (typeof input === 'string') {
        this.addRow(newResponse);
      } else {
        Object.assign(dialogData, newResponse);
      }
    });
  }

  addRow(newResponse: Response) {
    this.dataSource.data = [newResponse, ...this.dataSource.data];
  }

  onSwitchChange(event: any, row: Response): void {
    const confirmation = confirm('你確定要變更此設定嗎？');
    if (confirmation) {
      this.responseService.switchResponse(row, event.checked).then(
        (response: any) => {
          console.log('Switched web service:', response.data);
          // Handle success
        },
        (error) => {
          console.error('Error switching web service:', error);
          // Handle error
        }
      );
    } else {
      // 使用者點擊取消，回退切換狀態
      event.source.checked = !event.checked;
    }
  }
}
