import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FieldReservationService } from '../../services/field-reservation.service';
import {
  Field,
  FieldReservation,
  ReservationStatus,
  SportType
} from '../../models/field-reservation.model';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-field-reservation-front',
  templateUrl: './field-reservation.component.html',
  styleUrls: ['./field-reservation.component.css']
})
export class FieldReservationComponent implements OnInit {

  // ─── Data ────────────────────────────────────────────────────────
  allFields:       Field[]            = [];
  filteredFields:  Field[]            = [];
  myReservations:  FieldReservation[] = [];
  allReservations: FieldReservation[] = [];

  // ─── Filters ─────────────────────────────────────────────────────
  searchQuery       = '';
  selectedSport     = '';
  selectedAvail     = '';
  sportTypes        = Object.values(SportType);

  // ─── UI State ────────────────────────────────────────────────────
  isLoading         = false;
  showBookingModal  = false;
  showMyBookings    = false;
  selectedField:    Field | null = null;
  toastMessage      = '';
  toastType: 'success' | 'error' = 'success';
  showToast         = false;

  // ─── Form ────────────────────────────────────────────────────────
  bookingForm!: FormGroup;

  // ─── Sport emojis ────────────────────────────────────────────────
  sportEmoji: Record<string, string> = {
    FOOTBALL:   '⚽',
    BASKETBALL: '🏀',
    TENNIS:     '🎾',
    PADEL:      '🏓',
    VOLLEYBALL: '🏐',
    OTHER:      '🏟️'
  };

  constructor(
    private svc: FieldReservationService,
    private fb:  FormBuilder,
    private authService: AuthService 
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadFields();
    this.loadAllReservations();
    this.loadMyReservations();
  }

  // ─── Loaders ─────────────────────────────────────────────────────

  loadFields(): void {
    this.isLoading = true;
    this.svc.getAllFields().subscribe({
      next: data => {
        this.allFields = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => { this.toast('Failed to load fields', 'error'); this.isLoading = false; }
    });
  }

  loadAllReservations(): void {
    this.svc.getAllReservations().subscribe({
      next: data => { this.allReservations = data; },
      error: () => {}
    });
  }

  loadMyReservations(): void {
    const pid = this.getPlayerId();
    if (!pid) return;
    this.svc.getReservationsByPlayer(pid).subscribe({
      next: data => { this.myReservations = data; },
      error: () => {}
    });
  }

  // ─── Stats ───────────────────────────────────────────────────────

  get totalFields(): number { return this.allFields.length; }

  get availableNow(): number { return this.allFields.filter(f => f.available).length; }

  get monthlyReservations(): number {
    const now = new Date();
    return this.allReservations.filter(r => {
      const d = new Date(r.createdAt || '');
      return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
    }).length;
  }

  get averagePrice(): number {
    if (!this.allFields.length) return 0;
    const total = this.allFields.reduce((s, f) => s + (f.pricePerHour || 0), 0);
    return Math.round(total / this.allFields.length);
  }

  // ─── Filters ─────────────────────────────────────────────────────

  applyFilters(): void {
    let result = [...this.allFields];

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(f =>
        f.name.toLowerCase().includes(q) ||
        f.location.toLowerCase().includes(q)
      );
    }

    if (this.selectedSport) {
      result = result.filter(f => f.sportType === this.selectedSport);
    }

    if (this.selectedAvail === 'available') {
      result = result.filter(f => f.available);
    } else if (this.selectedAvail === 'unavailable') {
      result = result.filter(f => !f.available);
    }

    this.filteredFields = result;
  }

  clearFilters(): void {
    this.searchQuery   = '';
    this.selectedSport = '';
    this.selectedAvail = '';
    this.applyFilters();
  }

  // ─── Booking ─────────────────────────────────────────────────────

  openBookingModal(field: Field): void {
    this.selectedField = field;
    this.bookingForm.reset();
    this.showBookingModal = true;
  }

  submitBooking(): void {
    if (this.bookingForm.invalid || !this.selectedField?.id) return;

    const pid = this.getPlayerId();
    if (!pid) { this.toast('Please log in to make a reservation', 'error'); return; }

    const { date, startHour, endHour } = this.bookingForm.value;

    const startTime = `${date}T${startHour}:00`;
    const endTime   = `${date}T${endHour}:00`;

    if (endHour <= startHour) {
      this.toast('End time must be after start time', 'error');
      return;
    }

    const dto: FieldReservation = {
      fieldId:   this.selectedField.id,
      playerId:  pid,
      startTime,
      endTime
    };

    this.svc.createReservation(dto).subscribe({
      next: () => {
        this.toast('Reservation submitted successfully!');
        this.showBookingModal = false;
        this.loadMyReservations();
        this.loadAllReservations();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Booking failed. Slot may be unavailable.';
        this.toast(msg, 'error');
      }
    });
  }

  cancelMyReservation(id: number): void {
    const pid = this.getPlayerId();
    if (!pid) return;
    if (!confirm('Cancel this reservation?')) return;
    this.svc.cancelReservation(id, pid).subscribe({
      next: () => { this.toast('Reservation cancelled'); this.loadMyReservations(); },
      error: ()  => this.toast('Cancellation failed', 'error')
    });
  }

  // ─── Helpers ─────────────────────────────────────────────────────

  getEmoji(sport: string): string {
    return this.sportEmoji[sport] || '🏟️';
  }

  statusClass(status: ReservationStatus | undefined): string {
    switch (status) {
      case ReservationStatus.APPROVED:  return 'badge-approved';
      case ReservationStatus.REJECTED:  return 'badge-rejected';
      case ReservationStatus.CANCELLED: return 'badge-cancelled';
      default:                           return 'badge-pending';
    }
  }

  canCancel(status: ReservationStatus | undefined): boolean {
    return status === ReservationStatus.PENDING || status === ReservationStatus.APPROVED;
  }

  formatDate(dt: string | undefined): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  formatSlot(r: FieldReservation): string {
    const fmt = (s: string) =>
      new Date(s).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
    return `${fmt(r.startTime)} – ${fmt(r.endTime)}`;
  }

  estimatedPrice(): number {
    if (!this.selectedField || this.bookingForm.invalid) return 0;
    const { startHour, endHour } = this.bookingForm.value;
    if (!startHour || !endHour || endHour <= startHour) return 0;
    const hours = parseInt(endHour) - parseInt(startHour);
    return hours * (this.selectedField.pricePerHour || 0);
  }

  todayDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  closeModals(): void {
    this.showBookingModal = false;
    this.showMyBookings   = false;
    this.selectedField    = null;
  }

  private getPlayerId(): number | null {
  return this.authService.getUserId(); // ✅ utilise le JWT decode
}

  private buildForm(): void {
    this.bookingForm = this.fb.group({
      date:      ['', Validators.required],
      startHour: ['', Validators.required],
      endHour:   ['', Validators.required]
    });
  }

  private toast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType    = type;
    this.showToast    = true;
    setTimeout(() => (this.showToast = false), 3500);
  }

  get ReservationStatus() { return ReservationStatus; }
}