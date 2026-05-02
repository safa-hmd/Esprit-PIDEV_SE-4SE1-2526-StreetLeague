import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FieldReservationService } from '../../services/field-reservation.service';
import {
  Field,
  FieldReservation,
  FieldScheduleEntry,
  ReservationStatus,
  SportType , Payment, PaymentMethod
} from '../../models/field-reservation.model';

@Component({
  selector: 'app-field-reservation',
  templateUrl: './field-reservation.component.html',
  styleUrls: ['./field-reservation.component.css']
})
export class FieldReservationComponent implements OnInit {

  // ─── Data ───────────────────────────────────────────────────────
  pendingReservations: FieldReservation[] = [];
  allReservations:     FieldReservation[] = [];
  fields:              Field[]            = [];
  filteredFields:      Field[]            = [];
  // ── Payments ──────────────────────────────────────────────────────────────
  payments: Map<number, Payment> = new Map();

  // ─── UI state ───────────────────────────────────────────────────
  isLoading             = false;
  searchQuery           = '';
  showAddFieldModal     = false;
  showEditFieldModal    = false;
  showDetailModal       = false;
  showRejectNoteModal   = false;
  selectedReservation:  FieldReservation | null = null;
  selectedField:        Field | null = null;
  rejectTargetId:       number | null = null;
  adminNoteInput        = '';
  toastMessage          = '';
  toastType: 'success' | 'error' = 'success';
  showToast             = false;
  isSuggestingPrice  = false;
  showPriceSuggestion = false;
  suggestedPriceData: { suggestedPrice: number; basePrice: number; deltaPercent: number } | null = null;

  // ─── Forms ──────────────────────────────────────────────────────
  fieldForm!: FormGroup;
  sportTypes  = Object.values(SportType);
  ReservationStatus = ReservationStatus;

  constructor(
    private svc: FieldReservationService,
    private fb:  FormBuilder
  ) {}

  ngOnInit(): void {
    this.buildFieldForm();
    this.loadAll();
  }
    // ── Planning modal ────────────────────────────────────────────────────────
    isScheduleModalOpen  = false;
    scheduleField: Field | null = null;
    scheduleEntries: FieldScheduleEntry[] = [];
    isLoadingSchedule    = false;
    scheduleFrom: string = '';
    scheduleTo:   string = '';
  

  // ─── Loaders ────────────────────────────────────────────────────

  loadAll(): void {
    this.isLoading = true;
    this.loadFields();
    this.loadPendingReservations();
    this.loadAllReservations();
  }

  loadPendingReservations(): void {
    this.svc.getPendingReservations().subscribe({
      next: data => { this.pendingReservations = data; this.isLoading = false; },
      error: ()   => { this.toast('Failed to load pending reservations', 'error'); this.isLoading = false; }
    });
  }

  loadAllReservations(): void {
    this.svc.getAllReservations().subscribe({
      next: data => { this.allReservations = data;
        this.loadAllPayments();
       },
      error: ()   => {}
    });
  }



  loadFields(): void {
    this.svc.getAllFields().subscribe({
      next: data => {
        this.fields = data;
        this.applySearch();
      },
      error: () => this.toast('Failed to load fields', 'error')
    });
  }

  // ─── Stats ──────────────────────────────────────────────────────

  get availableFieldsCount(): number {
    return this.fields.filter(f => f.available).length;
  }

  get pendingCount(): number {
    return this.pendingReservations.length;
  }

  get monthlyReservations(): number {
    const now = new Date();
    return this.allReservations.filter(r => {
      const d = new Date(r.createdAt || '');
      return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
    }).length;
  }

  get monthlyRevenue(): number {
    const now = new Date();
    return this.allReservations
      .filter(r => {
        const d = new Date(r.createdAt || '');
        return d.getMonth() === now.getMonth() &&
               d.getFullYear() === now.getFullYear() &&
               r.statut === ReservationStatus.APPROVED;
      })
      .reduce((sum, r) => sum + (r.totalPrice ?? 0), 0);
  }

  // ─── Reservations actions ────────────────────────────────────────

  approve(id: number): void {
    this.svc.approveReservation(id).subscribe({
      next: () => { this.toast('Reservation approved'); this.loadAll(); },
      error: ()  => this.toast('Approval failed', 'error')
    });
  }

  openRejectModal(id: number): void {
    this.rejectTargetId  = id;
    this.adminNoteInput  = '';
    this.showRejectNoteModal = true;
  }

  confirmReject(): void {
    if (this.rejectTargetId == null) return;
    this.svc.rejectReservation(this.rejectTargetId, this.adminNoteInput || undefined).subscribe({
      next: () => {
        this.toast('Reservation rejected');
        this.showRejectNoteModal = false;
        this.loadAll();
      },
      error: () => this.toast('Rejection failed', 'error')
    });
  }

