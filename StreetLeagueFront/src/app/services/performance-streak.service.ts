// src/app/services/performance-streak.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface PlayerStatsDto {
  playerId: number;
  playerName: string;
  currentStreak: number;
  bestStreak: number;
  totalPoints: number;
  badge: string | null;
  status: string;
  rank: number | null;
  momentumScore: number;
  trend: string | null;
  fatigueRisk: number;
  riskLevel: string | null;
  recommendation: string | null;
  recommendedRestDays: number | null;
  synergyContribution: number | null;
}

@Injectable({ providedIn: 'root' })
export class PerformanceStreakService {

  private base = 'http://localhost:8086/StreetLeague/api/performance';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    let message = 'Erreur réseau ou serveur.';
    if (err.error?.message) {
      message = err.error.message;
    } else if (err.status === 403) {
      message = 'Accès refusé (token invalide ou expiré).';
    } else if (err.status === 404) {
      message = 'Joueur introuvable.';
    } else if (err.status === 0) {
      message = 'Backend inaccessible. Vérifiez que le serveur tourne.';
    }
    console.error('PerformanceStreakService error:', err);
    return throwError(() => ({ message }));
  }

  /**
   * Check-in du jour : crée ou MET À JOUR l'entrée existante.
   * Peut être appelé plusieurs fois — recalcule fatigue + streak à chaque fois.
   */
  checkin(playerId: number, attendanceType: string): Observable<PlayerStatsDto> {
    return this.http.post<PlayerStatsDto>(
      `${this.base}/checkin?playerId=${playerId}&attendanceType=${attendanceType}`,
      {},
      { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getPlayerStats(playerId: number): Observable<PlayerStatsDto> {
    return this.http.get<PlayerStatsDto>(
      `${this.base}/player/${playerId}`,
      { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getGlobalLeaderboard(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/leaderboard`,
      { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getGlobalMomentum(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/momentum`,
      { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getGlobalFatigueAlerts(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/fatigue-alerts`,
      { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }
}