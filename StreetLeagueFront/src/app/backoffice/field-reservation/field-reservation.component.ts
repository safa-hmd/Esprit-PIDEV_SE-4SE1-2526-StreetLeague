import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FieldReservationService } from '../../services/field-reservation.service';
import {
  Field,
  FieldReservation,
  ReservationStatus,
  SportType
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
      next: data => { this.allReservations = data; },
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
               r.status === ReservationStatus.APPROVED;
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
          f.name.toLowerCase().includes(q) ||
          f.location.toLowerCase().includes(q) ||
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

  statusClass(status: ReservationStatus | undefined): string {
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
  }

  private buildFieldForm(): void {
    this.fieldForm = this.fb.group({
      name:         ['', [Validators.required, Validators.minLength(3)]],
      description:  [''],
      sportType:    [SportType.FOOTBALL, Validators.required],
      location:     ['', Validators.required],
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
}