  viewReservationDetail(r: FieldReservation): void {
    this.selectedReservation = r;
    this.showDetailModal = true;
  }

  deleteReservation(id: number): void {
    if (!confirm('Delete this reservation permanently?')) return;
    this.svc.deleteReservation(id).subscribe({
      next: () => { this.toast('Reservation deleted'); this.loadAll(); },
      error: ()  => this.toast('Deletion failed', 'error')
    });
  }

  // ─── Field actions ───────────────────────────────────────────────

  openAddFieldModal(): void {
    this.fieldForm.reset({ available: true, sportType: SportType.FOOTBALL });
    this.showAddFieldModal = true;
  }

  openEditFieldModal(field: Field): void {
    this.selectedField = field;
    this.fieldForm.patchValue(field);
    this.showEditFieldModal = true;
  }

  saveNewField(): void {
    if (this.fieldForm.invalid) return;
    this.svc.createField(this.fieldForm.value as Field).subscribe({
      next: () => { this.toast('Field created'); this.showAddFieldModal = false; this.loadFields(); },
      error: err => {
      // Erreurs @Valid
      if (err.status === 400 && typeof err.error === 'object' && !err.error.message) {
        const messages = Object.entries(err.error)
          .map(([field, msg]) => `• ${field}: ${msg}`)
          .join('\n');
        this.toast(`Validation errors:\n${messages}`, 'error');
      } else {
        this.toast(err?.error?.message ?? 'Failed to create field', 'error');
      }
    }
  });
  }

  saveEditedField(): void {
    if (this.fieldForm.invalid || !this.selectedField?.id) return;
    this.svc.updateField(this.selectedField.id, this.fieldForm.value as Field).subscribe({
      next: () => { this.toast('Field updated'); this.showEditFieldModal = false; this.loadFields(); },
      error: err => {
      if (err.status === 400 && typeof err.error === 'object' && !err.error.message) {
        const messages = Object.entries(err.error)
          .map(([field, msg]) => `• ${field}: ${msg}`)
          .join('\n');
        this.toast(`Validation errors:\n${messages}`, 'error');
      } else {
        this.toast(err?.error?.message ?? 'Failed to update field', 'error');
      }
    }
  });
  }

  toggleField(field: Field): void {
    if (!field.id) return;
    this.svc.toggleAvailability(field.id).subscribe({
      next: updated => {
        const idx = this.fields.findIndex(f => f.id === field.id);
        if (idx !== -1) this.fields[idx] = updated;
        this.applySearch();
        this.toast(`Field marked as ${updated.available ? 'available' : 'reserved'}`);
      },
      error: () => this.toast('Toggle failed', 'error')
    });
  }

  deleteField(id: number): void {
    if (!confirm('Delete this field permanently?')) return;
    this.svc.deleteField(id).subscribe({
      next: () => { this.toast('Field deleted'); this.loadFields(); },
      error: ()  => this.toast('Deletion failed', 'error')
    });
  }

  // ─── Search ──────────────────────────────────────────────────────

  applySearch(): void {
    const q = this.searchQuery.toLowerCase().trim();
    this.filteredFields = q
      ? this.fields.filter(f =>
          f.nom.toLowerCase().includes(q) ||
          f.lieu.toLowerCase().includes(q) ||
          f.sportType.toLowerCase().includes(q)
        )
      : [...this.fields];
  }

  // ─── Helpers ─────────────────────────────────────────────────────

  formatSlot(r: FieldReservation): string {
    const fmt = (s: string) =>
      new Date(s).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
    return `${fmt(r.startTime)} – ${fmt(r.endTime)}`;
  }

  formatDate(dt: string | undefined): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  capacityLabel(capacity: number): string {
    return `${capacity}v${capacity}`;
  }

  statusClass(statut: ReservationStatus | undefined): string {
    switch (status) {
      case ReservationStatus.APPROVED:  return 'badge-approved';
      case ReservationStatus.REJECTED:  return 'badge-rejected';
      case ReservationStatus.CANCELLED: return 'badge-cancelled';
      default:                           return 'badge-pending';
    }
  }

  closeModals(): void {
    this.showAddFieldModal    = false;
    this.showEditFieldModal   = false;
    this.showDetailModal      = false;
    this.showRejectNoteModal  = false;
    this.selectedReservation  = null;
    this.selectedField        = null;
    this.showPriceSuggestion = false;
    this.suggestedPriceData  = null;
  }

  private buildFieldForm(): void {
    this.fieldForm = this.fb.group({
      nom:         ['', [Validators.required, Validators.minLength(3)]],
      description:  [''],
      sportType:    [SportType.FOOTBALL, Validators.required],
      lieu:     ['', Validators.required],
      imageUrl:     [''],
      pricePerHour: [null, [Validators.required, Validators.min(0)]],
      capacity:     [null, [Validators.required, Validators.min(1)]],
      available:    [true]
    });
  }

