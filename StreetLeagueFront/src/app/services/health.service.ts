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
// ── Diet ──
export interface DietRequestDTO {
  age: number;
  bmi: number;
}

export interface DietResponseDTO {
  id: number;
  goal: string;
  status: string;
daily_calories: number;  
  diet_plan: string[];     
  createdDate?: string;
}
export interface GoalDTO {
  goalType: 'WATER' | 'BMI';
  targetValue: number;
}

export interface FitnessReportDTO {
  userId: number;
  playerName: string;
  bmiScore: number;
  hydrationScore: number;
  consistencyScore: number;
  trendScore: number;
  finalScore: number;
  status: string;
  statusColor: string;
  currentBmi: number;
  bmiCategory: string;
  healthLogsLast30Days: number;
  waterLogsLast30Days: number;
  recommendations: string[];
  generatedAt: string;
}

export interface RewardDTO {
  result: string;
  message: string;
  canRetry: boolean;
  segmentIndex: number;
}

export interface DailyWaterLog {
  id: number;
  totalMl: number;
  goalMl: number;
  goalReached: boolean;
  date: string;
}

export interface UserBadge {
  id: number;
  badgeType: string;
  description: string;
  earnedDate: string;
}
//
// Ajouter cette interface
export interface WeeklyHealthReportDTO {
  fullName: string;
  weekStart: string;
  weekEnd: string;
  currentWeight: number;
  currentHeight: number;
  currentBmi: number;
  weightChangeThisWeek: number;
  totalWaterConsumedMl: number;
  daysGoalReached: number;
  avgDailyWaterMl: number;
  goals: GoalSummary[];
  bmiHistory: BmiEntry[];
    currentWaterStreak: number;   // ✅ ajoute
  longestWaterStreak: number;
}

export interface GoalSummary {
  goalType: string;
  targetValue: number;
  achieved: boolean;
}

export interface BmiEntry {
  date: string;
  bmi: number;
  weight: number;
}//

@Injectable({
  providedIn: 'root'
})
export class HealthService {

  private baseUrl = 'http://localhost:8086/StreetLeague';
  private apiUrl = `${this.baseUrl}/waterReminder`;
  private healthApiUrl = `${this.baseUrl}/health`;

  constructor(private http: HttpClient) {}

  // ── Water Reminder ──
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

  // ── BMI / Health ──
  updateHealth(userId: number, data: { weight: number; height: number; bmi: number }): Observable<any> {
    return this.http.put(`${this.healthApiUrl}/update/${userId}`, data);
  }

  // ── Daily Water Log ──
  logWater(userId: number, amount: number): Observable<DailyWaterLog> {
    return this.http.post<DailyWaterLog>(`${this.healthApiUrl}/water/log/${userId}`, { amount });
  }

  // ── Goals ──
  setGoal(userId: number, dto: GoalDTO): Observable<any> {
    return this.http.post(`${this.healthApiUrl}/goal/${userId}`, dto);
  }

  getGoals(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.healthApiUrl}/goal/${userId}`);
  }

  // ── Badges ──
  getBadges(userId: number): Observable<UserBadge[]> {
    return this.http.get<UserBadge[]>(`${this.healthApiUrl}/badges/${userId}`);
  }

  // ── Spin ──
  getSpinStatus(userId: number): Observable<{ canSpin: boolean }> {
    return this.http.get<{ canSpin: boolean }>(`${this.healthApiUrl}/spin/status/${userId}`);
  }

  spin(userId: number): Observable<RewardDTO> {
    return this.http.post<RewardDTO>(`${this.healthApiUrl}/spin/${userId}`, {});
  }
  getTodayWaterLogs(userId: number): Observable<any[]> {
  return this.http.get<any[]>(`${this.healthApiUrl}/water/today/${userId}`);
}
resetTodayWater(userId: number): Observable<any> {
  return this.http.delete(`${this.healthApiUrl}/water/reset/${userId}`);
}
/*getWeeklyReport(userId: number): Observable<WeeklyHealthReportDTO> {
  return this.http.get<WeeklyHealthReportDTO>(
    `${this.healthApiUrl}/weekly-report/${userId}`
  );
}*/

getWeeklyReport(userId: number, asOf?: string): Observable<WeeklyHealthReportDTO> {
  const params = asOf ? `?asOf=${asOf}` : '';
  return this.http.get<WeeklyHealthReportDTO>(
    `${this.healthApiUrl}/weekly-report/${userId}${params}`
  );
}


// ── Diet ──
getDietRecommendation(userId: number, age: number, bmi: number): Observable<any> {
  return this.http.post(`${this.baseUrl}/diet/recommend/${userId}`, { age, bmi });
}

getLastDiet(userId: number): Observable<DietResponseDTO> {
  return this.http.get<DietResponseDTO>(
    `${this.baseUrl}/diet/last/${userId}`
  );
}
getStreak(userId: number): Observable<{currentStreak: number, longestStreak: number, lastGoalDate: string}> {
  return this.http.get<any>(`${this.healthApiUrl}/streak/${userId}`);
}

getFitnessReport(userId: number): Observable<FitnessReportDTO> {
  return this.http.get<FitnessReportDTO>(`${this.healthApiUrl}/fitness-report/${userId}`);
}
}
