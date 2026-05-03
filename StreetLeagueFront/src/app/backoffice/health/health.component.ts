import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { HealthDashboardService, WaterReminderResponse, UserHealthData, UserGoal, UserBadgeResponse, WaterLogResponse } from '../../services/healthdashboard.service';

Chart.register(...registerables);

@Component({
  selector: 'app-health',
  templateUrl: './health.component.html',
  styleUrls: ['./health.component.css']
})
export class HealthComponent implements OnInit, AfterViewInit {

  // Tab Management
  activeTab = 'hydration';
  selectedUserId: number | null = null;

  // Hydration Data
  reminders: WaterReminderResponse[] = [];
  filteredReminders: WaterReminderResponse[] = [];
  
  // Health Status Data
  allUsersHealth: UserHealthData[] = [];
  filteredUsersHealth: UserHealthData[] = [];
  
  // Goals Data
  userGoals: UserGoal[] = [];
  
  // Badges Data
  userBadges: UserBadgeResponse[] = [];
  
  // Water Logs Data
  waterLogs: WaterLogResponse[] = [];

  isLoading = true;

  private bmiChart?: Chart;
  private hydroChart?: Chart;
  private bmiDistributionChart?: Chart;

  constructor(private healthService: HealthDashboardService) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  ngAfterViewInit(): void {}

  loadAllData(): void {
    this.isLoading = true;
    this.healthService.getAllReminders().subscribe({
      next: (data) => {
        this.reminders = data;
        this.filteredReminders = [...data];
        this.isLoading = false;
        setTimeout(() => {
          this.initFreqChart();
          this.initQtyChart();
        }, 100);
      },
      error: (err) => {
        console.error('Erreur chargement reminders:', err);
        this.isLoading = false;
      }
    });

    this.healthService.getAllUsersHealth().subscribe({
      next: (data) => {
        this.allUsersHealth = data;
        this.filteredUsersHealth = [...data];
      },
      error: (err) => console.error('Erreur chargement health:', err)
    });
  }

  switchTab(tab: string): void {
    this.activeTab = tab;
    this.selectedUserId = null;
  }

  onSearchReminders(event: any): void {
    const q = event.target.value.toLowerCase();
    this.filteredReminders = this.reminders.filter(r =>
      r.userName?.toLowerCase().includes(q) ||
      r.userEmail?.toLowerCase().includes(q)
    );
  }

  onSearchHealth(event: any): void {
    const q = event.target.value.toLowerCase();
    this.filteredUsersHealth = this.allUsersHealth.filter(u =>
      u.fullName?.toLowerCase().includes(q) ||
      u.email?.toLowerCase().includes(q)
    );
  }

  selectUser(userId: number): void {
    this.selectedUserId = userId;
    this.loadUserDetails(userId);
  }

  loadUserDetails(userId: number): void {
    this.healthService.getUserGoals(userId).subscribe({
      next: (data) => this.userGoals = data,
      error: (err) => console.error('Erreur chargement goals:', err)
    });

    this.healthService.getUserBadges(userId).subscribe({
      next: (data) => this.userBadges = data,
      error: (err) => console.error('Erreur chargement badges:', err)
    });

    this.healthService.getWaterLogs(userId).subscribe({
      next: (data) => this.waterLogs = data,
      error: (err) => console.error('Erreur chargement water logs:', err)
    });
  }

  freqLabel(f: number): string {
    if (f < 60) return f + ' min';
    if (f === 60) return '1 heure';
    return (f / 60) + ' heures';
  }

  getBmiStatus(bmi: number): string {
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getBmiStatusColor(bmi: number): string {
    if (bmi < 18.5) return '#3B82F6';
    if (bmi < 25) return '#1D9E75';
    if (bmi < 30) return '#EF9F27';
    return '#E61920';
  }

  getSelectedUser(): UserHealthData | undefined {
    return this.selectedUserId ? this.allUsersHealth.find(u => u.userId === this.selectedUserId) : undefined;
  }

  get totalUsers(): number {
    return this.reminders.length;
  }

  get activeReminders(): number {
    return this.reminders.filter(r => r.active).length;
  }

  get avgQuantity(): string {
    if (!this.reminders.length) return '0 ml';
    const avg = this.reminders.reduce((s, r) => s + r.quantity, 0) / this.reminders.length;
    return Math.round(avg) + ' ml';
  }

  get avgFrequency(): string {
    if (!this.reminders.length) return '-';
    const avg = this.reminders.reduce((s, r) => s + r.frequency, 0) / this.reminders.length;
    return Math.round(avg) + ' min';
  }

  get avgBmi(): string {
    if (!this.allUsersHealth.length) return '0';
    const avg = this.allUsersHealth.reduce((s, u) => s + u.bmi, 0) / this.allUsersHealth.length;
    return avg.toFixed(1);
  }

  private initFreqChart(): void {
    const canvas = document.getElementById('freqChart') as HTMLCanvasElement;
    if (!canvas) return;
    if (this.bmiChart) this.bmiChart.destroy();

    const freq30  = this.reminders.filter(r => r.frequency <= 30).length;
    const freq60  = this.reminders.filter(r => r.frequency > 30 && r.frequency <= 60).length;
    const freq90  = this.reminders.filter(r => r.frequency > 60 && r.frequency <= 90).length;
    const freq120 = this.reminders.filter(r => r.frequency > 90).length;

    this.bmiChart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: ['≤ 30 min', '31–60 min', '61–90 min', '> 90 min'],
        datasets: [{
          data: [freq30, freq60, freq90, freq120],
          backgroundColor: ['#E61920', '#EF9F27', '#3B8BD4', '#1D9E75'],
          borderWidth: 0,
          hoverOffset: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        cutout: '65%'
      }
    });
  }

  private initQtyChart(): void {
    const canvas = document.getElementById('qtyChart') as HTMLCanvasElement;
    if (!canvas) return;
    if (this.hydroChart) this.hydroChart.destroy();

    const labels = this.reminders.map(r => r.userName?.split(' ')[0] || 'User');
    const data   = this.reminders.map(r => r.quantity);
    const colors = this.reminders.map(r =>
      r.quantity >= 300 ? '#1D9E75' : r.quantity >= 200 ? '#EF9F27' : '#E24B4A'
    );

    this.hydroChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Quantité (ml)',
          data,
          backgroundColor: colors,
          borderRadius: 5,
          borderSkipped: false
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: {
            ticks: { color: '#9090a8', font: { size: 11 } },
            grid: { color: 'rgba(144,144,168,0.1)' }
          },
          x: {
            ticks: { color: '#9090a8', font: { size: 11 } },
            grid: { display: false }
          }
        }
      }
    });
  }
}
