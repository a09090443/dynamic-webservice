import {Component, EventEmitter, Inject, Output} from '@angular/core';
import {Endpoint} from "../model/endpoint";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder} from "@angular/forms";
import {EndpointService} from "../service/endpoint.service";

@Component({
  selector: 'app-error-dialog',
  standalone: true,
  imports: [],
  templateUrl: './error-dialog.component.html',
  styleUrl: './error-dialog.component.css'
})
export class ErrorDialogComponent {
  message: string | null = null;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: string
  ) {
    this.initializeForm(data); // 初始化表单
  }

  private initializeForm(data: string): void {
    this.message = data;
  }
}
