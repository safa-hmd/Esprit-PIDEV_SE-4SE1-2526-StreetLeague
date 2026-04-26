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

  leaderboard: PlayerStatsDto[]   = [];
  fatigueAlerts: PlayerStatsDto[] = [];
  currentPlayerStats: PlayerStatsDto | null = null;
  momentumData: PlayerStatsDto[]  = [];

  maxStreakInLeaderboard = 30;

  loading = false;
  checkinLoading = false;
  error: string | null = null;
  checkinType = 'TRAINING';

  // Notification in-page (remplace alert())
  notification: { message: string; type: 'success' | 'error' } | null = null;
  private notifTimeout: any = null;

  private destroy$ = new Subject<void>();

  constructor(private streakService: PerformanceStreakService) {}

  ngOnInit(): void { this.loadAllData(); }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['playerId'] && this.playerId > 0) {
      this.loadPlayerStats();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.notifTimeout) clearTimeout(this.notifTimeout);
  }

  // ── Notification in-page ─────────────────────────────────────────
  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    if (this.notifTimeout) clearTimeout(this.notifTimeout);
    this.notifTimeout = setTimeout(() => {
      this.notification = null;
    }, 4000);
  }

  // ── Chargement des données ───────────────────────────────────────
  loadAllData(): void {
    this.loading = true;
    this.error   = null;

    forkJoin({
      leaderboard: this.streakService.getGlobalLeaderboard(),
      alerts:      this.streakService.getGlobalFatigueAlerts(),
      momentum:    this.streakService.getGlobalMomentum()
    })
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: ({ leaderboard, alerts, momentum }) => {
        this.leaderboard   = leaderboard;
        this.fatigueAlerts = alerts;
        this.momentumData  = momentum;
        this.maxStreakInLeaderboard = leaderboard.length > 0
          ? Math.max(...leaderboard.map(p => p.bestStreak ?? p.currentStreak ?? 0), 1)
          : 30;
        this.loading = false;
        this.loadPlayerStats();
      },
      error: (err) => {
        console.error('Error loading streak data', err);
        this.error   = 'Impossible de charger les données. Vérifiez votre connexion.';
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

  // ── Check-in (peut être appelé plusieurs fois par jour) ──────────
  doCheckin(): void {
    const playerId = Number(localStorage.getItem('UserIdConnect')) || 0;
    if (!playerId) {
      this.showNotification('⚠️ Veuillez vous connecter pour continuer.', 'error');
      return;
    }

    if (this.checkinLoading) return; // évite double-clic
    this.checkinLoading = true;

    this.streakService.checkin(playerId, this.checkinType)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.checkinLoading = false;

          const badge = result.badge ? ` 🏆 ${result.badge}` : '';
          const fatigue = result.fatigueRisk != null
            ? ` | Fatigue: ${((result.fatigueRisk) * 100).toFixed(0)}%`
            : '';
          this.showNotification(
            `✅ Check-in enregistré ! Streak: ${result.currentStreak} jours${badge}${fatigue}`,
            'success'
          );

          // Recalcule les stats immédiatement
          this.currentPlayerStats = result;
          // Recharge leaderboard + alertes
          this.loadAllData();
        },
        error: (err) => {
          this.checkinLoading = false;
          const msg = err.error?.message || err.message || 'Erreur inconnue';
          this.showNotification(`❌ Erreur: ${msg}`, 'error');
        }
      });
  }

  // ── Utilitaires d'affichage ──────────────────────────────────────
  getRankClass(rank: number): string {
    if (rank === 1) return 'top1';
    if (rank === 2) return 'top2';
    if (rank === 3) return 'top3';
    return 'default';
  }

  getRiskColor(riskLevel: string): string {
    const colors: Record<string, string> = {
      'CRITICAL': '#ff3b5c', 'HIGH': '#ff6b35',
      'MODERATE': '#ffd700', 'LOW':  '#7fff6b'
    };
    return colors[riskLevel] ?? '#7fff6b';
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      'OPTIMAL': 'status-optimal', 'ACTIF': 'status-actif'
    };
    return classes[status] ?? 'status-faible';
  }

  getBadgeIcon(badge: string | null): string {
    if (!badge)                  return '⚪';
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
}