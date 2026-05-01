// coach-anomaly-dashboard.component.ts
import {
  Component, OnInit, OnDestroy,
  ViewChild, ElementRef, AfterViewChecked
} from '@angular/core';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
  PerformanceStreakService,
  PlayerStatsDto
} from 'src/app/services/performance-streak.service';

// Chart.js — déjà installé dans la plupart des projets Angular.
// Si ce n'est pas le cas : npm install chart.js
import {
  Chart,
  ArcElement, DoughnutController,
  BarElement, BarController, CategoryScale, LinearScale,
  Legend, Tooltip
} from 'chart.js';

Chart.register(
  ArcElement, DoughnutController,
  BarElement, BarController, CategoryScale, LinearScale,
  Legend, Tooltip
);

@Component({
  selector: 'app-coach-anomaly-dashboard',
  templateUrl: './coach-anomaly-dashboard.component.html',
  styleUrls: ['./coach-anomaly-dashboard.component.css']
})
export class CoachAnomalyDashboardComponent
  implements OnInit, OnDestroy, AfterViewChecked {

  // ── Canvas refs pour les graphiques ────────────────────────────────────
  @ViewChild('sevChart')  sevChartRef!:  ElementRef<HTMLCanvasElement>;
  @ViewChild('acwrChart') acwrChartRef!: ElementRef<HTMLCanvasElement>;

  private chartSev:  Chart | null = null;
  private chartAcwr: Chart | null = null;
  private chartsRendered = false;

  // ── Données ────────────────────────────────────────────────────────────
  anomalyAlerts: PlayerStatsDto[]  = [];
  acwrRanking:   PlayerStatsDto[]  = [];
  filteredAlerts: PlayerStatsDto[] = [];

  // ── État UI ────────────────────────────────────────────────────────────
  activeTab: 'anomalies' | 'acwr' | 'chart' = 'anomalies';
  selectedSeverity: 'ALL' | 'CRITICAL' | 'HIGH' | 'MEDIUM' = 'ALL';

  loading = false;
  error: string | null = null;

  // ── Compteurs anomalies ────────────────────────────────────────────────
  criticalCount = 0;
  highCount     = 0;
  mediumCount   = 0;

  // ── Compteurs ACWR ─────────────────────────────────────────────────────
  acwrDangerCount  = 0;
  acwrWarningCount = 0;
  acwrOptimalCount = 0;
  acwrUnderload    = 0;

  private destroy$ = new Subject<void>();

  constructor(private service: PerformanceStreakService) {}

  ngOnInit(): void { this.loadData(); }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.chartSev?.destroy();
    this.chartAcwr?.destroy();
  }

  // ── Rendu des graphiques après que le DOM est prêt ─────────────────────
  ngAfterViewChecked(): void {
    if (this.activeTab === 'chart' && !this.chartsRendered
        && this.sevChartRef && this.acwrChartRef) {
      this.renderCharts();
      this.chartsRendered = true;
    }
    if (this.activeTab !== 'chart') {
      this.chartsRendered = false;
    }
  }

  // ══════════════════════════════════════════════════════════════════════
  //  CHARGEMENT DES DONNÉES
  // ══════════════════════════════════════════════════════════════════════

  loadData(): void {
    this.loading = true;
    this.error   = null;

    forkJoin({
      anomalies: this.service.getAnomalyAlerts(),
      acwr:      this.service.getAcwrRanking()
    })
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: ({ anomalies, acwr }) => {
        this.anomalyAlerts = anomalies;
        this.acwrRanking   = acwr;

        // Compteurs anomalies
        this.criticalCount = anomalies.filter(p => p.anomalySeverity === 'CRITICAL').length;
        this.highCount     = anomalies.filter(p => p.anomalySeverity === 'HIGH').length;
        this.mediumCount   = anomalies.filter(p => p.anomalySeverity === 'MEDIUM').length;

        // Compteurs ACWR
        this.acwrDangerCount  = acwr.filter(p => p.acwrZone === 'DANGER').length;
        this.acwrWarningCount = acwr.filter(p => p.acwrZone === 'WARNING').length;
        this.acwrOptimalCount = acwr.filter(p => p.acwrZone === 'OPTIMAL').length;
        this.acwrUnderload    = acwr.filter(p => p.acwrZone === 'UNDERLOAD').length;

        this.applyFilter();
        this.loading = false;
      },
      error: () => {
        this.error   = 'Impossible de charger les données d\'analyse.';
        this.loading = false;
      }
    });
  }

  // ══════════════════════════════════════════════════════════════════════
  //  FILTRES & TABS
  // ══════════════════════════════════════════════════════════════════════

  applyFilter(): void {
    this.filteredAlerts = this.selectedSeverity === 'ALL'
      ? this.anomalyAlerts
      : this.anomalyAlerts.filter(p => p.anomalySeverity === this.selectedSeverity);
  }

  setFilter(f: 'ALL' | 'CRITICAL' | 'HIGH' | 'MEDIUM'): void {
    this.selectedSeverity = f;
    this.applyFilter();
  }

  setTab(tab: 'anomalies' | 'acwr' | 'chart'): void {
    this.activeTab = tab;
    this.chartsRendered = false;
  }

  // ══════════════════════════════════════════════════════════════════════
  //  GRAPHIQUES CHART.JS
  // ══════════════════════════════════════════════════════════════════════

  private renderCharts(): void {
    this.chartSev?.destroy();
    this.chartAcwr?.destroy();

    // Donut — sévérités
    this.chartSev = new Chart(this.sevChartRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Critique', 'Élevé', 'Modéré'],
        datasets: [{
          data: [this.criticalCount, this.highCount, this.mediumCount],
          backgroundColor: ['#f87171', '#fbbf24', '#fb923c'],
          borderWidth: 0,
          borderRadius: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: { boxWidth: 12, font: { size: 12 }, color: '#6b6b72' }
          }
        }
      }
    });

    // Barres — zones ACWR
    this.chartAcwr = new Chart(this.acwrChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Danger', 'Surveillance', 'Optimal', 'Sous-chargé'],
        datasets: [{
          data: [
            this.acwrDangerCount,
            this.acwrWarningCount,
            this.acwrOptimalCount,
            this.acwrUnderload
          ],
          backgroundColor: ['#f87171', '#fbbf24', '#86efac', '#7dd3fc'],
          borderRadius: 4,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1, color: '#6b6b72' },
            grid:  { color: '#2e2e35' }
          },
          x: {
            ticks: { color: '#6b6b72' },
            grid:  { color: '#2e2e35' }
          }
        }
      }
    });
  }

  // ══════════════════════════════════════════════════════════════════════
  //  HELPERS AFFICHAGE
  // ══════════════════════════════════════════════════════════════════════

  getAnomalyTypeLabel(type: string | null): string {
    const labels: Record<string, string> = {
      'PERFORMANCE_DROP':           'Chute confirmée',
      'PERFORMANCE_DROP_SUSPECTED': 'Chute suspecte',
      'PERFORMANCE_SPIKE':          'Pic de perf.',
      'NORMAL':                     'Normal'
    };
    return labels[type ?? ''] ?? 'Inconnu';
  }

  getAcwrZoneLabel(zone: string | null): string {
    const labels: Record<string, string> = {
      'UNDERLOAD': 'Sous-entraînement',
      'OPTIMAL':   'Zone optimale',
      'WARNING':   'Surveillance',
      'DANGER':    'Danger'
    };
    return labels[zone ?? ''] ?? 'Inconnu';
  }

  formatZScore(z: number | null): string {
    if (z == null) return 'N/A';
    return (z >= 0 ? '+' : '') + z.toFixed(2);
  }

  formatEwmaDrop(drop: number | null): string {
    if (drop == null) return 'N/A';
    return Math.round(drop * 100) + '%';
  }

  formatAcwr(acwr: number | null): string {
    if (acwr == null) return 'N/A';
    return acwr.toFixed(2);
  }

  getAcwrBarWidth(acwr: number | null): number {
    // Normalise 0–2.0 en 0–100%
    return Math.min(100, ((acwr ?? 0) / 2.0) * 100);
  }

  getAcwrBarColor(zone: string | null): string {
    const colors: Record<string, string> = {
      'UNDERLOAD': '#7dd3fc',
      'OPTIMAL':   '#86efac',
      'WARNING':   '#fbbf24',
      'DANGER':    '#f87171'
    };
    return colors[zone ?? ''] ?? '#444450';
  }
}