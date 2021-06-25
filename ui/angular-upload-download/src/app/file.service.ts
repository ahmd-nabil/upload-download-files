import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private baseUrl = 'http://localhost:8080/files';

  constructor(private http: HttpClient) { }

  // load all files on server
  loadFiles(): Observable<Array<string>> {
    return this.http.get<Array<string>>(`${this.baseUrl}`);
  }
  // define upload logic 
  upload(formData: FormData): Observable<HttpEvent<string[]>> {
    return this.http.post<string[]>(`${this.baseUrl}/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }
  
  // define download logic
  download(filename: String): Observable<HttpEvent<Blob>> {
    return this.http.get(`${this.baseUrl}/${filename}`,{
      reportProgress: true,
      observe: 'events',
      responseType: 'blob'
    });
  }
}
