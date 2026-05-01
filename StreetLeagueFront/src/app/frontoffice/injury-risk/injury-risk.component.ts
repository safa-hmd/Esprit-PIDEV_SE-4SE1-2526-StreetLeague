// src/app/frontoffice/injury-risk/injury-risk.component.ts
import { Component, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  PerformanceStreakService,
  PlayerStatsDto
} from 'src/app/services/performance-streak.service';

@Component({
  selector: 'app-injury-risk',
  templateUrl: './injury-risk.component.html',
  styleUrls: ['./Injury-risk.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class InjuryRiskComponent implements OnInit, OnDestroy {

  players: PlayerStatsDto[] = [];
  filteredPlayers: PlayerStatsDto[] = [];
  loading = false;
  error: string | null = null;
  selectedFilter: 'ALL' | 'CRITICAL' | 'HIGH' | 'MODERATE' | 'LOW' = 'ALL';

  // Stats résumées
  criticalCount  = 0;
  highCount      = 0;
  moderateCount  = 0;
  lowCount       = 0;

  private destroy$ = new Subject<void>();

  constructor(private service: PerformanceStreakService) {}

  ngOnInit(): void { this.loadData(); }
  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  loadData(): void {
    this.loading = true;
    this.error   = null;

    this.service.getInjuryRiskRanking()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.players  = data;
          this.criticalCount = data.filter(p => p.injuryRiskLevel === 'CRITICAL').length;
          this.highCount     = data.filter(p => p.injuryRiskLevel === 'HIGH').length;
          this.moderateCount = data.filter(p => p.injuryRiskLevel === 'MODERATE').length;
          this.lowCount      = data.filter(p => p.injuryRiskLevel === 'LOW').length;
          this.applyFilter();
          this.loading  = false;
        },
        error: () => {
          this.error   = 'Impossible de charger les données de risque.';
          this.loading = false;
        }
      });
  }

  applyFilter(): void {
    this.filteredPlayers = this.selectedFilter === 'ALL'
      ? this.players
      : this.players.filter(p => p.injuryRiskLevel === this.selectedFilter);
  }

  setFilter(f: 'ALL' | 'CRITICAL' | 'HIGH' | 'MODERATE' | 'LOW'): void {
    this.selectedFilter = f;
    this.applyFilter();
  }

  getRiskColor(level: string | null): string {
    const c: Record<string, string> = {
      'CRITICAL': '#ff3b5c', 'HIGH': '#ff6b35',
      'MODERATE': '#ffd700', 'LOW': '#7fff6b'
    };
    return c[level ?? ''] ?? '#7fff6b';
  }

  getRiskIcon(level: string | null): string {
    const c: Record<string, string> = {
      'CRITICAL': '🔴', 'HIGH': '🟠', 'MODERATE': '🟡', 'LOW': '🟢'
    };
    return c[level ?? ''] ?? '⚪';
  }

  getRecommendation(level: string | null, fatigueRisk: number | null,
                     workload: number | null, recovery: number | null): string {
    const f = (fatigueRisk ?? 0) * 100;
    const w = (workload ?? 0) * 100;
    const r = recovery ?? 0;

    if (level === 'CRITICAL') {
      return `⛔ REPOS OBLIGATOIRE : Fatigue ${f.toFixed(0)}%, charge ${w.toFixed(0)}%. Arrêt immédiat 3 jours minimum.`;
    }
    if (level === 'HIGH') {
      return `⚠️ RÉDUCTION REQUISE : Charge actuelle ${w.toFixed(0)}% — baisser l'intensité, repos 2j.`;
    }
    if (level === 'MODERATE') {
      return `ℹ️ SURVEILLER : Récupération ${r.toFixed(0)}% — maintenir 1 jour de repos/semaine.`;
    }
    return `✅ OPTIMAL : Continuez au rythme actuel. Récupération : ${r.toFixed(0)}%.`;
  }

  getWorkloadLabel(workload: number | null): string {
    const w = (workload ?? 0) * 100;
    if (w >= 80) return 'TRÈS ÉLEVÉE';
    if (w >= 60) return 'ÉLEVÉE';
    if (w >= 40) return 'MODÉRÉE';
    return 'FAIBLE';
  }
}
