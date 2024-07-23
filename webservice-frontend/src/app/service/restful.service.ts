import { Injectable } from '@angular/core';
import {catchError, firstValueFrom, Observable, throwError} from "rxjs";
import {config} from "../config";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class RestfulService {

  constructor(private http: HttpClient) {
  }

  fetchData(): Promise<any> {
    return firstValueFrom(this.http.get(`${config.apiUrl}/webservice-server/api/getControllers`));
  }

  saveFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>(`${config.apiUrl}/webservice-server/api/saveController`, formData));
  }

  updateFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>(`${config.apiUrl}/webservice-server/api/updateController`, formData));
  }

  async deleteRestful(publishUrls: string[]): Promise<any> {
    return await firstValueFrom(this.http.request('delete', `${config.apiUrl}/webservice-server/api/removeController`, {
      body: publishUrls
    }).pipe(
      catchError(this.handleError)
    ));
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

  async switchRestful(publishUri: string, isActive: boolean): Promise<any> {
    const params = new HttpParams()
      .set('publishUri', publishUri)
      .set('isActive', isActive.toString());
    return await firstValueFrom(this.http.get(`${config.apiUrl}/webservice-server/api/switchController`, { params }));
  }

}