  private toast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType    = type;
    this.showToast    = true;
    setTimeout(() => (this.showToast = false), 3000);
  }
<<<<<<< HEAD
}



=======

  openSchedule(field: Field): void {
  this.scheduleField = field;
  this.isScheduleModalOpen = true;

  // Par défaut : mois en cours
  const now = new Date();
  const y   = now.getFullYear();
  const m   = String(now.getMonth() + 1).padStart(2, '0');
  this.scheduleFrom = `${y}-${m}-01`;
  this.scheduleTo   = `${y}-${m}-${new Date(y, now.getMonth() + 1, 0).getDate()}`;

  this.loadSchedule();
}

closeSchedule(): void {
  this.isScheduleModalOpen = false;
  this.scheduleField  = null;
  this.scheduleEntries = [];
}

groupedSchedule: { date: string; items: FieldScheduleEntry[] }[] = [];
loadSchedule(): void {
  if (!this.scheduleField || !this.scheduleFrom || !this.scheduleTo) return; // ✅
  this.isLoadingSchedule = true;
  this.svc.getFieldSchedule(this.scheduleField.id!, this.scheduleFrom, this.scheduleTo)
    .subscribe({
      next: (entries: FieldScheduleEntry[]) => {
        this.scheduleEntries  = entries;
        this.groupedSchedule  = this.groupByDate(entries);
        this.isLoadingSchedule = false;
      },
      error: () => {
        this.toast('❌ Erreur chargement planning', 'error');
        this.isLoadingSchedule = false;
      }
    });
}

onScheduleDateChange(): void {
  if (this.scheduleFrom && this.scheduleTo) { 
    this.loadSchedule();
  }
}

// ── Helpers planning ──────────────────────────────────────────────────────
getScheduleIcon(entry: FieldScheduleEntry): string {
  return entry.type === 'TOURNAMENT' ? '🏆' : '👤';
}

getScheduleStatusClass(status: string): string {
  const map: Record<string, string> = {
    APPROVED:  'status-approved',
    PENDING:   'status-pending',
    REJECTED:  'status-rejected',
    UPCOMING:  'status-upcoming',
    ONGOING:   'status-ongoing',
    COMPLETED: 'status-completed',
    CANCELLED: 'status-cancelled',
  };
  return map[status] ?? '';
}

groupByDate(entries: FieldScheduleEntry[]): { date: string; items: FieldScheduleEntry[] }[] {
  const map = new Map<string, FieldScheduleEntry[]>();
  for (const e of entries) {
    if (!map.has(e.date)) map.set(e.date, []);
    map.get(e.date)!.push(e);
  }
  return Array.from(map.entries()).map(([date, items]) => ({ date, items }));
}

loadAllPayments(): void {
  this.svc.getAllPayments().subscribe({
    next: data => {
      this.payments = new Map(data.map(p => [p.reservationId, p]));
    },
    error: () => {}
  });
}

refundPayment(reservationId: number): void {
  if (!confirm('Refund this payment?')) return; 
  this.svc.refundPayment(reservationId).subscribe({
    next: () => {
      this.toast('✅ Refund completed');
      this.loadAll();
    },
    error: () => this.toast('❌ Error occurred while refunding', 'error')
  });
}

getPaymentStatusClass(status: string): string {
  const map: Record<string, string> = {
    PAID:     'badge-approved',
    PENDING:  'badge-pending',
    REFUNDED: 'badge-cancelled',
    FAILED:   'badge-rejected',
  };
  return map[status] ?? '';
}

get approvedReservations(): FieldReservation[] {
  return this.allReservations.filter(r => r.status === 'APPROVED');
}


suggestPrice(): void {
  this.isSuggestingPrice   = true;
  this.showPriceSuggestion = false;
  this.suggestedPriceData  = null;

  const obs = this.selectedField?.id
    ? this.svc.getSuggestedPrice(this.selectedField.id, 1)
    : this.svc.getSuggestedPriceFromParams(
        this.fieldForm.value.sportType,
        this.fieldForm.value.location || 'Tunis',
        this.fieldForm.value.capacity || 10,
        1
      );

  obs.subscribe({
    next: (data) => {
      this.suggestedPriceData  = data;
      this.showPriceSuggestion = true;
      this.isSuggestingPrice   = false;
    },
    error: () => {
      this.isSuggestingPrice = false;
      this.toast('❌ Unable to get price suggestion', 'error');
    }
  });
}
 
applySuggestedPrice(): void {
  if (!this.suggestedPriceData) return;
  this.fieldForm.patchValue({ pricePerHour: this.suggestedPriceData.suggestedPrice });
  this.showPriceSuggestion = false;
}
 
closePriceSuggestion(): void {
  this.showPriceSuggestion = false;
  this.suggestedPriceData  = null;
}

}
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
