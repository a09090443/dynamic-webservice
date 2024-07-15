import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {catchError, firstValueFrom, Observable, throwError} from "rxjs";
import {Response} from "../model/response";
import { config } from '../config';

@Injectable({
  providedIn: 'root'
})
export class ResponseService {

  constructor(private http: HttpClient) {
  }

  fetchData(publishUrl: string): Promise<any> {
    const body = {publishUrl: publishUrl};
    return firstValueFrom(this.http.post(`${config.apiUrl}/mockwebservice/ws/getResponseList`, body));
  }

  saveFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>(`${config.apiUrl}/mockwebservice/ws/saveMockResponse`, formData));
  }

  updateFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>(`${config.apiUrl}/mockwebservice/ws/updateResponse`, formData));
  }

  async deleteResponse(ids: string[]): Promise<any> {
    return await firstValueFrom(this.http.request('delete', `${config.apiUrl}/mockwebservice/ws/deleteResponse`, {
      body: ids
    }).pipe(
      catchError(this.handleError)
    ));
  }

  async switchResponse(row: Response, isActive: boolean): Promise<any> {
    const params = new HttpParams()
      .set('id', row.id)
      .set('isActive', isActive.toString());
    return await firstValueFrom(this.http.get(`${config.apiUrl}/mockwebservice/ws/switchResponse`, { params }));
  }

  private handleError(error: HttpErrorResponse): Observable<string> {
    if (error.error instanceof ErrorEvent) {
      // 客戶端或網絡錯誤
      console.error('An error occurred:', error.error.message);
    } else {
      // 伺服器返回錯誤狀態碼
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // 返回一個可以處理的錯誤訊息 Observable
    // @ts-ignore
    return throwError<string>('Something bad happened; please try again later.');
  }

}
