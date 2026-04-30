// src/app/frontoffice/performance-prediction/performance-prediction.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, forkJoin } from 'rxjs';
import {
  PerformanceStreakService,
  PlayerStatsDto
} from 'src/app/services/performance-streak.service';

export interface PredictionGroup {
  level: string;
  icon: string;
  color: string;
  players: PlayerStatsDto[];
  description: string;
}

@Component({
  selector: 'app-performance-prediction',
  templateUrl: './performance-prediction.component.html',
  styleUrls: ['./performance-prediction.component.css']
})
export class PerformancePredictionComponent implements OnInit, OnDestroy {

  predictions: PlayerStatsDto[]   = [];
  elitePlayers: PlayerStatsDto[]  = [];
  proPLayers: PlayerStatsDto[]    = [];
  interPlayers: PlayerStatsDto[]  = [];
  beginPlayers: PlayerStatsDto[]  = [];

  groups: PredictionGroup[] = [];
  loading = false;
  error: string | null = null;

  // Vue : 'cards' | 'table'
  viewMode: 'cards' | 'table' = 'cards';

  private destroy$ = new Subject<void>();

  constructor(private service: PerformanceStreakService) {}

  ngOnInit(): void { this.loadData(); }
  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  loadData(): void {
    this.loading = true;
    this.error   = null;

    forkJoin({
      predictions: this.service.getPerformancePredictions(),
      elite:       this.service.getPlayersByLevel('ELITE'),
      pro:         this.service.getPlayersByLevel('PRO'),
      inter:       this.service.getPlayersByLevel('INTERMEDIATE'),
      begin:       this.service.getPlayersByLevel('BEGINNER'),
    })
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: ({ predictions, elite, pro, inter, begin }) => {
        this.predictions  = predictions;
        this.elitePlayers = elite;
        this.proPLayers   = pro;
        this.interPlayers = inter;
        this.beginPlayers = begin;

        this.groups = [
          {
            level: 'ELITE',
            icon: '👑',
            color: '#ffd700',
            players: elite,
            description: '≥100 pts | ≥21j streak | ≥70% consistance'
          },
          {
            level: 'PRO',
            icon: '⭐',
            color: '#7ec8ff',
            players: pro,
            description: '≥50 pts | ≥10j streak | ≥50% consistance'
          },
          {
            level: 'INTERMEDIATE',
            icon: '🔵',
            color: '#7fff6b',
            players: inter,
            description: '≥20 pts | ≥5j streak | ≥30% consistance'
          },
          {
            level: 'BEGINNER',
            icon: '🟢',
            color: '#aaa',
            players: begin,
            description: 'En cours de progression'
          }
        ];

        this.loading = false;
      },
      error: () => {
        this.error   = 'Impossible de charger les prédictions.';
        this.loading = false;
      }
    });
  }

  getProbabilityClass(prob: number | null): string {
    const p = prob ?? 0;
    if (p >= 70) return 'prob-high';
    if (p >= 40) return 'prob-medium';
    return 'prob-low';
  }

  getProbabilityLabel(prob: number | null): string {
    const p = prob ?? 0;
    if (p >= 70) return '✅ Très probable';
    if (p >= 40) return '⚠️ Incertain';
    return '❌ Peu probable';
  }

  getPredictedChange(current: number, predicted: number | null): string {
    const p = predicted ?? 0;
    const diff = p - current;
    if (diff > 0) return `+${diff}`;
    if (diff < 0) return `${diff}`;
    return '±0';
  }

  getPredictedChangeClass(current: number, predicted: number | null): string {
    const p = predicted ?? 0;
    const diff = p - current;
    if (diff > 0) return 'change-up';
    if (diff < 0) return 'change-down';
    return 'change-stable';
  }

  getLevelColor(level: string | null): string {
    const c: Record<string, string> = {
      'ELITE': '#ffd700', 'PRO': '#7ec8ff',
      'INTERMEDIATE': '#7fff6b', 'BEGINNER': '#aaa'
    };
    return c[level ?? ''] ?? '#aaa';
  }

  getAverageProb(): string {
    if (this.predictions.length === 0) return '0';
    const avg = this.predictions.reduce(
      (sum, p) => sum + (p.streakContinuationProbability ?? 0), 0
    ) / this.predictions.length;
    return avg.toFixed(1);
  }

  getRisingStars(): PlayerStatsDto[] {
    return this.predictions
      .filter(p =>
        (p.streakContinuationProbability ?? 0) >= 60 &&
        (p.performanceLevel === 'INTERMEDIATE' || p.performanceLevel === 'BEGINNER')
      )
      .slice(0, 3);
  }
}
