import { environment } from 'src/environments/environment';
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

  // Métriques avancées existantes
  consistencyScore: number | null;
  performanceLevel: string | null;
  weeklyAverage: number | null;
  activeDaysLast30: number | null;
  attendanceRate: number | null;
  recoveryScore: number | null;
  predictedStreakIn7Days: number | null;
  streakContinuationProbability: number | null;
  injuryRiskScore: number | null;
  injuryRiskLevel: string | null;
  workloadFactor: number | null;
  weeklyHistory: number[] | null;
  pointsThisWeek: number | null;
  pointsThisMonth: number | null;

  // ══ NOUVEAUX — ACWR ══════════════════════════════════════════════════
  /** Ratio ACWR brut. Optimal : 0.8–1.3 | Danger : > 1.5 */
  acwr: number | null;
  /** Charge aiguë absolue sur 7 jours */
  acuteLoad: number | null;
  /** Charge chronique (moyenne hebdo sur 28j) */
  chronicLoad: number | null;
  /** Zone ACWR : 'UNDERLOAD' | 'OPTIMAL' | 'WARNING' | 'DANGER' */
  acwrZone: 'UNDERLOAD' | 'OPTIMAL' | 'WARNING' | 'DANGER' | null;
  /** Recommandation basée sur l'ACWR */
  acwrRecommendation: string | null;

  // ══ NOUVEAUX — Anomalies ══════════════════════════════════════════════
  /** 'NORMAL' | 'PERFORMANCE_DROP' | 'PERFORMANCE_DROP_SUSPECTED' | 'PERFORMANCE_SPIKE' */
  anomalyType: 'NORMAL' | 'PERFORMANCE_DROP' | 'PERFORMANCE_DROP_SUSPECTED' | 'PERFORMANCE_SPIKE' | null;
  /** 'NONE' | 'MEDIUM' | 'HIGH' | 'CRITICAL' */
  anomalySeverity: 'NONE' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | null;
  /** Z-Score du score actuel vs historique 28j. < -2.0 = chute significative */
  zScore: number | null;
  /** Valeur EWMA courante (tendance lissée) */
  ewmaScore: number | null;
  /** Chute EWMA par rapport à la moyenne (0–1, ex : 0.40 = chute de 40%) */
  ewmaDrop: number | null;
  /** Message explicatif généré par l'algorithme */
  anomalyMessage: string | null;
  /** true si une alerte coach a été ou doit être envoyée */
  coachAlertSent: boolean | null;
}

@Injectable({ providedIn: 'root' })
export class PerformanceStreakService {

  private base = `${environment.baseUrl}/api/performance`;

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
    if (err.error?.message)      message = err.error.message;
    else if (err.status === 403) message = 'Accès refusé (token invalide ou expiré).';
    else if (err.status === 404) message = 'Joueur introuvable.';
    else if (err.status === 0)   message = 'Backend inaccessible. Vérifiez que le serveur tourne.';
    console.error('PerformanceStreakService error:', err);
    return throwError(() => ({ message }));
  }

  // ── Endpoints existants ──────────────────────────────────────────────

  checkin(playerId: number, attendanceType: string): Observable<PlayerStatsDto> {
    return this.http.post<PlayerStatsDto>(
      `${this.base}/checkin?playerId=${playerId}&attendanceType=${attendanceType}`,
      {}, { headers: this.getHeaders() }
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
      `${this.base}/leaderboard`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getGlobalMomentum(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/momentum`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getGlobalFatigueAlerts(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/fatigue-alerts`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getInjuryRiskRanking(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/injury-risk`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getConsistencyRanking(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/consistency-ranking`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getPlayersByLevel(level: string): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/by-level/${level}`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  getPerformancePredictions(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/predictions`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  // ══ NOUVEAUX — ACWR ══════════════════════════════════════════════════

  /**
   * Stats ACWR détaillées d'un joueur (ratio, charge aiguë/chronique, zone).
   * GET /api/performance/acwr/:playerId
   */
  getAcwrStats(playerId: number): Observable<PlayerStatsDto> {
    return this.http.get<PlayerStatsDto>(
      `${this.base}/acwr/${playerId}`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  /**
   * Classement global par ACWR décroissant (plus à risque en premier).
   * GET /api/performance/acwr/ranking
   */
  getAcwrRanking(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/acwr/ranking`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  // ══ NOUVEAUX — Anomalies ══════════════════════════════════════════════

  /**
   * Analyse Z-Score + EWMA pour un joueur spécifique.
   * GET /api/performance/anomaly/:playerId
   */
  getAnomalyStats(playerId: number): Observable<PlayerStatsDto> {
    return this.http.get<PlayerStatsDto>(
      `${this.base}/anomaly/${playerId}`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  /**
   * Liste des joueurs avec anomalies actives (triés par sévérité).
   * Endpoint principal du dashboard coach.
   * GET /api/performance/anomaly/alerts
   */
  getAnomalyAlerts(): Observable<PlayerStatsDto[]> {
    return this.http.get<PlayerStatsDto[]>(
      `${this.base}/anomaly/alerts`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  // ══ ENDPOINT COMBINÉ ══════════════════════════════════════════════════

  /**
   * Analyse complète ACWR + Anomalie + toutes métriques pour un joueur.
   * Endpoint principal vue détaillée coach.
   * GET /api/performance/full-analysis/:playerId
   */
  getFullAnalysis(playerId: number): Observable<PlayerStatsDto> {
    return this.http.get<PlayerStatsDto>(
      `${this.base}/full-analysis/${playerId}`, { headers: this.getHeaders() }
    ).pipe(catchError(this.handleError));
  }

  // ══ Helpers utilitaires côté frontend ════════════════════════════════

  /** Retourne la couleur CSS selon la zone ACWR. */
  getAcwrZoneColor(zone: string | null): string {
    const colors: Record<string, string> = {
      'UNDERLOAD': '#60a5fa',   // bleu — sous-entraînement
      'OPTIMAL':   '#4ade80',   // vert — zone idéale
      'WARNING':   '#facc15',   // jaune — surveillance
      'DANGER':    '#f87171',   // rouge — danger
    };
    return colors[zone ?? ''] ?? '#9ca3af';
  }

  /** Retourne l'icône selon la zone ACWR. */
  getAcwrZoneIcon(zone: string | null): string {
    const icons: Record<string, string> = {
      'UNDERLOAD': '🔵',
      'OPTIMAL':   '✅',
      'WARNING':   '⚠️',
      'DANGER':    '🚨',
    };
    return icons[zone ?? ''] ?? '⚪';
  }

  /** Retourne la couleur selon la sévérité d'anomalie. */
  getAnomalySeverityColor(severity: string | null): string {
    const colors: Record<string, string> = {
      'NONE':     '#4ade80',
      'MEDIUM':   '#facc15',
      'HIGH':     '#fb923c',
      'CRITICAL': '#f87171',
    };
    return colors[severity ?? ''] ?? '#9ca3af';
  }

  /** Retourne l'icône selon le type d'anomalie. */
  getAnomalyTypeIcon(type: string | null): string {
    const icons: Record<string, string> = {
      'NORMAL':                    '✅',
      'PERFORMANCE_DROP':          '📉',
      'PERFORMANCE_DROP_SUSPECTED':'🔍',
      'PERFORMANCE_SPIKE':         '📈',
    };
    return icons[type ?? ''] ?? '⚪';
  }
}
