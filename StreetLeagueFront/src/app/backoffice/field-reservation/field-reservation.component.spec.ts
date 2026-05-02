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

  pendingReservations: FieldReservation[] = [];
  allReservations:     FieldReservation[] = [];
  fields:              Field[]            = [];
  filteredFields:      Field[]            = [];

  isLoading = false;
  searchQuery = '';

  showAddFieldModal   = false;
  showEditFieldModal  = false;
  showDetailModal     = false;
  showRejectNoteModal = false;

  selectedReservation: FieldReservation | null = null;
  selectedField: Field | null = null;

  rejectTargetId: number | null = null;
  adminNoteInput = '';

  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  fieldForm!: FormGroup;
  sportTypes = Object.values(SportType);
  ReservationStatus = ReservationStatus;

  constructor(
    private svc: FieldReservationService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.buildFieldForm();
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;
    this.loadFields();
    this.loadPendingReservations();
    this.loadAllReservations();
  }

  loadPendingReservations(): void {
    this.svc.getPendingReservations().subscribe({
      next: data => {
        this.pendingReservations = data;
        this.isLoading = false;
      },
      error: () => {
        this.toast('Failed to load pending reservations', 'error');
        this.isLoading = false;
      }
    });
  }

  loadAllReservations(): void {
    this.svc.getAllReservations().subscribe({
      next: data => this.allReservations = data
    });
  }

  loadFields(): void {
    this.svc.getAllFields().subscribe({
      next: data => {
        this.fields = data || [];   // ✅ sécurité ajoutée
        this.applySearch();
      },
      error: () => this.toast('Failed to load fields', 'error')
    });
  }

  // ✅ FIX SAFE
  get availableFieldsCount(): number {
    return (this.fields ?? []).filter(f => f.available === true).length;
  }

  get pendingCount(): number {
    return this.pendingReservations.length;
  }

  get monthlyReservations(): number {
    const now = new Date();
    return this.allReservations.filter(r => {
      const d = new Date(r.createdAt || '');
      return d.getMonth() === now.getMonth() &&
             d.getFullYear() === now.getFullYear();
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

  private buildFieldForm(): void {
    this.fieldForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      sportType: [SportType.FOOTBALL, Validators.required],
      lieu: ['', Validators.required],
      imageUrl: [''],
      pricePerHour: [null, [Validators.required, Validators.min(0)]],
      capacity: [null, [Validators.required, Validators.min(1)]],
      available: [true]
    });
  }

  private toast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}

