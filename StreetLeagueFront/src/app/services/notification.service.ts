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

  getMyNotifications(): Observable<NotificationResponse[]> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.get<NotificationResponse[]>(
      `${this.base}/my?email=${email}`,
      { headers: this.getHeaders() }
    );
  }

  markAsRead(idNotification: number): Observable<void> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.put<void>(
      `${this.base}/${idNotification}/read?email=${email}`,
      {},
      { headers: this.getHeaders() }
    );
  }
}