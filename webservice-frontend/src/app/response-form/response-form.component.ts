import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {Response} from "../model/response";
import {ResponseService} from "../service/response.service";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
  selector: 'app-response-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatIcon,
    MatInputModule,
    NgIf,
    MatIconButton,
    MatCheckbox,
    MatDialogActions,
    MatButton,
    MatDialogTitle,
    MatDialogContent,
    MatFormFieldModule,
    NgClass
  ],
  templateUrl: './response-form.component.html',
  styleUrl: './response-form.component.css'
})
export class ResponseFormComponent implements OnInit{
  form!: FormGroup;
  isEditMode: boolean;
  publishUrl: string | null = null;
  message: string | null = null;
  messageType: 'success' | 'error' | null = null;
  countdown: number | null = null;
  isResponseContentValidJson = true;

  @Output() responseSaved = new EventEmitter<Response>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: Response,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ResponseFormComponent>,
    private responseService: ResponseService
  ) {
    this.isEditMode = !!data; // 判断是否为编辑模式
    this.initializeForm(data || {} as Response); // 初始化表单
  }

  private initializeForm(data: Response): void {
    this.publishUrl = data.publishUrl;
    const defaultData: Response = {
      publishUrl: '',
      method: '',
      condition: '',
      responseContent: '',
      isActive: false,
    };
    this.form = this.fb.group({
      publishUrl: [data?.publishUrl || defaultData.publishUrl, Validators.required],
      method: [data?.method || defaultData.method, Validators.required],
      condition: [data?.condition || defaultData.condition, Validators.required],
      responseContent: [data?.responseContent || defaultData.responseContent],
      isActive: [data?.isActive || defaultData.isActive],
    });
  }

  onSave(): void {
    if (this.form.valid) {
      console.log('Form data:', this.form.value);
      const formData = this.form.value;
      // formData.publishUrl = this.publishUrl;
      this.responseService.saveFormData(formData).then(
        (response: any) => {
          console.log('Fetched data:', response.data);
          this.message = 'Response updated successfully!';
          this.messageType = 'success';
          this.responseSaved.emit(response.data);
          // 倒數2秒後關閉對話框
          this.countdown = 2;
          const countdownInterval = setInterval(() => {
            if (this.countdown !== null && this.countdown > 0) {
              this.countdown--;
            } else {
              clearInterval(countdownInterval);
              this.dialogRef.close();
            }
          }, 1000);
        },
        (error) => {
          // console.error('Error fetching data:', error);
          this.message = 'Failed to update response!';
          this.messageType = 'error';
        }
      );
    } else {
      console.error('Form is invalid');
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  clearField(fieldName: string): void {
    this.form.get(fieldName)!.setValue('');
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      publishUrl: this.publishUrl,
      method: [''],
      condition: [''],
      responseContent: ['']
    });

    this.form.get('responseContent')!.valueChanges.subscribe(value => {
      this.isResponseContentValidJson = this.isValidJson(value);
    });
  }

  isValidJson(str: string): boolean {
    try {
      JSON.parse(str);
      return true;
    } catch (e) {
      return false;
    }
  }
}
