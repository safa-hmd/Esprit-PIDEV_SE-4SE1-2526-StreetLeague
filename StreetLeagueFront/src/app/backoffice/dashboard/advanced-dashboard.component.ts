import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { 
  AdvancedStatsService, 
  CommunauteStatsDTO, 
  TopCommunauteDTO, 
  EvenementSansSponsoringDTO, 
  ComparaisonSponsorDTO, 
  DashboardSponsorCommunauteDTO 
} from '../../services/advanced-stats.service';

@Component({
  selector: 'app-advanced-dashboard',
  templateUrl: './advanced-dashboard.component.html',
  styleUrls: ['./advanced-dashboard.component.css']
})
export class AdvancedDashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('contributionChart') contributionChartRef!: ElementRef;
  @ViewChild('topCommunauteChart') topCommunauteChartRef!: ElementRef;
  @ViewChild('sponsorRadarChart') sponsorRadarChartRef!: ElementRef;

  contributionChart: any;
  topCommunauteChart: any;
  sponsorRadarChart: any;

  // Données brutes
  communautesStats: CommunauteStatsDTO[] = [];
  topCommunautes: TopCommunauteDTO[] = [];
  evenementsSansSponsoring: EvenementSansSponsoringDTO[] = [];
  comparaisonSponsors: ComparaisonSponsorDTO[] = [];
  dashboardSponsorCommunaute: DashboardSponsorCommunauteDTO[] = [];

  // KPIs
  totalSponsoringsGlobal = 0;
  totalEvenementsSansSponsoring = 0;

  constructor(private statsService: AdvancedStatsService) {}

  ngOnInit(): void {
    this.loadAllStats();
  }

  ngAfterViewInit(): void {
    // Les graphiques seront initialisés une fois les données chargées
  }

  loadAllStats() {
    // 1. Contribution totale par community
    this.statsService.getContributionTotaleParCommunaute().subscribe(data => {
      this.communautesStats = data;
      this.initContributionChart();
    });

    // 2. Top Communities (seuil = 1)
    this.statsService.getTopCommunautesAvecSponsorings(undefined, 1).subscribe(data => {
      this.topCommunautes = data;
      this.initTopCommunauteChart();
    });

    // 3. Events sans sponsoring
    this.statsService.getEvenementsSansSponsoring().subscribe(data => {
      this.evenementsSansSponsoring = data;
      this.totalEvenementsSansSponsoring = data.length;
    });

    // 4. Comparaison Sponsors
    this.statsService.getComparaisonContractsVsSponsorings().subscribe(data => {
      this.comparaisonSponsors = data;
      this.initSponsorRadarChart();
    });

    // 5. Dashboard Complet
    this.statsService.getDashboardSponsorParCommunaute().subscribe(data => {
      this.dashboardSponsorCommunaute = data;
      this.totalSponsoringsGlobal = data.reduce((sum, item) => sum + (item.totalContribution || 0), 0);
    });
  }

  // --- INITIALISATION DES GRAPHIQUES ---

  initContributionChart() {
    if (this.contributionChart) this.contributionChart.destroy();
    
    const ctx = this.contributionChartRef.nativeElement.getContext('2d');
    const labels = this.communautesStats.map(c => c.communauteNom);
    const data = this.communautesStats.map(c => c.totalContribution);

    this.contributionChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Total Contributions ($)',
          data: data,
          backgroundColor: 'rgba(59, 130, 246, 0.7)',
          borderColor: 'rgba(59, 130, 246, 1)',
          borderWidth: 1,
          borderRadius: 8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          title: { display: true, text: 'Contributions par Community' }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }

  initTopCommunauteChart() {
    if (this.topCommunauteChart) this.topCommunauteChart.destroy();

    const ctx = this.topCommunauteChartRef.nativeElement.getContext('2d');
    const labels = this.topCommunautes.map(c => c.communauteNom);
    const data = this.topCommunautes.map(c => c.nombreSponsorings);

    this.topCommunauteChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          label: 'Namebre de Sponsorings',
          data: data,
          backgroundColor: [
            'rgba(16, 185, 129, 0.7)',
            'rgba(245, 158, 11, 0.7)',
            'rgba(239, 68, 68, 0.7)',
            'rgba(139, 92, 246, 0.7)',
            'rgba(236, 72, 153, 0.7)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'right' },
          title: { display: true, text: 'Répartition des Sponsorings' }
        }
      }
    });
  }

  initSponsorRadarChart() {
    if (this.sponsorRadarChart) this.sponsorRadarChart.destroy();

    const ctx = this.sponsorRadarChartRef.nativeElement.getContext('2d');
    const topSponsors = this.comparaisonSponsors.slice(0, 5);
    
    const labels = topSponsors.map(s => s.sponsorNom);
    const contractsData = topSponsors.map(s => s.totalContrats);
    const sponsoringsData = topSponsors.map(s => s.totalSponsorings);

    this.sponsorRadarChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Total Contracts ($)',
            data: contractsData,
            backgroundColor: 'rgba(59, 130, 246, 0.7)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 1
          },
          {
            label: 'Total Events ($)',
            data: sponsoringsData,
            backgroundColor: 'rgba(16, 185, 129, 0.7)',
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }

  formatDate(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}




