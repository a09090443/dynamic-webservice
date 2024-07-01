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
import {Endpoint} from "../model/endpoint";
import {EndpointFormComponent} from "../endpoint-form/endpoint-form.component";
import {EndpointService} from "../service/endpoint.service";
import {MatSlideToggle} from "@angular/material/slide-toggle";

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
              private endpointService: EndpointService) {
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
      this.dataSource.data = this.dataSource.data.filter(item => !this.selection.isSelected(item));
    }
    console.log(this.dataSource.data);
  }

  removeRow(id: number) {
    const deleteItem = confirm("確定刪除?");
    if (deleteItem) {
      const data = this.dataSource.data;
      data.splice(
        this.paginator.pageIndex * this.paginator.pageSize + id,
        1
      );
      this.dataSource.data = data;
    }
    console.log(id);
  }

  openDialog(row?: Endpoint) {
    const dialogRef = this.dialog.open(EndpointFormComponent, {
      width: '600px',
      disableClose: true,
      data: row,
    });
    dialogRef.componentInstance.endpointSaved.subscribe((newEndpoint: Endpoint) => {
      this.addRow(newEndpoint);
    });
  }

  addRow(newEndpoint: Endpoint) {
    this.dataSource.data = [newEndpoint, ...this.dataSource.data];
  }

  onSwitchChange(event: any, row: Endpoint): void {
    const confirmation = confirm('你確定要變更此設定嗎？');
    if (confirmation) {
      console.log('User confirmed');
      this.endpointService.switchWebservice(row.publishUrl, event.checked).then(
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
