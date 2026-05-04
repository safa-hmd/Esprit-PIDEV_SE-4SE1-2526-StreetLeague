import { environment } from 'src/environments/environment';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { HealthService, WaterReminderDTO, GoalDTO, RewardDTO, UserBadge, DietRequestDTO, DietResponseDTO,FitnessReportDTO } from '../../services/health.service';
import { AuthService } from '../../services/auth.service';
import { WeeklyHealthReportDTO } from '../../services/health.service';
import jsPDF from 'jspdf';
import { HttpClient } from '@angular/common/http';


interface DrinkLog {
  time: string;
  quantity: number;
}

@Component({
  selector: 'app-health',
  templateUrl: './health.component.html',
  styleUrls: ['./health.component.css']
})
export class HealthComponent implements OnInit, OnDestroy {

  // ── Diet ── (UNE SEULE FOIS - supprime les doublons)
  dietResult: DietResponseDTO | null = null;
  dietLoading: boolean = false;
  showDiet: boolean = false;
  
  // Metrics
  heartRate: number = 72;
  calories: number = 1240;

  glassCount: number = 0;
  glassTarget: number = 8;
  hydrationPercentage: number = 0;
currentStreak: number = 0;
longestStreak: number = 0;
  // Reminder
  reminderActive: boolean = false;
  reminderFrequency: number = 60;
  amountPerReminder: number = 250;
  reminderId?: number;
  nextReminderTime: number = 0;
  countdownInterval: any;
  showNotification: boolean = false;
  notificationMessage: string = '';

  // Drink History
  drinkHistory: DrinkLog[] = [];

  // BMI
  weight!: number;
  height!: number;
  bmiResult: string = '';
  bmiLabel: string = '';
  idealWeight: string = '';
  weightDifference: string = '';
  bmiCategory: string = '';

  // Goals
  waterGoal: number = 2000;
  bmiGoal: number = 22;
  showGoalForm: boolean = false;

  // Badges
  badges: UserBadge[] = [];

  // Spin
  canSpin: boolean = false;
  isSpinning: boolean = false;
  wheelRotation: number = 0;
  spinResult: string = '';
  showSpinResult: boolean = false;
  showWheel: boolean = false;

  // Report
  weeklyReport: WeeklyHealthReportDTO | null = null;
  showWeeklyReport: boolean = false;
  reportLoading: boolean = false;
  showCongrats: boolean = false;
  congratsMessage: string = '';

  fitnessReport: FitnessReportDTO | null = null;
fitnessLoading: boolean = false;
fitnessError: boolean = false;

  private currentUserId: number | null = null;

  constructor(
    private healthService: HealthService,
    private authService: AuthService,
    private http: HttpClient
  ) {}

ngOnInit() {
  this.loadTodayHistory();
  
  const userId = this.authService.getCurrentUserId();
  if (userId && userId !== 0) {
    this.currentUserId = userId;
    this.initHealthData(userId);
  } else {
    // ✅ Fallback via /user/profile (utilise le JWT automatiquement)
    this.http.get<any>(`${environment.baseUrl}/user/profile`).subscribe({
      next: (profile) => {
        this.currentUserId = profile.idUser;
        this.initHealthData(profile.idUser);
      },
      error: (err) => console.error('❌ Failed to get profile:', err)
    });
  }
}

private initHealthData(userId: number) {
  this.loadBadges(userId);
  this.checkSpinStatus(userId);
  this.loadGoals(userId);
  this.loadTodayWaterFromDB(userId);
  this.loadStreak(userId);
  this.loadExistingReminder(userId);
}

