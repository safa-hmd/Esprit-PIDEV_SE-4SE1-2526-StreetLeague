import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DemoService {
  constructor(private http: HttpClient) {}

  studentHello(): Observable<string> {
    return this.http.get(`${environment.baseUrl}/student/hello`, {
      responseType: 'text',
    });
  }

  teacherHello(): Observable<string> {
    return this.http.get(`${environment.baseUrl}/teacher/hello`, {
      responseType: 'text',
    });
  }
}