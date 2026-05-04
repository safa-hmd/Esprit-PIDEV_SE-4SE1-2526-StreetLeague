import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { StatsService, DashboardStatsDTO, TopProduitDTO, LivreurPerfDTO } from '../../services/stats.service';

declare const Chart: any;

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {

  stats: DashboardStatsDTO | null = null;
  loading = false;
  errorMsg = '';

  periodes = [
    { label: '7 jours',  value: 7 },
    { label: '30 jours', value: 30 },
    { label: '90 jours', value: 90 },
  ];
  selectedJours = 30;

  private chartCA: any = null;
  private chartStatuts: any = null;
  private chartProduits: any = null;

  constructor(private statsService: StatsService) {}

  ngOnInit(): void {
    this.loadChartJs().then(() => this.loadDashboard());
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.destroyCharts();
  }

  private loadChartJs(): Promise<void> {
    return new Promise((resolve) => {
      if ((window as any).Chart) { resolve(); return; }
      const script = document.createElement('script');
      script.src = 'https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js';
      script.onload = () => resolve();
      script.onerror = () => resolve();
      document.head.appendChild(script);
    });
  }

  loadDashboard(): void {
    this.loading = true;
    this.errorMsg = '';
    this.destroyCharts();

    this.statsService.getDashboard(this.selectedJours).subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
        setTimeout(() => this.renderCharts(), 100);
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Impossible de charger les statistiques.';
      }
    });
  }

  onPeriodeChange(jours: number): void {
    this.selectedJours = jours;
    this.loadDashboard();
  }

  private renderCharts(): void {
    if (!this.stats) return;
    this.renderChartCA();
    this.renderChartStatuts();
    this.renderChartProduits();
  }

  private renderChartCA(): void {
    const canvas = document.getElementById('chartCA') as HTMLCanvasElement;
    if (!canvas || !this.stats) return;

    const labels = this.stats.evolutionCA.map(d => {
      const [, m, j] = d.date.split('-');
      return `${j}/${m}`;
    });
    const dataCA = this.stats.evolutionCA.map(d => d.ca);
    const dataNb = this.stats.evolutionCA.map(d => d.nbCommandes);

    this.chartCA = new Chart(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'CA (TND)',
            data: dataCA,
            borderColor: '#e61920',
            backgroundColor: 'rgba(230,25,32,0.08)',
            borderWidth: 2,
            fill: true,
            tension: 0.4,
            pointRadius: 3,
            pointBackgroundColor: '#e61920',
            yAxisID: 'y'
          },
          {
            label: 'Nb commandes',
            data: dataNb,
            borderColor: '#3b82f6',
            backgroundColor: 'transparent',
            borderWidth: 1.5,
            borderDash: [4, 3],
            pointRadius: 2,
            tension: 0.4,
            yAxisID: 'y2'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: { labels: { color: '#aaa', font: { size: 12 } } },
          tooltip: {
            callbacks: {
              label: (ctx: any) => {
                if (ctx.datasetIndex === 0) return ` ${ctx.parsed.y.toFixed(2)} TND`;
                return ` ${ctx.parsed.y} commandes`;
              }
            }
          }
        },
        scales: {
          x: { ticks: { color: '#888', maxTicksLimit: 10 }, grid: { color: 'rgba(255,255,255,0.05)' } },
          y: { position: 'left', ticks: { color: '#e61920', callback: (v: any) => `${v} TND` }, grid: { color: 'rgba(255,255,255,0.05)' } },
          y2: { position: 'right', ticks: { color: '#3b82f6' }, grid: { display: false } }
        }
      }
    });
  }

  private renderChartStatuts(): void {
    const canvas = document.getElementById('chartStatuts') as HTMLCanvasElement;
    if (!canvas || !this.stats) return;

    const s = this.stats;
    this.chartStatuts = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: ['Préparées', 'Assignées', 'En transit', 'Livrées', 'Échecs'],
        datasets: [{
          data: [
            s.livraisonsPreparees,
            s.livraisonsAssignees,
            s.livraisonsExpediees,
            s.livraisonsLivrees,
            s.livraisonsEchecs
          ],
          backgroundColor: ['#6b7280', '#f59e0b', '#3b82f6', '#22c55e', '#ef4444'],
          borderWidth: 0,
          hoverOffset: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '65%',
        plugins: {
          legend: { position: 'bottom', labels: { color: '#aaa', font: { size: 11 }, padding: 12 } }
        }
      }
    });
  }

  private renderChartProduits(): void {
    const canvas = document.getElementById('chartProduits') as HTMLCanvasElement;
    if (!canvas || !this.stats) return;

    const top = this.stats.topProduits.slice(0, 8);

    this.chartProduits = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: top.map(p => p.nom.length > 20 ? p.nom.substring(0, 20) + '…' : p.nom),
        datasets: [
          {
            label: 'Quantité vendue',
            data: top.map(p => p.quantiteVendue),
            backgroundColor: top.map(p =>
              p.stockActuel < 5 ? 'rgba(239,68,68,0.7)' : 'rgba(230,25,32,0.65)'
            ),
            borderColor: top.map(p =>
              p.stockActuel < 5 ? '#ef4444' : '#e61920'
            ),
            borderWidth: 1,
            borderRadius: 4,
            yAxisID: 'y'
          },
          {
            label: 'CA généré (TND)',
            data: top.map(p => p.caGenere),
            backgroundColor: 'rgba(59,130,246,0.35)',
            borderColor: '#3b82f6',
            borderWidth: 1,
            borderRadius: 4,
            yAxisID: 'y2'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#aaa', font: { size: 12 } } },
          tooltip: {
            callbacks: {
              afterLabel: (ctx: any) => {
                const p = top[ctx.dataIndex];
                return `Stock actuel : ${p.stockActuel} unités${p.stockActuel < 5 ? ' ⚠' : ''}`;
              }
            }
          }
        },
        scales: {
          x: { ticks: { color: '#aaa', font: { size: 11 } }, grid: { color: 'rgba(255,255,255,0.04)' } },
          y: { ticks: { color: '#e61920' }, grid: { color: 'rgba(255,255,255,0.05)' } },
          y2: { position: 'right', ticks: { color: '#3b82f6', callback: (v: any) => `${v} TND` }, grid: { display: false } }
        }
      }
    });
  }

  private destroyCharts(): void {
    [this.chartCA, this.chartStatuts, this.chartProduits].forEach(c => {
      if (c) { c.destroy(); }
    });
    this.chartCA = this.chartStatuts = this.chartProduits = null;
  }

  // ──────────────────────────────────────────────────────────
  // Getters / Helpers pour le template
  // ──────────────────────────────────────────────────────────

  get totalLivraisons(): number {
    if (!this.stats) return 0;
    return this.stats.livraisonsPreparees + this.stats.livraisonsAssignees +
           this.stats.livraisonsExpediees + this.stats.livraisonsLivrees + this.stats.livraisonsEchecs;
  }

  getNiveauClass(niveau: string): string {
    return niveau === 'excellent' ? 'perf-excellent'
         : niveau === 'bon'       ? 'perf-bon'
         : 'perf-faible';
  }

  getBarWidth(taux: number): string {
    return `${Math.min(taux, 100).toFixed(0)}%`;
  }

  getBubbleSize(pct: number): number {
    return Math.max(28, Math.sqrt(pct / 100) * 160);
  }

  getBubbleColor(produit: TopProduitDTO): string {
    if (produit.stockActuel < 5) return '#ef4444';
    if (produit.stockActuel < 15) return '#f59e0b';
    return '#e61920';
  }

  /** Vérifie si tous les produits ont un stock >= 15 */
  get isAllStockSufficient(): boolean {
    return this.stats?.topProduits?.every(p => p.stockActuel >= 15) ?? false;
  }

  /** Retourne les 8 premiers produits (pour les bulles) */
  get topProduitsSlice(): TopProduitDTO[] {
    return this.stats?.topProduits?.slice(0, 8) ?? [];
  }

  /** Vérifie si le stock d’un produit est critique (<15) */
  isStockCritique(produit: TopProduitDTO): boolean {
    return produit.stockActuel < 15;
  }
}