  ngOnDestroy() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }

  // ── Load user data on init ──
  loadUserData() {
    this.authService.getUserIdByEmail().subscribe({
      next: (userId) => {
        this.currentUserId = userId;
        this.loadBadges(userId);
        this.checkSpinStatus(userId);
        this.loadGoals(userId);
        this.loadTodayWaterFromDB(userId);
        this.loadStreak(userId);
        this.loadExistingReminder(userId);
      }
    });
  }
  loadExistingReminder(userId: number) {
  // Appelle getAll côté admin OU ajoute un endpoint GET /waterReminder/byUser
  // Pour l'instant, on utilise localStorage comme fallback
  const savedReminderId = localStorage.getItem(`reminderId_${userId}`);
  if (savedReminderId) {
    this.reminderId = parseInt(savedReminderId);
  }
}
loadStreak(userId: number) {
  this.healthService.getStreak(userId).subscribe({
    next: (data) => {
      this.currentStreak = data.currentStreak;
      this.longestStreak = data.longestStreak;
    },
    error: (err) => console.error(err)
  });
}
loadTodayWaterFromDB(userId: number) {
  this.healthService.getTodayWaterLogs(userId).subscribe({
    next: (logs) => {
      console.log('Today water logs:', logs); // debug
      if (logs && logs.length > 0) {
        const totalMl = logs[0].totalMl;
        // ✅ Sync glassCount avec DB
        this.glassCount = Math.floor(totalMl / this.amountPerReminder);
        // ✅ Sync aussi drinkHistory display
        const goalMl = this.waterGoal;
        this.hydrationPercentage = Math.min(100, (totalMl / goalMl) * 100);
        console.log(`totalMl: ${totalMl}, glassCount: ${this.glassCount}, %: ${this.hydrationPercentage}`);
      } else {
        // ✅ Reset si aucun log aujourd'hui
        this.glassCount = 0;
        this.hydrationPercentage = 0;
      }
    },
    error: (err) => console.error('loadTodayWater error:', err)
  });
}

  loadBadges(userId: number) {
    this.healthService.getBadges(userId).subscribe({
      next: (badges) => this.badges = badges,
      error: (err) => console.error(err)
    });
  }

  checkSpinStatus(userId: number) {
    this.healthService.getSpinStatus(userId).subscribe({
      next: (status) => {
        const wasLocked = !this.canSpin;
        this.canSpin = status.canSpin;
        this.showWheel = status.canSpin;
        if (wasLocked && status.canSpin) {
          this.showCongrats = true;
        }
      }
    });
  }

  loadGoals(userId: number) {
    this.healthService.getGoals(userId).subscribe({
      next: (goals) => {
        const waterGoal = goals.find(g => g.goalType === 'WATER');
        const bmiGoal = goals.find(g => g.goalType === 'BMI');
        if (waterGoal) this.waterGoal = waterGoal.targetValue;
        if (bmiGoal) this.bmiGoal = bmiGoal.targetValue;
      },
      error: (err) => console.error(err)
    });
  }

  // ── Notification ──
  requestNotificationPermission() {
    if ('Notification' in window) Notification.requestPermission();
  }

  // ── Reminder ──
  toggleReminder() {
    this.reminderActive = !this.reminderActive;
    if (this.reminderActive) {
      this.requestNotificationPermission();
      this.startCountdown();
    } else {
      if (this.countdownInterval) clearInterval(this.countdownInterval);
      this.nextReminderTime = 0;
    }
    this.saveSettings();
  }

  startCountdown() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
    this.nextReminderTime = this.reminderFrequency * 60;
    this.countdownInterval = setInterval(() => {
      this.nextReminderTime--;
      if (this.nextReminderTime <= 0) {
        this.nextReminderTime = this.reminderFrequency * 60;
        this.triggerReminder();
      }
    }, 1000);
  }

  triggerReminder() {
    if (!this.reminderActive) return;
    this.notificationMessage = `💧 Time to drink ${this.amountPerReminder} ml of water!`;
    this.showNotification = true;
    setTimeout(() => { this.showNotification = false; }, 5000);
    if (Notification.permission === 'granted') {
      new Notification('💧 Hydration Reminder', {
        body: `Drink ${this.amountPerReminder} ml of water`,
        icon: 'assets/water.png'
      });
    }
    this.playSound();
  }

  playSound() {
    const audio = new Audio('assets/sounds/alert.mp3');
    audio.play().catch(err => console.log('Sound blocked', err));
  }

  // ── Drink Now — log en DB aussi ──
  drinkNow() {
  const time = new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  this.drinkHistory.push({ time, quantity: this.amountPerReminder });
  
  // ✅ Enlève la condition — toujours incrementer
  this.glassCount++;
  this.updateProgress();
  this.saveDrinkToLocalStorage();

  // ✅ Check userId avant appel
  console.log('currentUserId:', this.currentUserId); // debug
  
  if (this.currentUserId) {
    this.healthService.logWater(this.currentUserId, this.amountPerReminder).subscribe({
      next: (log) => {
        console.log('Water logged ✅', log);
        // ✅ Refresh glassCount depuis DB pour sync
        this.loadTodayWaterFromDB(this.currentUserId!);
        
        if (log.goalReached) {
          this.notificationMessage = '🎉 Daily water goal reached!';
          this.showNotification = true;
          setTimeout(() => { this.showNotification = false; }, 5000);
          this.checkSpinStatus(this.currentUserId!);
          this.loadBadges(this.currentUserId!);
          this.loadStreak(this.currentUserId!); // ✅ refresh streak
        }
      },
      error: (err) => {
        console.error('Failed to log water', err);
        // ✅ Affiche l'erreur exacte
        this.notificationMessage = '❌ Error: ' + (err.error?.message || err.status);
        this.showNotification = true;
        setTimeout(() => { this.showNotification = false; }, 5000);
      }
    });
  } else {
    console.error('❌ No userId — user not loaded yet');
  }
}

  updateProgress() {
    this.hydrationPercentage = (this.glassCount / this.glassTarget) * 100;
  }

  loadTodayHistory() {
    const today = new Date().toDateString();
    const stored = localStorage.getItem(`drinkHistory_${today}`);
    if (stored) {
      this.drinkHistory = JSON.parse(stored);
      this.glassCount = this.drinkHistory.length;
      this.updateProgress();
    }
  }

  saveDrinkToLocalStorage() {
    const today = new Date().toDateString();
    localStorage.setItem(`drinkHistory_${today}`, JSON.stringify(this.drinkHistory));
  }

  setFrequency(freq: number) { this.reminderFrequency = freq; }

  changeQuantity(val: number) {
    this.amountPerReminder += val;
    if (this.amountPerReminder < 100) this.amountPerReminder = 100;
    if (this.amountPerReminder > 2000) this.amountPerReminder = 2000;
  }

  // ── BMI ──
  getBMIPercentage(): number {
    if (!this.bmiResult) return 0;
    const bmi = parseFloat(this.bmiResult);
    return Math.min(100, Math.max(0, ((bmi - 16) / (40 - 16)) * 100));
  }

  isWeightDifferencePositive(): boolean { return parseFloat(this.weightDifference) > 0; }
  isWeightDifferenceNegative(): boolean { return parseFloat(this.weightDifference) < 0; }

  getWeightDifferenceDisplay(): string {
    const diff = parseFloat(this.weightDifference);
    return diff > 0 ? '+' + this.weightDifference : this.weightDifference;
  }

  calculateBMI() {
  this.bmiResult = '';
  this.bmiLabel = '';
  this.bmiCategory = '';

  if (!this.weight || !this.height) {
    this.notificationMessage = 'Error: Please enter both weight and height';
    this.showNotification = true;
    setTimeout(() => { this.showNotification = false; }, 5000);
    return;
  }
  if (this.weight <= 0 || this.height <= 0) {
    this.notificationMessage = 'Error: Values must be positive';
    this.showNotification = true;
    setTimeout(() => { this.showNotification = false; }, 5000);
    return;
  }

  const h = this.height / 100;
  const bmi = this.weight / (h * h);
  this.bmiResult = bmi.toFixed(1);

  if (bmi < 18.5) { this.bmiLabel = 'Insuffisant'; this.bmiCategory = 'insuffisant'; }
  else if (bmi < 25) { this.bmiLabel = 'Normal'; this.bmiCategory = 'normal'; }
  else if (bmi < 30) { this.bmiLabel = 'Surpoids'; this.bmiCategory = 'surpoids'; }
  else { this.bmiLabel = 'Obésité'; this.bmiCategory = 'obesite'; }

  const idealW = 22 * (h * h);
  this.idealWeight = idealW.toFixed(1);
  this.weightDifference = (this.weight - idealW).toFixed(1);

  // ✅ Lecture directe localStorage — pas d'appel HTTP
  const userId = this.authService.getCurrentUserId();
  if (!userId) {
    console.error('❌ User not logged in');
    return;
  }

  this.currentUserId = userId;
  this.healthService.updateHealth(userId, {
    weight: this.weight,
    height: this.height,
    bmi: bmi
  }).subscribe({
    next: () => {
      console.log('Health data saved ✅');
      if (bmi <= this.bmiGoal) {
        this.checkSpinStatus(userId);
        this.loadBadges(userId);
        this.loadFitnessReport(userId);
      }
    },
    error: (err) => console.error('Failed to save health data', err)
  });
}

  // ── Goals ──
  saveGoals() {
    if (!this.currentUserId) return;

    const waterDto: GoalDTO = { goalType: 'WATER', targetValue: this.waterGoal };
    const bmiDto: GoalDTO = { goalType: 'BMI', targetValue: this.bmiGoal };

    this.healthService.setGoal(this.currentUserId, waterDto).subscribe();
    this.healthService.setGoal(this.currentUserId, bmiDto).subscribe({
      next: () => {
        this.showGoalForm = false;
        this.notificationMessage = '✅ Goals saved!';
        this.showNotification = true;
        setTimeout(() => { this.showNotification = false; }, 3000);
      }
    });
  }

  // ── Spin ──
