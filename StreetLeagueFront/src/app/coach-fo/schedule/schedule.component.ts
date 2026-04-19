import { Component, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { ScheduleEvent } from 'src/app/models/schedule-event.model';
import { ScheduleRefreshService } from 'src/app/services/schedule-refresh.service';
import { ScheduleService } from 'src/app/services/schedule.service';

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  events: ScheduleEvent[] = [];
  view: 'week' | 'month' = 'week';
  loading = false;
  userId: number = 0;

  weekDays: Date[] = [];
  hours = Array.from({ length: 15 }, (_, i) => i + 8);
  monthDays: (Date | null)[] = [];
  monthLabel = '';
  currentDate = new Date();
  readonly DAY_NAMES = ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'];

  private destroy$ = new Subject<void>();
  constructor(private svc: ScheduleService, private scheduleRefresh: ScheduleRefreshService) {
    this.userId = this.getUserIdFromStorage();
    console.log('userId résolu =', this.userId);
  }

  private getUserIdFromStorage(): number {
    // Étape 1 : essayer UserIdConnect direct
    const stored = localStorage.getItem('UserIdConnect');
    if (stored && stored !== 'undefined' && stored !== 'null') {
      const n = parseInt(stored, 10);
      if (!isNaN(n)) return n;
    }


    
    // Étape 2 : décoder depuis le JWT
    const token = localStorage.getItem('TokenUserConnect');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('JWT payload =', payload);
        // Cherche l'ID dans le payload
        if (payload.id) return Number(payload.id);
        if (payload.userId) return Number(payload.userId);
        if (payload.user_id) return Number(payload.user_id);
      } catch (e) {
        console.error('Erreur décodage JWT', e);
      }
    }

    // Étape 3 : chercher via email dans le backend (fallback)
    console.warn('userId introuvable dans localStorage');
    return 0;
  }

  
  private toDate(val: any): Date {
    if (!val) return new Date(NaN);
    if (Array.isArray(val)) {
      const [y, mo, d, h = 0, m = 0] = val;
      return new Date(y, mo - 1, d, h, m);
    }
    return new Date(val);
  }

ngOnInit(): void {
  this.buildWeek();
  this.load();

  // ← écoute les suppressions faites ailleurs
  this.scheduleRefresh.refresh$
    .pipe(takeUntil(this.destroy$))
    .subscribe(() => this.load());
}

ngOnDestroy(): void {
  this.destroy$.next();
  this.destroy$.complete();
}

  load(): void {
    if (this.userId === 0) {
      console.error('userId = 0, chargement annulé');
      return;
    }
    this.loading = true;
    const req = this.view === 'week'
      ? this.svc.getWeek(this.userId)
      : this.svc.getMonth(this.userId);

    req.subscribe({
      next: data => {
        this.events = data;
        this.loading = false;
        if (this.view === 'month') this.buildMonth();
      },
      error: () => this.loading = false
    });
  }

  switchView(v: 'week' | 'month'): void {
    this.view = v;
    this.load();
  }

  buildWeek(): void {
    const mon = this.getMonday(this.currentDate);
    this.weekDays = Array.from({ length: 7 }, (_, i) => {
      const d = new Date(mon);
      d.setDate(mon.getDate() + i);
      return d;
    });
  }

  getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    date.setDate(diff);
    return date;
  }

  prevWeek(): void {
    this.currentDate.setDate(this.currentDate.getDate() - 7);
    this.buildWeek(); this.load();
  }

  nextWeek(): void {
    this.currentDate.setDate(this.currentDate.getDate() + 7);
    this.buildWeek(); this.load();
  }

  slotEvents(day: Date, hour: number): ScheduleEvent[] {
    return this.events.filter(e => {
      const s = this.toDate(e.startTime);
      return s.toDateString() === day.toDateString()
          && s.getHours() === hour;
    });
  }

  buildMonth(): void {
    const y = this.currentDate.getFullYear();
    const m = this.currentDate.getMonth();
    this.monthLabel = this.currentDate.toLocaleString('fr-FR',
      { month: 'long', year: 'numeric' });
    const first = new Date(y, m, 1);
    const lastDay = new Date(y, m + 1, 0).getDate();
    const pad = (first.getDay() + 6) % 7;
    const days = Array.from({ length: lastDay }, (_, i) => new Date(y, m, i + 1));
    const totalCells = pad + lastDay;
    const extra = (7 - (totalCells % 7)) % 7;
    this.monthDays = [
      ...Array(pad).fill(null),
      ...days,
      ...Array(extra).fill(null)
    ];
  }

  dayEvents(day: Date | null): ScheduleEvent[] {
    if (!day) return [];
    return this.events.filter(e =>
      this.toDate(e.startTime).toDateString() === day.toDateString());
  }

  prevMonth(): void {
    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
    this.load();
  }

  nextMonth(): void {
    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
    this.load();
  }

  isToday(d: Date | null): boolean {
    return !!d && d.toDateString() === new Date().toDateString();
  }

  fmt(iso: any): string {
    return this.toDate(iso).toLocaleTimeString('fr-FR',
      { hour: '2-digit', minute: '2-digit' });
  }

  dayOfWeek(day: Date | null): string {
    return day ? this.DAY_NAMES[(day.getDay() + 6) % 7] : '';
  }

  dayNumber(day: Date): string {
    return String(day.getDate()).padStart(2, '0');
  }

  formatWeekDayRange(day: Date): string {
    return day.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' });
  }

  formatWeekDayFull(day: Date): string {
    return day.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  get conflicts(): number {
    return this.events.filter(e => e.hasConflict).length;
  }


  // Ajoute ces propriétés
selectedEvent: ScheduleEvent | null = null;
showModal = false;

// Ajoute cette méthode
openEvent(ev: ScheduleEvent): void {
  this.selectedEvent = ev;
  this.showModal = true;
}

closeModal(): void {
  this.showModal = false;
  this.selectedEvent = null;
}

getGoogleMapsLink(location: string): string {
  return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(location)}`;
}

getScoreColor(recommendation: string | undefined): string {
  if (recommendation === 'EXCELLENT') return '#2ecc71';
  if (recommendation === 'ACCEPTABLE') return '#f39c12';
  return '#e74c3c';
}
}