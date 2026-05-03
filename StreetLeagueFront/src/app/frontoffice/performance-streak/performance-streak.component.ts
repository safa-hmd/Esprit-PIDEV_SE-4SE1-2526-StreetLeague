// src/app/frontoffice/performance-streak/performance-streak.component.ts
import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  PerformanceStreakService,
  PlayerStatsDto
} from 'src/app/services/performance-streak.service';

@Component({
  selector: 'app-performance-streak',
  templateUrl: './performance-streak.component.html',
  styleUrls: ['./performance-streak.component.css']
})
export class PerformanceStreakComponent implements OnInit, OnChanges, OnDestroy {

  @Input() playerId: number = 0;

  // ── Données ────────────────────────────────────────────────────────────
  leaderboard: PlayerStatsDto[]         = [];
  consistencyRanking: PlayerStatsDto[]  = [];
  fatigueAlerts: PlayerStatsDto[]       = [];
  currentPlayerStats: PlayerStatsDto | null = null;
  momentumData: PlayerStatsDto[]        = [];

  // Vue active du tab
  activeTab: 'leaderboard' | 'consistency' | 'momentum' = 'leaderboard';

  maxStreakInLeaderboard = 30;

  loading        = false;
  checkinLoading = false;
  error: string | null = null;
  checkinType    = 'TRAINING';

  notification: { message: string; type: 'success' | 'error' } | null = null;
  private notifTimeout: any = null;
  private destroy$ = new Subject<void>();

  constructor(private streakService: PerformanceStreakService) {}

  ngOnInit(): void  { this.loadAllData(); }
  ngOnChanges(c: SimpleChanges): void {
    if (c['playerId'] && this.playerId > 0) this.loadPlayerStats();
  }
  ngOnDestroy(): void {
    this.destroy$.next(); this.destroy$.complete();
    if (this.notifTimeout) clearTimeout(this.notifTimeout);
  }

  // ── Notification ───────────────────────────────────────────────────────
  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    if (this.notifTimeout) clearTimeout(this.notifTimeout);
    this.notifTimeout = setTimeout(() => { this.notification = null; }, 4000);
  }

  // ── Chargement ─────────────────────────────────────────────────────────
  loadAllData(): void {
    this.loading = true;
    this.error   = null;

    forkJoin({
      leaderboard:  this.streakService.getGlobalLeaderboard(),
      consistency:  this.streakService.getConsistencyRanking(),
      alerts:       this.streakService.getGlobalFatigueAlerts(),
      momentum:     this.streakService.getGlobalMomentum()
    })
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: ({ leaderboard, consistency, alerts, momentum }) => {
        this.leaderboard        = leaderboard;
        this.consistencyRanking = consistency;
        this.fatigueAlerts      = alerts;
        this.momentumData       = momentum;
        this.maxStreakInLeaderboard = leaderboard.length > 0
          ? Math.max(...leaderboard.map(p => p.bestStreak ?? p.currentStreak ?? 0), 1)
          : 30;
        this.loading = false;
        this.loadPlayerStats();
      },
      error: (err) => {
        this.error   = 'Impossible de charger les données.';
        this.loading = false;
      }
    });
  }

  loadPlayerStats(): void {
    const storedId = Number(localStorage.getItem('UserIdConnect')) || 0;
    const id = this.playerId > 0 ? this.playerId : storedId;
    if (id > 0) {
      this.streakService.getPlayerStats(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next:  (data) => this.currentPlayerStats = data,
          error: (err)  => console.error('Error loading player stats', err)
        });
    }
  }

  // ── Check-in ───────────────────────────────────────────────────────────
  doCheckin(): void {
    const playerId = Number(localStorage.getItem('UserIdConnect')) || 0;
    if (!playerId) {
      this.showNotification('⚠️ Veuillez vous connecter pour continuer.', 'error');
      return;
    }
    if (this.checkinLoading) return;
    this.checkinLoading = true;

    this.streakService.checkin(playerId, this.checkinType)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.checkinLoading = false;
          const badge   = result.badge   ? ` 🏆 ${result.badge}` : '';
          const fatigue = result.fatigueRisk != null
            ? ` | Fatigue: ${((result.fatigueRisk) * 100).toFixed(0)}%` : '';
          this.showNotification(
            `✅ Check-in ! Streak: ${result.currentStreak}j | Niveau: ${result.performanceLevel}${badge}${fatigue}`,
            'success'
          );
          this.currentPlayerStats = result;
          this.loadAllData();
        },
        error: (err) => {
          this.checkinLoading = false;
          this.showNotification(`❌ Erreur: ${err.message || 'Erreur inconnue'}`, 'error');
        }
      });
  }

  // ── Utilitaires affichage ─────────────────────────────────────────────
  getRankClass(rank: number): string {
    if (rank === 1) return 'top1';
    if (rank === 2) return 'top2';
    if (rank === 3) return 'top3';
    return 'default';
  }

  getRiskColor(riskLevel: string | null): string {
    const c: Record<string, string> = {
      'CRITICAL': '#ff3b5c', 'HIGH': '#ff6b35',
      'MODERATE': '#ffd700', 'LOW':  '#7fff6b'
    };
    return c[riskLevel ?? ''] ?? '#7fff6b';
  }

  getStatusClass(status: string): string {
    const c: Record<string, string> = { 'OPTIMAL': 'status-optimal', 'ACTIF': 'status-actif' };
    return c[status] ?? 'status-faible';
  }

  getLevelClass(level: string | null): string {
    const c: Record<string, string> = {
      'ELITE': 'level-elite', 'PRO': 'level-pro',
      'INTERMEDIATE': 'level-inter', 'BEGINNER': 'level-beginner'
    };
    return c[level ?? ''] ?? 'level-beginner';
  }

  getLevelIcon(level: string | null): string {
    const c: Record<string, string> = {
      'ELITE': '👑', 'PRO': '⭐', 'INTERMEDIATE': '🔵', 'BEGINNER': '🟢'
    };
    return c[level ?? ''] ?? '🟢';
  }

  getBadgeIcon(badge: string | null): string {
    if (!badge)                  return '⚪';
    if (badge.includes('LEGEND')) return '👑';
    if (badge.includes('IRON'))  return '🏆';
    if (badge.includes('FORT'))  return '⭐';
    if (badge.includes('WEEK'))  return '🔥';
    return '📈';
  }

  getStreakBarWidth(currentStreak: number): number {
    return Math.min((currentStreak / this.maxStreakInLeaderboard) * 100, 100);
  }

  getFatiguePercent(risk: number | null | undefined): string {
    return (((risk ?? 0) * 100).toFixed(0));
  }

  getWeeklyHistoryBar(history: number[] | null): { active: boolean }[] {
    if (!history) return Array(7).fill({ active: false });
    return history.map(v => ({ active: v === 1 }));
  }

  getConsistencyColor(score: number | null): string {
    const s = score ?? 0;
    if (s >= 80) return '#7fff6b';
    if (s >= 60) return '#ffd700';
    if (s >= 40) return '#ff6b35';
    return '#ff3b5c';
  }

  getTrendIcon(trend: string | null): string {
    if (trend === 'UP') return '📈';
    if (trend === 'DOWN') return '📉';
    return '➡️';
  }
}