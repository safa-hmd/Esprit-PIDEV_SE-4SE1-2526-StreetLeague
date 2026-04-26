import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { ScheduleEvent } from '../models/schedule-event.model';

export interface ScheduleResponse {
  events:     ScheduleEvent[];
  topFields?: any[];
}

@Injectable({ providedIn: 'root' })
export class ScheduleService {

  private readonly API = 'http://localhost:8086/StreetLeague/api/schedule';

  constructor(private http: HttpClient) {}

  private headers(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect');
    return token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
  }

  // ── Week schedule ────────────────────────────────────────────────
  getWeek(userId: number, lat?: number, lng?: number): Observable<ScheduleEvent[]> {
    let params = new HttpParams();
    if (lat != null) params = params.set('lat', lat.toString());
    if (lng != null) params = params.set('lng', lng.toString());

    return this.http.get<ScheduleResponse>(
      `${this.API}/${userId}/week`,
      { headers: this.headers(), params }
    ).pipe(
      map(response => this.normalizeEvents(response?.events || []))
    );
  }

  // ── Month schedule ───────────────────────────────────────────────
  getMonth(userId: number, lat?: number, lng?: number): Observable<ScheduleEvent[]> {
    let params = new HttpParams();
    if (lat != null) params = params.set('lat', lat.toString());
    if (lng != null) params = params.set('lng', lng.toString());

    return this.http.get<ScheduleResponse>(
      `${this.API}/${userId}/month`,
      { headers: this.headers(), params }
    ).pipe(
      map(response => this.normalizeEvents(response?.events || []))
    );
  }

  // ── Normalize events — fix date arrays, assign colors, defaults ──
  private normalizeEvents(events: any[]): ScheduleEvent[] {
    return events.map(ev => {
      const normalized: ScheduleEvent = {
        ...ev,
        startTime:  this.normalizeDate(ev.startTime),
        endTime:    this.normalizeDate(ev.endTime),
        color:      ev.color || this.defaultColor(ev.type),
        hasConflict: ev.hasConflict || false,
        status:     ev.status || 'SCHEDULED',
      };
      return normalized;
    });
  }

  // Spring Boot sometimes returns [year, month, day, hour, min] arrays
  private normalizeDate(val: any): string {
    if (!val) return new Date().toISOString();
    if (Array.isArray(val)) {
      const [y, mo, d, h = 0, m = 0] = val;
      return new Date(y, mo - 1, d, h, m).toISOString();
    }
    return val;
  }

  private defaultColor(type: string): string {
    switch (type) {
      case 'MATCH':      return '#e74c3c';
      case 'TRAINING':   return '#3498db';
      case 'TOURNAMENT': return '#8e44ad';
      default:           return '#7f8c8d';
    }
  }
}