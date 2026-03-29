import { Component, OnInit, OnDestroy } from '@angular/core';
import { HealthService, WaterReminderDTO } from '../../services/health.service';

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

  constructor(private healthService: HealthService) {}

  ngOnInit() {
    this.loadTodayHistory();
  }

  ngOnDestroy() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }

  // 🔔 Notification permission
  requestNotificationPermission() {
    if ('Notification' in window) {
      Notification.requestPermission();
    }
  }

  // 🔔 Toggle reminder
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

  // 🔁 Countdown
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

  // 🔔 Reminder
  triggerReminder() {
    if (!this.reminderActive) return;
    // Show notification message
    this.notificationMessage = `💧 Time to drink ${this.amountPerReminder} ml of water!`;
    this.showNotification = true;

    // Hide notification after 5 seconds
    setTimeout(() => {
      this.showNotification = false;
    }, 5000);
    if (Notification.permission === 'granted') {
      new Notification('💧 Hydration Reminder', {
        body: `Buvez ${this.amountPerReminder} ml d’eau`,
        icon: 'assets/water.png'
      });
    }

    this.playSound();
  }

  // 🔊 Sound
  playSound() {
    const audio = new Audio('assets/sounds/alert.mp3');
    audio.play().catch(err => console.log('Sound blocked', err));
  }

  // 🥤 Drink
  drinkNow() {
    const time = new Date().toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });

    this.drinkHistory.push({
      time,
      quantity: this.amountPerReminder
    });

    if (this.glassCount < this.glassTarget) {
      this.glassCount++;
      this.updateProgress();
    }

    this.saveDrinkToLocalStorage();
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

  // 🔧 FUNCTIONS MISSING (IMPORTANT)

  setFrequency(freq: number) {
    this.reminderFrequency = freq;
  }

  changeQuantity(val: number) {
    this.amountPerReminder += val;

    if (this.amountPerReminder < 100) this.amountPerReminder = 100;
    if (this.amountPerReminder > 2000) this.amountPerReminder = 2000;
  }

  getBMIPercentage(): number {
    if (!this.bmiResult) return 0;

    const bmi = parseFloat(this.bmiResult);
    const minBmi = 16;
    const maxBmi = 40;

    return ((bmi - minBmi) / (maxBmi - minBmi)) * 100;
  }

  isWeightDifferencePositive(): boolean {
    return parseFloat(this.weightDifference) > 0;
  }

  isWeightDifferenceNegative(): boolean {
    return parseFloat(this.weightDifference) < 0;
  }

  getWeightDifferenceDisplay(): string {
    const diff = parseFloat(this.weightDifference);
    return diff > 0 ? '+' + this.weightDifference : this.weightDifference;
  }

  // BMI
  calculateBMI() {
    if (!this.weight || !this.height) return;

    const h = this.height / 100;
    const bmi = this.weight / (h * h);

    this.bmiResult = bmi.toFixed(1);

    if (bmi < 18.5) this.bmiLabel = 'Insuffisant';
    else if (bmi < 25) this.bmiLabel = 'Normal';
    else if (bmi < 30) this.bmiLabel = 'Surpoids';
    else this.bmiLabel = 'Obésité';

    const idealW = 22 * (h * h);
    this.idealWeight = idealW.toFixed(1);

    const diff = this.weight - idealW;
    this.weightDifference = diff.toFixed(1);
  }

  // Save settings
  saveSettings() {
    const dto: WaterReminderDTO = {
      frequency: this.reminderFrequency,
      quantity: this.amountPerReminder,
      active: this.reminderActive
    };

    if (this.reminderId) {
      this.healthService.updateReminder(this.reminderId, dto)
        .subscribe(res => console.log(res));
    } else {
      this.healthService.addReminder(dto)
        .subscribe(res => {
          this.reminderId = res.id;
        });
    }
  }

  // Utils
  getGlassArray(): number[] {
    return Array(this.glassTarget).fill(0);
  }

  isGlassFilled(index: number): boolean {
    return index < this.glassCount;
  }

  formatNextReminder(): string {
    if (!this.reminderActive) return '-';

    const minutes = Math.floor(this.nextReminderTime / 60);
    const seconds = this.nextReminderTime % 60;

    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }
}