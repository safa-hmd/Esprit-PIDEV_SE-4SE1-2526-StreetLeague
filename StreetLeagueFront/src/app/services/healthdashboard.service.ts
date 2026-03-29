import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WaterReminderResponse {
  id: number;
  frequency: number;
  quantity: number;
  active: boolean;
  userName: string;
  userEmail: string;
}

@Injectable({ providedIn: 'root' })
export class HealthDashboardService {
  private apiUrl = 'http://localhost:8086/StreetLeague';

  constructor(private http: HttpClient) {}

  getAllReminders(): Observable<WaterReminderResponse[]> {
    return this.http.get<WaterReminderResponse[]>(`${this.apiUrl}/waterReminder/getAll`);
  }
}
