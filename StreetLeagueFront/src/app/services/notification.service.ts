import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private baseUrl = 'http://localhost:8086/StreetLeague/api/notifications';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect') || localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    });
  }

  getMyNotifications(userId: string | number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/my?userId=${userId}`, { headers: this.getHeaders() });
  }

  getUnreadCount(userId: string | number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/unread-count?userId=${userId}`, { headers: this.getHeaders() });
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}/read`, {}, { headers: this.getHeaders() });
  }

  markAllAsRead(userId: string | number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/mark-all-read?userId=${userId}`, {}, { headers: this.getHeaders() });
  }
}
