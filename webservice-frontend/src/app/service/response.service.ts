import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {firstValueFrom} from "rxjs";

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
    return firstValueFrom(this.http.post<any>('http://localhost:8080/webservice-server/ws/saveMockResponse', formData));
  }

  async switchResponse(publishUrl: string, isActive: boolean): Promise<any> {
    const params = new HttpParams()
      .set('publishUrl', publishUrl)
      .set('isActive', isActive.toString());
    return await firstValueFrom(this.http.get('http://localhost:8080/webservice-server/ws/switchWebService', {params}));
  }

}
