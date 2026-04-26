import { Component, OnInit, OnDestroy } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subject, takeUntil } from 'rxjs';
import { ScheduleEvent } from 'src/app/models/schedule-event.model';
import { ScheduleRefreshService } from 'src/app/services/schedule-refresh.service';
import { ScheduleService } from 'src/app/services/schedule.service';

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit, OnDestroy {

  // ── Tab ────────────────────────────────────────────────
  activeTab: 'schedule' | 'recommend' = 'schedule';

  // ── Data ───────────────────────────────────────────────
  events:  ScheduleEvent[] = [];
  view:    'week' | 'month' = 'week';
  loading = false;
  userId  = 0;

  // ── GPS ────────────────────────────────────────────────
  userLat: number | undefined;
  userLng: number | undefined;
  geoReady = false;

  // ── Calendar ───────────────────────────────────────────
  weekDays:    Date[] = [];
  hours = Array.from({ length: 15 }, (_, i) => i + 8);
  monthDays:   (Date | null)[] = [];
  monthLabel = '';
  currentDate = new Date();
  readonly DAY_NAMES = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];

  // ── Modal ──────────────────────────────────────────────
  selectedEvent: ScheduleEvent | null = null;
  showModal = false;
  mapUrl: SafeResourceUrl | null = null;

  private destroy$ = new Subject<void>();

  constructor(
    private svc: ScheduleService,
    private scheduleRefresh: ScheduleRefreshService,
    private sanitizer: DomSanitizer
  ) {
    this.userId = this.resolveUserId();
  }

  private resolveUserId(): number {
    const stored = localStorage.getItem('UserIdConnect');
    if (stored && stored !== 'undefined' && stored !== 'null') {
      const n = Number(stored);
      if (!isNaN(n) && n > 0) return n;
    }
    const token = localStorage.getItem('TokenUserConnect');
    if (token) {
      try {
        const p = JSON.parse(atob(token.split('.')[1]));
        return Number(p.id || p.userId || p.user_id || 0);
      } catch (_) {}
    }
    return 0;
  }

  ngOnInit(): void {
    this.buildWeek();

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        pos => {
          this.userLat  = pos.coords.latitude;
          this.userLng  = pos.coords.longitude;
          this.geoReady = true;
          this.load();
        },
        () => { this.geoReady = true; this.load(); },
        { timeout: 5000 }
      );
    } else {
      this.geoReady = true;
      this.load();
    }

    this.scheduleRefresh.refresh$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.load());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ── Load ───────────────────────────────────────────────
  load(): void {
    if (this.userId === 0) return;
    this.loading = true;

    const req$ = this.view === 'week'
      ? this.svc.getWeek(this.userId, this.userLat, this.userLng)
      : this.svc.getMonth(this.userId, this.userLat, this.userLng);

    req$.subscribe({
      next: data => {
        this.events  = data;
        this.loading = false;
        if (this.view === 'month') this.buildMonth();
      },
      error: () => { this.loading = false; }
    });
  }

  switchView(v: 'week' | 'month'): void {
    this.view = v;
    if (v === 'month') this.buildMonth();
    this.load();
  }

  // ── Modal ──────────────────────────────────────────────
  openEvent(ev: ScheduleEvent): void {
    this.selectedEvent = ev;
    this.showModal = true;
    this.mapUrl = null;

    if (ev.location) {
      const url = `https://maps.google.com/maps?q=${encodeURIComponent(ev.location)}&output=embed&z=14`;
      this.mapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
  }

  closeModal(): void {
    this.showModal    = false;
    this.selectedEvent = null;
    this.mapUrl       = null;
  }

  getEventDirections(ev: ScheduleEvent): string {
    if (!ev.location) return '#';
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(ev.location)}`;
  }

  getFieldDirections(ev: ScheduleEvent): string {
    if (ev.recommendedFieldLat && ev.recommendedFieldLng) {
      return `https://www.google.com/maps/dir/?api=1&destination=${ev.recommendedFieldLat},${ev.recommendedFieldLng}`;
    }
    if (ev.recommendedFieldLocation) {
      return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(ev.recommendedFieldLocation)}`;
    }
    return '#';
  }

  // ── Display helpers ────────────────────────────────────
  getScoreColor(rec: string | undefined): string {
    if (rec === 'EXCELLENT')  return '#34d399';
    if (rec === 'ACCEPTABLE') return '#fbbf24';
    return '#f87171';
  }

  getEventIcon(type: string): string {
    switch (type) {
      case 'MATCH':      return '⚽';
      case 'TRAINING':   return '🏋️';
      case 'TOURNAMENT': return '🏆';
      default:           return '📅';
    }
  }

  getScorePercent(score: number | undefined): number {
    return Math.round((score || 0) * 100);
  }

  private toDate(val: any): Date {
    if (!val) return new Date(NaN);
    if (Array.isArray(val)) {
      const [y, mo, d, h = 0, m = 0] = val;
      return new Date(y, mo - 1, d, h, m);
    }
    return new Date(val);
  }

  fmt(val: any): string {
    const d = this.toDate(val);
    return isNaN(d.getTime()) ? '--:--' :
      d.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }

  fmtDate(val: any): string {
    const d = this.toDate(val);
    return isNaN(d.getTime()) ? '' :
      d.toLocaleDateString('fr-FR', { weekday: 'long', day: '2-digit', month: 'long', year: 'numeric' });
  }

  // ── Week calendar ──────────────────────────────────────
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
    this.buildWeek();
    this.load();
  }

  nextWeek(): void {
    this.currentDate.setDate(this.currentDate.getDate() + 7);
    this.buildWeek();
    this.load();
  }

  slotEvents(day: Date, hour: number): ScheduleEvent[] {
    return this.events.filter(e => {
      const s = this.toDate(e.startTime);
      return s.toDateString() === day.toDateString() && s.getHours() === hour;
    });
  }

  formatWeekDayRange(day: Date): string {
    return day.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' });
  }

  formatWeekDayFull(day: Date): string {
    return day.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  // ── Month calendar ─────────────────────────────────────
  buildMonth(): void {
    const y = this.currentDate.getFullYear();
    const m = this.currentDate.getMonth();
    this.monthLabel = this.currentDate.toLocaleString('fr-FR', { month: 'long', year: 'numeric' });
    const first   = new Date(y, m, 1);
    const lastDay = new Date(y, m + 1, 0).getDate();
    const pad     = (first.getDay() + 6) % 7;
    const days    = Array.from({ length: lastDay }, (_, i) => new Date(y, m, i + 1));
    const total   = pad + lastDay;
    const extra   = (7 - (total % 7)) % 7;
    this.monthDays = [...Array(pad).fill(null), ...days, ...Array(extra).fill(null)];
  }

  dayEvents(day: Date | null): ScheduleEvent[] {
    if (!day) return [];
    return this.events.filter(e =>
      this.toDate(e.startTime).toDateString() === day.toDateString()
    );
  }

  prevMonth(): void {
    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
    this.buildMonth();
    this.load();
  }

  nextMonth(): void {
    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
    this.buildMonth();
    this.load();
  }

  isToday(d: Date | null): boolean {
    return !!d && d.toDateString() === new Date().toDateString();
  }

  dayOfWeek(day: Date | null): string {
    return day ? this.DAY_NAMES[(day.getDay() + 6) % 7] : '';
  }

  dayNumber(day: Date): string {
    return String(day.getDate()).padStart(2, '0');
  }

  get conflicts(): number {
    return this.events.filter(e => e.hasConflict).length;
  }
}