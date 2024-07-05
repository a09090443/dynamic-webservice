import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {catchError, firstValueFrom, Observable, throwError} from "rxjs";
import {Response} from "../model/response";

@Injectable({
  providedIn: 'root'
})
export class ResponseService {

  constructor(private http: HttpClient) {
  }

  fetchData(publishUrl: string): Promise<any> {
    const body = {publishUrl: publishUrl};
    return firstValueFrom(this.http.post('http://localhost:8080/webservice-server/ws/getResponseList', body));
  }

  saveFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>('http://localhost:8080/webservice-server/ws/saveMockResponse', formData));
  }

  updateFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>('http://localhost:8080/webservice-server/ws/updateResponse', formData));
  }

  async deleteResponse(ids: string[]): Promise<any> {
    return await firstValueFrom(this.http.request('delete', 'http://localhost:8080/webservice-server/ws/deleteResponse', {
      body: ids
    }).pipe(
      catchError(this.handleError)
    ));
  }

  async switchResponse(row: Response, isActive: boolean): Promise<any> {
    const params = new HttpParams()
      .set('id', row.id)
      .set('isActive', isActive.toString());
    return await firstValueFrom(this.http.get('http://localhost:8080/webservice-server/ws/switchResponse', { params }));
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
