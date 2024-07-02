import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {catchError, firstValueFrom, Observable, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ResponseService {

  constructor(private http: HttpClient) {
  }

  fetchData(publishUrl: string): Promise<any> {
    const body = { publishUrl: publishUrl };
    return firstValueFrom(this.http.post('http://localhost:8080/webservice-server/ws/getResponseList', body));
  }

  saveFormData(formData: any): Promise<any> {
    return firstValueFrom(this.http.post<any>('http://localhost:8080/webservice-server/ws/registerWebService', formData));
  }

  async switchWebservice(publishUrl: string, isActive: boolean): Promise<any> {
    const params = new HttpParams()
      .set('publishUrl', publishUrl)
      .set('isActive', isActive.toString());
    return await firstValueFrom(this.http.get('http://localhost:8080/webservice-server/ws/switchWebService', {params}));
  }

  async uploadFile(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response: any = await firstValueFrom(this.http.post('http://localhost:8080/webservice-server/ws/uploadJarFile', formData).pipe(
        catchError(this.handleError)
      ));
      if (response.code === 200) {
        return response.data; // 返回 data
      } else {
        throw new Error(response.message);
      }
    } catch (error) {
      console.error('Error uploading file:', error);
      throw error; // 可以根據需要進行進一步處理
    }
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
