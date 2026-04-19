import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScheduleEvent } from '../models/schedule-event.model';

@Injectable({ providedIn: 'root' })
export class ScheduleService {

  private API = 'http://localhost:8086/StreetLeague/api/schedule';

  constructor(private http: HttpClient) {}

  getWeek(userId: number): Observable<ScheduleEvent[]> {
    return this.http.get<ScheduleEvent[]>(`${this.API}/${userId}/week`);
  }

  getMonth(userId: number): Observable<ScheduleEvent[]> {
    return this.http.get<ScheduleEvent[]>(`${this.API}/${userId}/month`);
  }
}