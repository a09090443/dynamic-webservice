import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {catchError, firstValueFrom, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class EndpointService {

  constructor(private http: HttpClient) {
  }

  fetchData(): Promise<any> {
    return firstValueFrom(this.http.get('http://localhost:8080/mockwebservice/ws/getEndpoints'));
  }

  async uploadFile(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response: any = await firstValueFrom(this.http.post('http://localhost:8080/mockwebservice/ws/uploadJarFile', formData).pipe(
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

  private handleError(error: HttpErrorResponse) {
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
    return throwError('Something bad happened; please try again later.');
  }
  // uploadFile(file: File): Promise<any> {
  //   const formData = new FormData();
  //   formData.append('file', file);
  //   return firstValueFrom(this.http.post('http://localhost:8080/mockwebservice/ws/uploadJarFile', formData));
  // }
}
