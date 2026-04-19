// src/app/services/notification.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NotificationResponse } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private base = 'http://localhost:8086/StreetLeague/notification';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect');
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  private get email(): string {
    return localStorage.getItem('EmailUserConnect') ?? '';
  }

  getMyNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(
      `${this.base}/my?email=${this.email}`,
      { headers: this.getHeaders() }
    );
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(
      `${this.base}/${id}/read?email=${this.email}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(
      `${this.base}/read-all?email=${this.email}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.base}/${id}/delete?email=${this.email}`,
      { headers: this.getHeaders() }
    );
  }
}