import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WaterReminderDTO {
  id?: number;
  frequency: number;
  quantity: number;
  active: boolean;
  userId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class HealthService {

  private apiUrl = 'http://localhost:8080/waterReminder';

  constructor(private http: HttpClient) {}

  addReminder(data: WaterReminderDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/add`, data);
  }

  updateReminder(id: number, data: WaterReminderDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${id}`, data);
  }

  getReminder(id: number): Observable<WaterReminderDTO> {
    return this.http.get<WaterReminderDTO>(`${this.apiUrl}/getById/${id}`);
  }

  deleteReminder(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}