spinWheel() {
  if (!this.canSpin || this.isSpinning || !this.currentUserId) return;
  this.isSpinning = true;
  this.showSpinResult = false;

  this.healthService.spin(this.currentUserId).subscribe({
    next: (result: RewardDTO) => {
        console.log('Result:', result.result, '| segmentIndex:', result.segmentIndex);

      const segmentDeg = 360 / 6; // 60° par segment
      
      // ✅ Le pointer est en HAUT → segment 0 commence à droite
      // Faut corriger l'offset de 30° (demi-segment)
      const offset = 30; // demi-segment pour centrer
      const targetSegmentAngle = result.segmentIndex * segmentDeg + offset;
      
      // ✅ 5 tours complets + arriver sur le bon segment
      // On soustrait car la wheel tourne dans le sens horaire
      const spins = 5 * 360;
      this.wheelRotation = spins + (360 - targetSegmentAngle);

      setTimeout(() => {
        this.isSpinning = false;
        this.spinResult = result.message;
        this.showSpinResult = true;
        this.canSpin = result.canRetry;
        if (!result.canRetry) this.showWheel = false;
        this.loadBadges(this.currentUserId!);
      }, 4000);
    },
    error: (err) => { this.isSpinning = false; console.error(err); }
  });
}

  resetTodayHistory() {
    const today = new Date().toDateString();
    localStorage.removeItem(`drinkHistory_${today}`);
    this.drinkHistory = [];
    this.glassCount = 0;
    this.hydrationPercentage = 0;

    if (this.currentUserId) {
      this.healthService.resetTodayWater(this.currentUserId).subscribe();
    }
  }

  getBadgeIcon(badgeType: string): string {
    const icons: { [key: string]: string } = {
      'WATER_WEEK': '🏆',
      'BADGE': '🏅',
      'FREE_DELIVERY': '🚚',
      'COUPON_5': '🎟️',
      'COUPON_10': '🎫',
      'BMI_GOAL': '💪'
    };
    return icons[badgeType] || '⭐';
  }

  // ── Settings ──
