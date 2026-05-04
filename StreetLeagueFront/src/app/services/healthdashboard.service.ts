import { environment } from 'src/environments/environment';
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

export interface UserHealthData {
  userId: number;
  fullName: string;
  email: string;
  weight: number;
  height: number;
  bmi: number;
  lastUpdated: string;
}

export interface UserGoal {
  id: number;
  goalType: string;
  targetValue: number;
  achieved: boolean;
}

export interface UserBadgeResponse {
  id: number;
  userId: number;
  badgeType: string;
  description: string;
  earnedDate: string;
}

export interface WaterLogResponse {
  date: string;
  totalMl: number;
  goalMl: number;
  goalReached: boolean;
}

@Injectable({ providedIn: 'root' })
export class HealthDashboardService {
  private apiUrl = `${environment.baseUrl}`;

  constructor(private http: HttpClient) {}

  getAllReminders(): Observable<WaterReminderResponse[]> {
    return this.http.get<WaterReminderResponse[]>(`${this.apiUrl}/waterReminder/getAll`);
  }

  getAllUsersHealth(): Observable<UserHealthData[]> {
    return this.http.get<UserHealthData[]>(`${this.apiUrl}/health/all`);
  }

  getUserGoals(userId: number): Observable<UserGoal[]> {
    return this.http.get<UserGoal[]>(`${this.apiUrl}/health/goal/${userId}`);
  }

  getUserBadges(userId: number): Observable<UserBadgeResponse[]> {
    return this.http.get<UserBadgeResponse[]>(`${this.apiUrl}/health/badges/${userId}`);
  }

  getWaterLogs(userId: number): Observable<WaterLogResponse[]> {
    return this.http.get<WaterLogResponse[]>(`${this.apiUrl}/health/water-logs/${userId}`);
  }
}

