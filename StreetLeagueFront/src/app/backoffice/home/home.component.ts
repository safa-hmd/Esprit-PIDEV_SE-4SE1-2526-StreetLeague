import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { TeamService } from 'src/app/services/team.service';
import { MatchService } from 'src/app/services/match.service';
import { TrainingService } from 'src/app/services/training.service';
import { Team } from 'src/app/models/team.model';
import { MatchResponse } from 'src/app/models/match.model';
import { TrainingResponse } from 'src/app/models/training.model';
import {
  FinancialSummary,
  RevenueByField,
  RevenueBySport,
  RevenueByMonth,
  TopPlayer
} from 'src/app/models/dashboard.model';
import { DashboardService } from 'src/app/services/dashboard.service';


import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {

  //── ViewChild safa ────────────────────────────────────
  @ViewChild('matchStatusChart') matchStatusRef!: ElementRef;
  @ViewChild('trainingStatusChart') trainingStatusRef!: ElementRef;
  @ViewChild('sportChart') sportChartRef!: ElementRef;
  @ViewChild('playerChart') playerChartRef!: ElementRef;

    // ── ViewChild Sara ────────────────────────────────────────
  @ViewChild('revenueByFieldChart') revenueByFieldRef!: ElementRef;
  @ViewChild('revenueBySportChart') revenueBySportRef!: ElementRef;
  @ViewChild('revenueByDayChart') revenueByMonthRef!: ElementRef;

  // ── Stats safa─────────────────────────────────────────────────
  totalTeams     = 0;
  totalMatches   = 0;
  totalTrainings = 0;
  totalPlayers   = 0;

    // ── Stats Sara ────────────────────────────────────────────
  financialSummary: FinancialSummary = {
    totalRevenue: 0,
    totalPayments: 0,
    totalRefunds: 0,
    pendingCount: 0
  };
  revenueByField:  RevenueByField[]  = [];
  revenueBySport:  RevenueBySport[]  = [];
  revenueByMonth:  RevenueByMonth[]  = [];
  topPlayers:      TopPlayer[]       = [];

  // ── Recent Data ───────────────────────────────────────────
  recentMatches:   MatchResponse[]    = [];
  recentTrainings: TrainingResponse[] = [];
  topTeams:        Team[]             = [];

  // ── Raw data for charts ───────────────────────────────────
  private allMatches:   MatchResponse[]    = [];
  private allTrainings: TrainingResponse[] = [];
  private allTeams:     Team[]             = [];

  // ── Loading ───────────────────────────────────────────────
  isLoadingTeams     = false;
  isLoadingMatches   = false;
  isLoadingTrainings = false;
  isLoadingDashboard  = false;

  dataReady = false;
  adminName = '';

  private charts: Chart[] = [];

  constructor(
    private router: Router,
    private teamService: TeamService,
    private matchService: MatchService,
    private trainingService: TrainingService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.adminName = localStorage.getItem('EmailUserConnect') || 'Admin';
    this.loadAllData();
  }

  ngAfterViewInit(): void {}

  // ── Load All Data ─────────────────────────────────────────
  loadAllData(): void {
    this.isLoadingTeams = true;
    this.isLoadingMatches = true;
    this.isLoadingTrainings = true;
    this.isLoadingDashboard = true;

    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.allTeams     = data;
        this.totalTeams   = data.length;
        this.totalPlayers = data.reduce((s, t) => s + (t.playerCount || 0), 0);
        this.topTeams     = data.slice(0, 6);
        this.isLoadingTeams = false;
        this.checkReady();
      },
      error: () => { this.isLoadingTeams = false; this.checkReady(); }
    });

    this.matchService.getAllMatches().subscribe({
      next: (data) => {
        this.allMatches    = data;
        this.totalMatches  = data.length;
        this.recentMatches = data.slice(0, 5);
        this.isLoadingMatches = false;
        this.checkReady();
      },
      error: () => { this.isLoadingMatches = false; this.checkReady(); }
    });

    this.trainingService.getAllTrainings().subscribe({
      next: (data) => {
        this.allTrainings    = data;
        this.totalTrainings  = data.length;
        this.recentTrainings = data.slice(0, 4);
        this.isLoadingTrainings = false;
        this.checkReady();
      },
      error: () => { this.isLoadingTrainings = false; this.checkReady(); }
    });
        // ── Dashboard financier ───────────────────────────────
    this.dashboardService.getSummary().subscribe({
      next: (data) => { this.financialSummary = data; },
      error: () => {}
    });
        this.dashboardService.getRevenueByField().subscribe({
      next: (data) => { this.revenueByField = data; },
      error: () => {}
    });

    this.dashboardService.getRevenueBySport().subscribe({
      next: (data) => { this.revenueBySport = data; },
      error: () => {}
    });

    this.dashboardService.getRevenueByMonth().subscribe({
      next: (data) => { this.revenueByMonth = data; },
      error: () => {}
    });

    this.dashboardService.getTopPlayers().subscribe({
      next: (data) => {
        this.topPlayers = data;
        this.isLoadingDashboard = false;
        this.checkReady();
      },
      error: () => { this.isLoadingDashboard = false; this.checkReady(); }
    });

    
  }

  private checkReady(): void {
    if (!this.isLoadingTeams && !this.isLoadingMatches && !this.isLoadingTrainings) {
      this.dataReady = true;
      setTimeout(() => this.buildCharts(), 100);
    }
  }

  // ── Build All Charts ──────────────────────────────────────
  private buildCharts(): void {
    this.charts.forEach(c => c.destroy());
    this.charts = [];
    //safa
    this.buildMatchStatusChart();
    this.buildTrainingStatusChart();
    this.buildSportChart();
    this.buildPlayerChart();
        // Sara
    this.buildRevenueByFieldChart();
    this.buildRevenueBySportChart();
    this.buildRevenueByMonthChart();
  }

    // ── Charts safaa ───────────────────────────
  private buildMatchStatusChart(): void {
    const counts = {
      SCHEDULED: this.allMatches.filter(m => m.statut === 'SCHEDULED').length,
      ONGOING:   this.allMatches.filter(m => m.statut === 'ONGOING').length,
      FINISHED:  this.allMatches.filter(m => m.statut === 'FINISHED').length,
      CANCELLED: this.allMatches.filter(m => m.statut === 'CANCELLED').length,
    };
    const ctx = this.matchStatusRef?.nativeElement;
    if (!ctx) return;
    this.charts.push(new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Scheduled', 'Ongoing', 'Finished', 'Cancelled'],
        datasets: [{
          data: Object.values(counts),
          backgroundColor: ['#4e8a9f', '#e87040', '#4ade80', '#f87171'],
          borderWidth: 0,
        }]
      },
      options: {
        responsive: true,
        cutout: '70%',
        plugins: {
          legend: { position: 'bottom', labels: { color: '#aaa', font: { size: 11 } } }
        }
      }
    }));
  }

  private buildTrainingStatusChart(): void {
    const counts = {
      PLANNED:   this.allTrainings.filter(t => t.statut === 'PLANNED').length,
      COMPLETED: this.allTrainings.filter(t => t.statut === 'COMPLETED').length,
      CANCELLED: this.allTrainings.filter(t => t.statut === 'CANCELLED').length,
    };
    const ctx = this.trainingStatusRef?.nativeElement;
    if (!ctx) return;
    this.charts.push(new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Planned', 'Completed', 'Cancelled'],
        datasets: [{
          label: 'Sessions',
          data: Object.values(counts),
          backgroundColor: ['#4e8a9f', '#4ade80', '#f87171'],
          borderRadius: 8,
          borderSkipped: false,
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: { ticks: { color: '#aaa' }, grid: { color: '#2a2a2a' } },
          y: { ticks: { color: '#aaa', stepSize: 1 }, grid: { color: '#2a2a2a' }, beginAtZero: true }
        }
      }
    }));
  }

  private buildSportChart(): void {
    const sportMap: Record<string, number> = {};
    this.allTeams.forEach(t => {
      const s = t.sport || 'Unknown';
      sportMap[s] = (sportMap[s] || 0) + 1;
    });
    const colors = ['#4e8a9f','#e87040','#4ade80','#f87171','#c2748a','#6b9ed2','#fbbf24'];
    const ctx = this.sportChartRef?.nativeElement;
    if (!ctx) return;
    this.charts.push(new Chart(ctx, {
      type: 'pie',
      data: {
        labels: Object.keys(sportMap),
        datasets: [{
          data: Object.values(sportMap),
          backgroundColor: colors.slice(0, Object.keys(sportMap).length),
          borderWidth: 0,
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom', labels: { color: '#aaa', font: { size: 11 } } }
        }
      }
    }));
  }

  private buildPlayerChart(): void {
    const top = [...this.allTeams]
      .sort((a, b) => (b.playerCount || 0) - (a.playerCount || 0))
      .slice(0, 6);
    const ctx = this.playerChartRef?.nativeElement;
    if (!ctx) return;
    this.charts.push(new Chart(ctx, {
      type: 'bar',
      data: {
        labels: top.map(t => t.nom),
        datasets: [{
          label: 'Players',
          data: top.map(t => t.playerCount || 0),
          backgroundColor: '#4e8a9f',
          borderRadius: 8,
          borderSkipped: false,
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: { ticks: { color: '#aaa', stepSize: 1 }, grid: { color: '#2a2a2a' }, beginAtZero: true },
          y: { ticks: { color: '#aaa' }, grid: { color: '#2a2a2a' } }
        }
      }
    }));
  }

    // ── Charts Sara ───────────────────────────────────────────
  private buildRevenueByFieldChart(): void {
  const ctx = this.revenueByFieldRef?.nativeElement;
  if (!ctx) return;
  const colors = ['#e63946', '#4e8a9f', '#4ade80', '#fbbf24', '#c2748a', '#e87040'];
  this.charts.push(new Chart(ctx, {
    type: 'bar',
    data: {
      labels: this.revenueByField.map(r => r.fieldName),
      datasets: [{
        label: 'Revenue (TND)',
        data: this.revenueByField.map(r => r.revenue),
        backgroundColor: this.revenueByField.map((_, i) => colors[i % colors.length]),
        borderRadius: 8,
        borderSkipped: false
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        x: { ticks: { color: '#aaa' }, grid: { color: '#2a2a2a' } },
        y: { ticks: { color: '#aaa' }, grid: { color: '#2a2a2a' }, beginAtZero: true }
      }
    }
  }));
}

  private buildRevenueBySportChart(): void {
    const ctx = this.revenueBySportRef?.nativeElement;
    if (!ctx) return;
    const colors = ['#e63946','#4e8a9f','#4ade80','#fbbf24','#c2748a','#e87040'];
    this.charts.push(new Chart(ctx, {
      type: 'pie',
      data: {
        labels: this.revenueBySport.map(r => r.sportType),
        datasets: [{
          data: this.revenueBySport.map(r => r.revenue),
          backgroundColor: colors.slice(0, this.revenueBySport.length),
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { position: 'bottom', labels: { color: '#aaa', font: { size: 11 } } } }
      }
    }));
  }

  private buildRevenueByMonthChart(): void {
    const ctx = this.revenueByMonthRef?.nativeElement;
    if (!ctx) return;
    this.charts.push(new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.revenueByMonth.map(r => r.month),  // ← déjà "2026-04-21" après fix backend
        datasets: [{
          label: 'Revenue by Day (TND)',  // ✅ changer le label
          data: this.revenueByMonth.map(r => r.revenue),
          borderColor: '#e63946',
          backgroundColor: 'rgba(230,57,70,0.1)',
          pointBackgroundColor: '#e63946',
          tension: 0.4,
          fill: true
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: { ticks: { color: '#aaa' }, grid: { color: '#2a2a2a' } },
          y: { ticks: { color: '#aaa' }, grid: { color: '#2a2a2a' }, beginAtZero: true }
        }
      }
    }));
  }

  // ── Navigation ────────────────────────────────────────────
  goTo(path: string): void { this.router.navigate([path]); }

  // ── Helpers ───────────────────────────────────────────────
  getMatchStatusClass(statut: string): string {
    switch (status) {
      case 'SCHEDULED': return 'badge-scheduled';
      case 'ONGOING':   return 'badge-ongoing';
      case 'FINISHED':  return 'badge-finished';
      case 'CANCELLED': return 'badge-cancelled';
      default:          return 'badge-default';
    }
  }

  getTrainingStatusClass(statut: string): string {
    switch (status) {
      case 'PLANNED':   return 'badge-scheduled';
      case 'COMPLETED': return 'badge-finished';
      case 'CANCELLED': return 'badge-cancelled';
      default:          return 'badge-default';
    }
  }

  getProgressColor(count: number): string {
    const pct = Math.round((count / 25) * 100);
    if (pct >= 100) return '#f87171';
    if (pct >= 75)  return '#e87040';
    return '#4e8a9f';
  }
}


