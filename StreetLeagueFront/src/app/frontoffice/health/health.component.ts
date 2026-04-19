import { Component, OnInit, OnDestroy } from '@angular/core';
import { HealthService, WaterReminderDTO, GoalDTO, RewardDTO, UserBadge, DietRequestDTO, DietResponseDTO } from '../../services/health.service';import { AuthService } from '../../services/auth.service';
import { WeeklyHealthReportDTO } from '../../services/health.service';
import jsPDF from 'jspdf';

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

  // Metrics
  heartRate: number = 72;
  calories: number = 1240;

  glassCount: number = 0;
  glassTarget: number = 8;
  hydrationPercentage: number = 0;
// ── Diet ──
dietResult: DietResponseDTO | null = null;
dietLoading: boolean = false;
showDiet: boolean = false;
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

  //Report
  weeklyReport: WeeklyHealthReportDTO | null = null;
showWeeklyReport: boolean = false;
reportLoading: boolean = false;

  private currentUserId: number | null = null;

  constructor(
    private healthService: HealthService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadTodayHistory();
    this.loadUserData();
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
      this.loadTodayWaterFromDB(userId); // ← أضف هذا
    }
  });
}
loadTodayWaterFromDB(userId: number) {
  this.healthService.getTodayWaterLogs(userId).subscribe({
    next: (logs) => {
      if (logs && logs.length > 0) {
        // الـ backend يرجع DailyWaterLog — خذ totalMl واحسب عدد الكؤوس
        const totalMl = logs[0].totalMl;
        this.glassCount = Math.floor(totalMl / this.amountPerReminder);
        this.updateProgress();
      }
    },
    error: (err) => console.error(err)
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
    if (this.glassCount < this.glassTarget) {
      this.glassCount++;
      this.updateProgress();
    }
    this.saveDrinkToLocalStorage();

    // ✅ Sauvegarder en DB
    if (this.currentUserId) {
      this.healthService.logWater(this.currentUserId, this.amountPerReminder).subscribe({
        next: (log) => {
          console.log('Water logged ✅', log);
          if (log.goalReached) {
            this.notificationMessage = '🎉 Objectif eau atteint ! Continuez comme ça !';
            this.showNotification = true;
            setTimeout(() => { this.showNotification = false; }, 5000);
            // Recheck spin
            this.checkSpinStatus(this.currentUserId!);
            this.loadBadges(this.currentUserId!);
          }
        },
        error: (err) => console.error('Failed to log water', err)
      });
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

    this.authService.getUserIdByEmail().subscribe({
      next: (userId) => {
        this.currentUserId = userId;
        this.healthService.updateHealth(userId, {
          weight: this.weight,
          height: this.height,
          bmi: bmi
        }).subscribe({
          next: () => {
            console.log('Health data saved ✅');
            // Vérifier si BMI goal atteint
            if (bmi <= this.bmiGoal) {
              this.checkSpinStatus(userId);
              this.loadBadges(userId);
            }
          },
          error: (err) => console.error('Failed to save health data', err)
        });
      },
      error: (err) => console.error('Failed to get userId', err)
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
        // احسب الزاوية بناءً على segment من الـ backend
        const segmentDeg = 360 / 6; // 6 segments = 60° each
        const targetAngle = 5 * 360 + (result.segmentIndex * segmentDeg + segmentDeg / 2);
        this.wheelRotation = Math.ceil(this.wheelRotation / 360) * 360 + targetAngle;

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
      this.healthService.updateReminder(this.reminderId, dto).subscribe(res => console.log(res));
    } else {
      this.healthService.addReminder(dto).subscribe(res => { this.reminderId = res.id; });
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
    // كوبي الكود للـ clipboard
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
  // WATER_WEEK و BMI_GOAL مجرد عرض، ما فيهم action
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
showCongrats: boolean = false;
congratsMessage: string = '';

checkAndShowCongrats(type: 'WATER' | 'BMI') {
  this.congratsMessage = type === 'WATER' 
    ? '🎉 Water goal achieved for 7 days! Spin the wheel!'
    : '💪 BMI goal achieved! Spin the wheel!';
  this.showCongrats = true;
}
scrollToSpin() {
  document.querySelector('.spin-card')?.scrollIntoView({ behavior: 'smooth' });
}


// Ajouter cette méthode dans la classe
// Remplacer loadWeeklyReport() par :
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
  let y = 35;

  // ── Logo ──
  const logoPath = 'assets/images/logofond.png'; // mets le logo dans src/assets/
  const img = new Image();
  img.src = logoPath;
  img.onload = () => {
    doc.addImage(img, 'PNG', 85, 5, 40, 25); // centré en haut
    this.buildPDF(doc, r, y);
  };
  img.onerror = () => {
    // si logo pas trouvé, génère quand même sans logo
    this.buildPDF(doc, r, 20);
  };
}

private buildPDF(doc: jsPDF, r: any, y: number) {
  const round2 = (n: number) => Math.round(n * 100) / 100;

  // ── Header ──
  doc.setFontSize(18);
  doc.setFont('helvetica', 'bold');
  doc.text('Weekly Health Report', 105, y, { align: 'center' });
  y += 8;
  doc.setFontSize(11);
  doc.setFont('helvetica', 'normal');
  doc.text(r.fullName, 105, y, { align: 'center' });
  y += 6;
  doc.text(`Period: ${r.weekStart} to ${r.weekEnd}`, 105, y, { align: 'center' });
  y += 14;

  // ── Séparateur ──
  doc.setDrawColor(200, 200, 200);
  doc.line(14, y - 4, 196, y - 4);

  // ── Body Metrics ──
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(13);
  doc.text('Body Metrics', 14, y); y += 8;
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(11);
  doc.text(`Weight: ${r.currentWeight} kg`, 14, y); y += 6;
  doc.text(`Height: ${r.currentHeight} cm`, 14, y); y += 6;
  doc.text(`BMI: ${round2(r.currentBmi)}`, 14, y); y += 6;
  const change = r.weightChangeThisWeek;
  const changeStr = change > 0 ? `+${change.toFixed(1)}` : change.toFixed(1);
  doc.text(`Weight Change This Week: ${changeStr} kg`, 14, y); y += 14;

  doc.line(14, y - 4, 196, y - 4);

  // ── Hydration ──
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(13);
  doc.text('Hydration This Week', 14, y); y += 8;
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(11);
  doc.text(`Total Consumed: ${r.totalWaterConsumedMl} ml`, 14, y); y += 6;
  doc.text(`Days Goal Reached: ${r.daysGoalReached} / 7`, 14, y); y += 6;
  doc.text(`Daily Average: ${Math.round(r.avgDailyWaterMl)} ml`, 14, y); y += 14;

  doc.line(14, y - 4, 196, y - 4);

  // ── Goals ──
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(13);
  doc.text('Goals Status', 14, y); y += 8;
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(11);
  if (r.goals && r.goals.length > 0) {
    r.goals.forEach((g: any) => {
      const status = g.achieved ? '[Achieved]' : '[In Progress]';
      doc.text(`${g.goalType} - Target: ${g.targetValue}  ${status}`, 14, y);
      y += 6;
    });
  } else {
    doc.text('No goals set yet.', 14, y); y += 6;
  }
  y += 8;

  doc.line(14, y - 4, 196, y - 4);

  // ── BMI History ──
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(13);
  doc.text('BMI History', 14, y); y += 8;
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(11);
  if (r.bmiHistory && r.bmiHistory.length > 0) {
    r.bmiHistory.forEach((entry: any) => {
      // nouvelle page si nécessaire
      if (y > 270) { doc.addPage(); y = 20; }
      doc.text(
        `${entry.date}   BMI: ${round2(entry.bmi)}   Weight: ${entry.weight} kg`,
        14, y
      );
      y += 6;
    });
  } else {
    doc.text('No BMI history recorded yet.', 14, y);
  }

  // ── Footer ──
  doc.setFontSize(9);
  doc.setTextColor(150, 150, 150);
  doc.text('Generated by StreetLeague Health Tracker', 105, 290, { align: 'center' });

  doc.save(`health-report-${r.weekStart}.pdf`);
}

// Helper pour afficher le changement de poids avec signe
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

  // Header
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

  // Body Metrics
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

  // Hydration
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

  // Goals
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

  // BMI History
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

  // Footer
  const today = new Date().toLocaleDateString('fr-FR');
  doc.setFontSize(9);
  doc.setTextColor(150);
  doc.text(`Generated on ${today}`, pageWidth / 2, 290, { align: 'center' });

  const safeName = report.fullName.replace(/\s+/g, '-');
  doc.save(`health-report-${safeName}-${report.weekStart}.pdf`);
}


getDietPlan() {
  if (!this.currentUserId || !this.bmiResult) {
    this.notificationMessage = '⚠️ Calculate your BMI first!';
    this.showNotification = true;
    setTimeout(() => this.showNotification = false, 3000);
    return;
  }
  this.dietLoading = true;
  const request: DietRequestDTO = {
    age: 25, // لو عندك age في الـ user خذه من الـ auth
    bmi: parseFloat(this.bmiResult)
  };
  this.healthService.getDietRecommendation(this.currentUserId, request).subscribe({
    next: (diet) => {
      this.dietResult = diet;
      this.showDiet = true;
      this.dietLoading = false;
    },
    error: (err) => {
      console.error(err);
      this.dietLoading = false;
    }
  });
}
}
