import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgClass, NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {HeaderComponent} from "../header/header.component";
import {MatButton, MatButtonModule, MatIconButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource, MatTableModule
} from "@angular/material/table";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput, MatInputModule} from "@angular/material/input";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatSort, MatSortHeader, MatSortModule} from "@angular/material/sort";
import {SelectionModel} from "@angular/cdk/collections";
import {MatDialog, MatDialogActions, MatDialogContent, MatDialogModule, MatDialogTitle} from "@angular/material/dialog";
import {ActivatedRoute} from "@angular/router";
import {EndpointFormComponent} from "../endpoint-form/endpoint-form.component";
import {Response} from "../model/response";
import {ResponseService} from "../service/response.service";
import {ResponseFormComponent} from "../response-form/response-form.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
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
              private route: ActivatedRoute ) {
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

  openResponseForm(input?: Response | string) {
    let dialogData: { row?: Response; publishUrl?: string } = {};

    if (typeof input === 'string') {
      dialogData.publishUrl = input;
    } else if (input && typeof input === 'object') {
      dialogData.row = input;
    }

    const dialogRef = this.dialog.open(ResponseFormComponent, {
      width: '600px',
      disableClose: true,
      data: dialogData,
    });
    dialogRef.componentInstance.responseSaved.subscribe((newResponse: Response) => {
      this.addRow(newResponse);
    });
  }

  addRow(newResponse: Response) {
    this.dataSource.data = [newResponse, ...this.dataSource.data];
  }

  onSwitchChange(event: any, row: Response): void {
    const confirmation = confirm('你確定要變更此設定嗎？');
    if (confirmation) {
      console.log('User confirmed');
      this.responseService.switchResponse(row.publishUrl, event.checked).then(
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
