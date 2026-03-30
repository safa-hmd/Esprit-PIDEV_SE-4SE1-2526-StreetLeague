import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/api-url';

@Injectable({
  providedIn: 'root',
})
export class DemoService {
  constructor(private http: HttpClient) {}

  studentHello(): Observable<string> {
    return this.http.get(`${API_BASE_URL}/student/hello`, {
      responseType: 'text',
    });
  }

  teacherHello(): Observable<string> {
    return this.http.get(`${API_BASE_URL}/teacher/hello`, {
      responseType: 'text',
    });
  }
}