saveSettings() {
  const dto: WaterReminderDTO = {
    frequency: this.reminderFrequency,
    quantity: this.amountPerReminder,
    active: this.reminderActive
  };
  if (this.reminderId) {
    this.healthService.updateReminder(this.reminderId, dto).subscribe();
  } else {
    this.healthService.addReminder(dto).subscribe(res => {
      this.reminderId = res.id;
      // ✅ Persiste l'ID
      if (this.currentUserId) {
        localStorage.setItem(`reminderId_${this.currentUserId}`, res.id.toString());
      }
    });
  }
}

  getGlassArray(): number[] { return Array(this.glassTarget).fill(0); }
  isGlassFilled(index: number): boolean { return index < this.glassCount; }

  formatNextReminder(): string {
    if (!this.reminderActive) return '-';
    const minutes = Math.floor(this.nextReminderTime / 60);
    const seconds = this.nextReminderTime % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  useBadge(badge: UserBadge) {
    if (badge.badgeType === 'COUPON_5' || badge.badgeType === 'COUPON_10') {
      const code = `${badge.badgeType}-${badge.id}`;
      navigator.clipboard.writeText(code).then(() => {
        this.notificationMessage = `✅ Code copied: ${code}`;
        this.showNotification = true;
        setTimeout(() => this.showNotification = false, 4000);
      });
    } else if (badge.badgeType === 'FREE_DELIVERY') {
      this.notificationMessage = '🚚 Free delivery activated on your next order!';
      this.showNotification = true;
      setTimeout(() => this.showNotification = false, 5000);
    }
  }

  getBadgeActionLabel(badgeType: string): string {
    const labels: { [key: string]: string } = {
      'COUPON_5': '🎟️ Copy Code',
      'COUPON_10': '🎫 Copy Code',
      'FREE_DELIVERY': '🚚 Activate',
      'WATER_WEEK': '',
      'BMI_GOAL': '',
      'BADGE': ''
    };
    return labels[badgeType] || '';
  }

  checkAndShowCongrats(type: 'WATER' | 'BMI') {
    this.congratsMessage = type === 'WATER' 
      ? '🎉 Water goal achieved for 7 days! Spin the wheel!'
      : '💪 BMI goal achieved! Spin the wheel!';
    this.showCongrats = true;
  }

  scrollToSpin() {
    document.querySelector('.spin-card')?.scrollIntoView({ behavior: 'smooth' });
  }

  loadWeeklyReport() {
    if (!this.currentUserId) return;
    this.reportLoading = true;
    this.showWeeklyReport = true;

    this.healthService.getWeeklyReport(this.currentUserId).subscribe({
      next: (report) => {
        this.weeklyReport = report;
        this.reportLoading = false;
      },
      error: (err) => {
        console.error('Failed to load weekly report', err);
        this.reportLoading = false;
      }
    });
  }

  downloadReportPDF() {
  if (!this.weeklyReport) return;
  const r = this.weeklyReport;
  const doc = new jsPDF();

  const logoPath = 'assets/images/logofond.png';
  const img = new Image();
  img.src = logoPath;
  img.onload = () => {
    doc.addImage(img, 'PNG', 85, 5, 40, 25);
    this.buildPDF(doc, r, 35);
  };
  img.onerror = () => {
    this.buildPDF(doc, r, 20);
  };
}

private buildPDF(doc: jsPDF, r: any, y: number) {
  const round2 = (n: number) => Math.round(n * 100) / 100;
  const pageW = 196;

  // ── HEADER ──
  doc.setFontSize(18);
  doc.setFont('helvetica', 'bold');
  doc.setTextColor(0, 0, 0);
  doc.text('Weekly Health Report', 105, y, { align: 'center' }); y += 8;

  doc.setFontSize(11);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(80, 80, 80);
  doc.text(r.fullName || '', 105, y, { align: 'center' }); y += 6;
  doc.text(`Period: ${r.weekStart} to ${r.weekEnd}`, 105, y, { align: 'center' }); y += 14;

  // ── HELPERS ──
  const line = () => {
    doc.setDrawColor(200, 200, 200);
    doc.line(14, y - 4, pageW, y - 4);
  };

  const sectionTitle = (title: string) => {
    line();
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(13);
    doc.setTextColor(0, 0, 0);
    doc.text(title, 14, y); y += 8;
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(11);
  };

  // ── 1. BODY METRICS ──
  sectionTitle('Body Metrics');
  doc.setTextColor(0, 0, 0);
  doc.text(`Weight: ${r.currentWeight} kg`, 14, y); y += 6;
  doc.text(`Height: ${r.currentHeight} cm`, 14, y); y += 6;
  doc.text(`BMI: ${round2(r.currentBmi)}`, 14, y); y += 6;

  const change = r.weightChangeThisWeek ?? 0;
  const changeStr = change > 0 ? `+${change.toFixed(1)}` : change.toFixed(1);
  doc.text('Weight Change This Week: ', 14, y);
  if (change > 0) doc.setTextColor(220, 38, 38);
  else if (change < 0) doc.setTextColor(16, 185, 129);
  else doc.setTextColor(0, 0, 0);
  doc.text(`${changeStr} kg`, 75, y);
  doc.setTextColor(0, 0, 0);
  y += 14;

  // ── 2. HYDRATION ──
  sectionTitle('Hydration This Week');
  doc.text(`Total Consumed: ${r.totalWaterConsumedMl} ml`, 14, y); y += 6;
  doc.text(`Days Goal Reached: ${r.daysGoalReached} / 7`, 14, y); y += 6;
  doc.text(`Daily Average: ${Math.round(r.avgDailyWaterMl)} ml`, 14, y); y += 14;

  // ── 3. WATER STREAK ── (nouveau)
  sectionTitle('Water Streak');
  const cs = r.currentWaterStreak ?? 0;
  const ls = r.longestWaterStreak ?? 0;
  doc.text('Current Streak: ', 14, y);
  doc.setTextColor(cs > 0 ? 234 : 150, cs > 0 ? 88 : 150, cs > 0 ? 12 : 150);
  doc.text(`${cs} day${cs !== 1 ? 's' : ''}`, 52, y);
  doc.setTextColor(0, 0, 0); y += 6;

  doc.text('Best Streak: ', 14, y);
  doc.setTextColor(ls > 0 ? 234 : 150, ls > 0 ? 88 : 150, ls > 0 ? 12 : 150);
  doc.text(`${ls} day${ls !== 1 ? 's' : ''}`, 46, y);
  doc.setTextColor(0, 0, 0); y += 14;

  // ── 4. GOALS ──
  sectionTitle('Goals Status');
  if (r.goals && r.goals.length > 0) {
    r.goals.forEach((g: any) => {
      doc.setTextColor(0, 0, 0);
      doc.text(`${g.goalType} - Target: ${g.targetValue}`, 14, y);
      if (g.achieved) doc.setTextColor(16, 185, 129);
      else doc.setTextColor(245, 158, 11);
      doc.setFont('helvetica', 'bold');
      doc.text(g.achieved ? '[Achieved]' : '[In Progress]', 130, y);
      doc.setFont('helvetica', 'normal');
      doc.setTextColor(0, 0, 0);
      y += 6;
    });
  } else {
    doc.setTextColor(150, 150, 150);
    doc.text('No goals set yet.', 14, y);
    doc.setTextColor(0, 0, 0);
    y += 6;
  }
  y += 8;

  // ── 5. BMI HISTORY ──
  sectionTitle('BMI History');
  if (r.bmiHistory && r.bmiHistory.length > 0) {
    r.bmiHistory.forEach((entry: any) => {
      if (y > 270) { doc.addPage(); y = 20; }
      doc.setTextColor(80, 80, 80);
      doc.text(`${entry.date}`, 14, y);
      doc.setTextColor(0, 0, 0);
      doc.text(`BMI: ${round2(entry.bmi)}`, 80, y);
      doc.text(`Weight: ${entry.weight} kg`, 130, y);
      y += 6;
    });
  } else {
    doc.setTextColor(150, 150, 150);
    doc.text('No BMI history recorded yet.', 14, y);
  }

  // ── FOOTER ──
  doc.setFontSize(9);
  doc.setTextColor(150, 150, 150);
  doc.text('Generated by StreetLeague Health Tracker', 105, 290, { align: 'center' });

  doc.save(`health-report-${r.fullName?.replace(/\s+/g, '-')}-${r.weekStart}.pdf`);
}
  getWeightChangeDisplay(): string {
    if (!this.weeklyReport) return '';
    const change = this.weeklyReport.weightChangeThisWeek;
    return change > 0 ? `+${change.toFixed(1)}` : change.toFixed(1);
  }

  isWeightChangePositive(): boolean {
    return !!this.weeklyReport && this.weeklyReport.weightChangeThisWeek > 0;
  }

  isWeightChangeNegative(): boolean {
    return !!this.weeklyReport && this.weeklyReport.weightChangeThisWeek < 0;
  }

  downloadPdf() {
    if (!this.weeklyReport) {
      if (!this.currentUserId) return;
      this.healthService.getWeeklyReport(this.currentUserId).subscribe({
        next: (report) => {
          this.weeklyReport = report;
          this.generatePdf(report);
        },
        error: (err) => console.error('Failed to load report for PDF', err)
      });
    } else {
      this.generatePdf(this.weeklyReport);
    }
  }

  private generatePdf(report: WeeklyHealthReportDTO) {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
    let y = 20;

    doc.setFontSize(22);
    doc.setFont('helvetica', 'bold');
    doc.text('Weekly Health Report', pageWidth / 2, y, { align: 'center' });
    y += 8;

    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(100);
    doc.text(
      `${report.fullName}  |  ${report.weekStart} → ${report.weekEnd}`,
      pageWidth / 2, y, { align: 'center' }
    );
    y += 12;

    doc.setDrawColor(200);
    doc.line(14, y, pageWidth - 14, y);
    y += 10;

    doc.setTextColor(0);
    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.text('Body Metrics', 14, y);
    y += 8;

    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    const weightChange = report.weightChangeThisWeek;
    const changeSign = weightChange > 0 ? '+' : '';

    [
      ['Weight', `${report.currentWeight} kg`],
      ['Height', `${report.currentHeight} cm`],
      ['BMI', `${report.currentBmi}`],
      ['Weight Change This Week', `${changeSign}${weightChange.toFixed(1)} kg`],
    ].forEach(([label, value]) => {
      doc.setTextColor(100); doc.text(label, 14, y);
      doc.setTextColor(0);   doc.text(value, 110, y);
      y += 7;
    });
    y += 5;

    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(0);
    doc.text('Hydration This Week', 14, y);
    y += 8;

    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    [
      ['Total Water Consumed', `${report.totalWaterConsumedMl} ml`],
      ['Days Goal Reached', `${report.daysGoalReached} / 7`],
      ['Daily Average', `${Math.round(report.avgDailyWaterMl)} ml`],
    ].forEach(([label, value]) => {
      doc.setTextColor(100); doc.text(label, 14, y);
      doc.setTextColor(0);   doc.text(value, 110, y);
      y += 7;
    });
    y += 5;

    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(0);
    doc.text('Goals Status', 14, y);
    y += 8;

    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    if (report.goals.length === 0) {
      doc.setTextColor(150);
      doc.text('No goals set yet.', 14, y);
      y += 7;
    } else {
      report.goals.forEach(goal => {
        doc.setTextColor(0);
        doc.text(`${goal.goalType}  —  Target: ${goal.targetValue}`, 14, y);
        doc.setTextColor(goal.achieved ? 22 : 150);
        doc.text(goal.achieved ? 'Achieved' : 'In Progress', 155, y);
        doc.setTextColor(0);
        y += 7;
      });
    }
    y += 5;

    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(0);
    doc.text('BMI History This Week', 14, y);
    y += 8;

    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    if (report.bmiHistory.length === 0) {
      doc.setTextColor(150);
      doc.text('No BMI records this week.', 14, y);
    } else {
      report.bmiHistory.forEach(entry => {
        doc.setTextColor(100); doc.text(`${entry.date}`, 14, y);
        doc.setTextColor(0);   doc.text(`BMI: ${entry.bmi}`, 80, y);
        doc.text(`Weight: ${entry.weight} kg`, 130, y);
        y += 7;
        if (y > 270) { doc.addPage(); y = 20; }
      });
    }

    const today = new Date().toLocaleDateString('fr-FR');
    doc.setFontSize(9);
    doc.setTextColor(150);
    doc.text(`Generated on ${today}`, pageWidth / 2, 290, { align: 'center' });

    const safeName = report.fullName.replace(/\s+/g, '-');
    doc.save(`health-report-${safeName}-${report.weekStart}.pdf`);
  }

  // ✅ Méthode getDietPlan corrigée
  getDietPlan() {
    if (!this.currentUserId) {
      console.error('User ID not found');
      this.notificationMessage = '❌ Please log in first';
      this.showNotification = true;
      setTimeout(() => this.showNotification = false, 3000);
      return;
    }
    
    if (!this.bmiResult) {
      this.notificationMessage = '⚠️ Please calculate your BMI first!';
      this.showNotification = true;
      setTimeout(() => this.showNotification = false, 3000);
      return;
    }
    
    this.dietLoading = true;
    const userId = this.currentUserId; // ✅ TypeScript sait que ce n'est pas null ici
    
    this.healthService.getDietRecommendation(userId, 25, parseFloat(this.bmiResult))
      .subscribe({
        next: (response: any) => {
          console.log('Diet response:', response);
          this.dietResult = response;
          this.showDiet = true;
          this.dietLoading = false;
        },
        error: (err) => {
          console.error('Error getting diet plan:', err);
          this.notificationMessage = '❌ Error getting diet plan';
          this.showNotification = true;
          setTimeout(() => this.showNotification = false, 3000);
          this.dietLoading = false;
        }
      });
  }
  showReportModal = false;

openReportModal(): void {
  this.showReportModal = true;
  this.loadWeeklyReport();
}

closeReportModal(): void {
  this.showReportModal = false;
}

loadFitnessReport(userId: number) {
  this.fitnessLoading = true;
  this.fitnessError = false;
  this.healthService.getFitnessReport(userId).subscribe({
    next: (report) => {
      this.fitnessReport = report;
      this.fitnessLoading = false;
    },
    error: (err) => {
      console.error('Failed to load fitness report', err);
      this.fitnessLoading = false;
      this.fitnessError = true;
    }
  });
}

refreshFitnessReport() {
  this.authService.getUserIdByEmail().subscribe({
    next: (userId) => this.loadFitnessReport(userId),
    error: (err) => console.error('Cannot get userId', err)
  });
}

getStatusLabel(): string {
  if (!this.fitnessReport) return '';
  const map: Record<string, string> = {
    'ELITE':   '🟢 ELITE — Excellent',
    'FIT':     '🟡 FIT — Apte',
    'CAUTION': '🟠 CAUTION — Avec réserve',
    'UNFIT':   '🔴 UNFIT — Non apte',
    'NO_DATA': '⚪ Pas de données'
  };
  return map[this.fitnessReport.status] || this.fitnessReport.status;
}

getScoreBarWidth(score: number): string {
  return Math.min(score, 100) + '%';
}

getScoreBarColor(score: number): string {
  if (score >= 80) return '#22c55e';
  if (score >= 60) return '#eab308';
  if (score >= 40) return '#f97316';
  return '#ef4444';
}
}
