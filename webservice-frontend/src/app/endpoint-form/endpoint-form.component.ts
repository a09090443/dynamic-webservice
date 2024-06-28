import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {NgIf} from "@angular/common";
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInputModule} from '@angular/material/input';
import {Endpoint} from "../model/endpoint";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {EndpointService} from "../service/endpoint.service";

@Component({
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
    MatFormFieldModule
  ],
  templateUrl: './endpoint-form.component.html',
  styleUrl: './endpoint-form.component.css'
})
export class EndpointFormComponent {
  form!: FormGroup;
  isEditMode: boolean;
  jarFileName: string | null = null;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: Endpoint,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EndpointFormComponent>,
    private endpointService: EndpointService
  ) {
    this.isEditMode = !!data; // 判断是否为编辑模式
    this.initializeForm(data || {} as Endpoint); // 初始化表单
  }

  private initializeForm(data: Endpoint): void {
    const defaultData: Endpoint = {
      rowId: '',
      publishUrl: '',
      beanName: '',
      classPath: '',
      jarFileId: '',
      jarFileName: '',
      file: null,
      isActive: false,
    };
    this.jarFileName = data?.jarFileName || defaultData.jarFileName;

    this.form = this.fb.group({
      rowId: [data?.rowId || defaultData.rowId],
      publishUrl: [data?.publishUrl || defaultData.publishUrl, Validators.required],
      beanName: [data?.beanName || defaultData.beanName, Validators.required],
      classPath: [data?.classPath || defaultData.classPath, Validators.required],
      jarFileId: [data?.jarFileId || defaultData.jarFileId],
      file: [defaultData.file],
      isActive: [data?.isActive || defaultData.isActive],
    });
  }

  onSave(): void {
    if (this.form.valid) {
      console.log('Form data:', this.form.value);
      // this.dialogRef.close(this.form.value);
      // Assuming you want to send the entire form value to the server
      const formData = this.form.value;
      this.endpointService.saveFormData(formData).then(
        (response: any) => {
          console.log('Fetched data:', response.data);
        },
        (error) => {
          console.error('Error fetching data:', error);
          // 处理错误情况
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

  onFileChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files && inputElement.files.length > 0) {
      const file = inputElement.files[0];
      this.form.patchValue({file});
      this.form.get('file')!.updateValueAndValidity();
    }
  }

  removeFile(): void {
    this.form.get('file')!.setValue(null);
  }

  async uploadFile() {
    const file = this.form.get('file')?.value;
    if (file) {
      try {
        const responseData = await this.endpointService.uploadFile(file);
        this.jarFileName = responseData.jarFileName;
        this.form.get('jarFileId')!.setValue(responseData.jarFileId);
        console.log('File uploaded successfully:', responseData);
      } catch (error) {
        console.error('Error uploading file:', error);
      }
    } else {
      console.error('No file selected');
    }
  }
}
