// src/app/backoffice/tournaments/tournament.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { TournamentService } from 'src/app/services/tournament.service';
import {
  TournamentDto, TournamentRegistrationDto,
  SportType, TournamentStatus, TournamentType
} from './tournament.model';

import { FieldScheduleEntry } from 'src/app/models/field-reservation.model';
import { FieldReservationService } from 'src/app/services/field-reservation.service';
import { Field } from 'src/app/models/field-reservation.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-tournament',
  templateUrl: './tournaments.component.html',
  styleUrls: ['./tournaments.component.css']
})
export class TournamentComponent implements OnInit, OnDestroy {

  // ── Data ──────────────────────────────────────────────────────────────────
  allTournaments: TournamentDto[]      = [];
  filteredTournaments: TournamentDto[] = [];
  isLoading    = false;
  errorMessage = '';
  fields: Field[] = [];

  // ── Toast ─────────────────────────────────────────────────────────────────
  toastMessage: string | null = null;

  // ── Registration modal (admin view) ───────────────────────────────────────
  isRequestsModalOpen = false;
  selectedTournament: TournamentDto | null = null;
  pendingRegistrations: TournamentRegistrationDto[] = [];
  isLoadingRequests = false;

  // ── Create / Edit modal ───────────────────────────────────────────────────
  isFormModalOpen    = false;
  editingTournament: TournamentDto | null = null;
  tournamentForm!: FormGroup;
  isSaving = false;


  // ── Options ───────────────────────────────────────────────────────────────
  readonly sportOptions: { value: SportType; label: string }[] = [
    { value: 'FOOTBALL',   label: 'Football'   },
    { value: 'BASKETBALL', label: 'Basketball' },
    { value: 'VOLLEYBALL', label: 'Volleyball' },
    { value: 'TENNIS',     label: 'Tennis'     },
    { value: 'PADEL',      label: 'Padel'      },
    { value: 'OTHER',      label: 'Other'      },
  ];

  readonly statusOptions: { value: TournamentStatus; label: string }[] = [
    { value: 'UPCOMING',  label: 'Open'        },
    { value: 'ONGOING',   label: 'In Progress' },
    { value: 'COMPLETED', label: 'Completed'   },
    { value: 'CANCELLED', label: 'Cancelled'   },
  ];

  readonly typeOptions: { value: TournamentType; label: string }[] = [
    { value: 'INDIVIDUAL', label: 'Individual' },
    { value: 'TEAM',       label: 'Team'       },
  ];

  private destroy$ = new Subject<void>();

  constructor(public svc: TournamentService, private fb: FormBuilder , private fieldService: FieldReservationService) {}

  ngOnInit(): void {
    this.svc.toast$.pipe(takeUntil(this.destroy$)).subscribe(m => this.toastMessage = m);
    this.svc.filters$.pipe(takeUntil(this.destroy$)).subscribe(f => {
      this.filteredTournaments = this.svc.applyFilters(this.allTournaments, f);
  
    });
    this.buildForm();
    this.load();
    this.loadFields();

  }
  loadFields() {
  this.fieldService.getAllFields().subscribe(f => this.fields = f);
}
  

  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  // ── Forms ─────────────────────────────────────────────────────────────────

  private buildForm(): void {
    this.tournamentForm = this.fb.group({
      name:                 ['', [Validators.required, Validators.minLength(3)]],
      description:          [''],
      sportType:            ['FOOTBALL',   Validators.required],
      tournamentType:       ['TEAM',       Validators.required],
      startDate:            ['',           Validators.required],
      endDate:              ['',           Validators.required],
      registrationDeadline: ['',           Validators.required],
      maxParticipants:      [8, [Validators.required, Validators.min(2)]],
      fieldId: [null, Validators.required],
      prizePool:            [0],
    });
  }

  // ── Load ──────────────────────────────────────────────────────────────────

  load(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.svc.getAll().pipe(takeUntil(this.destroy$)).subscribe({
      next: data => {
        this.allTournaments = data;
        this.filteredTournaments = this.svc.applyFilters(data, this.svc.getFilters());
        this.isLoading = false;
      },
      error: () => { this.errorMessage = 'Failed to load tournaments.'; this.isLoading = false; }
    });
  }

  // ── Filters ───────────────────────────────────────────────────────────────

  onSearch(e: Event): void {
    this.svc.updateFilters({ search: (e.target as HTMLInputElement).value });
  }
  onSportFilter(e: Event): void {
    this.svc.updateFilters({ sport: (e.target as HTMLSelectElement).value as SportType | '' });
  }
  onStatusFilter(e: Event): void {
    this.svc.updateFilters({ status: (e.target as HTMLSelectElement).value as TournamentStatus | '' });
  }

  // ── Requests modal (Admin: see PENDING registrations) ─────────────────────

  openRequests(t: TournamentDto): void {
    this.selectedTournament = t;
    this.isRequestsModalOpen = true;
    this.isLoadingRequests = true;
    this.pendingRegistrations = [];

    this.svc.getRegistrationsByTournament(t.id!).pipe(takeUntil(this.destroy$)).subscribe({
      next: regs => {
        // Only show PENDING registrations to admin
        this.pendingRegistrations = regs.filter(r => r.status === 'PENDING');
        this.isLoadingRequests = false;
      },
      error: () => {
        this.svc.showToast('❌ Failed to load registrations.');
        this.isLoadingRequests = false;
      }
    });
  }

  closeRequests(): void {
    this.isRequestsModalOpen = false;
    this.selectedTournament = null;
    this.pendingRegistrations = [];
  }

  acceptRegistration(reg: TournamentRegistrationDto): void {
    if (!reg.id) return;
    this.svc.acceptRegistration(reg.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.svc.showToast('✅ Registration accepted!');
        // Remove from pending list instantly
        this.pendingRegistrations = this.pendingRegistrations.filter(r => r.id !== reg.id);
        this.load(); // refresh card counts
      },
      error: err => this.svc.showToast(`❌ ${err?.error?.message ?? 'Error accepting'}`)
    });
  }

  rejectRegistration(reg: TournamentRegistrationDto): void {
    if (!reg.id) return;
    this.svc.rejectRegistration(reg.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.svc.showToast('🚫 Registration rejected.');
        this.pendingRegistrations = this.pendingRegistrations.filter(r => r.id !== reg.id);
      },
      error: err => this.svc.showToast(`❌ ${err?.error?.message ?? 'Error rejecting'}`)
    });
  }

  // ── Create / Edit modal ───────────────────────────────────────────────────

  openCreate(): void {
    this.editingTournament = null;
    this.tournamentForm.reset({ sportType: 'FOOTBALL', tournamentType: 'TEAM', maxParticipants: 8 });
    this.isFormModalOpen = true;
  }

  openEdit(t: TournamentDto): void {
    this.editingTournament = t;
    this.tournamentForm.patchValue(t);
    this.isFormModalOpen = true;
  }

  closeForm(): void { this.isFormModalOpen = false; this.editingTournament = null; }

  submitForm(): void {
    if (this.tournamentForm.invalid) return;
    this.isSaving = true;
     const raw = this.tournamentForm.value;
      const dto: TournamentDto = {
    ...raw,
    fieldId: raw.fieldId ? Number(raw.fieldId) : null  // ✅ forcer en number
  };
    const call$ = this.editingTournament?.id
      ? this.svc.update(this.editingTournament.id, dto)
      : this.svc.create(dto);

    call$.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.isSaving = false;
        this.svc.showToast(this.editingTournament ? '✅ Tournament updated!' : '✅ Tournament created!');
        this.closeForm();
        this.load();
      },
       
    error: err => {
      this.isSaving = false;

      // Erreurs @Valid → { name: "...", location: "..." }
      if (err.status === 400 && typeof err.error === 'object' && !err.error.message) {
        const messages = Object.entries(err.error)
          .map(([field, msg]) => `• ${field}: ${msg}`)
          .join('\n');
        this.svc.showToast(`❌ ${messages}`);

      // Erreur métier → { message: "Tournament with this name already exists" }
      } else {
        const msg = err?.error?.message ?? err?.error?.error ?? 'Error saving tournament';
        this.svc.showToast(`❌ ${msg}`);
      }
    }
      });
  }

  // ── Admin actions ─────────────────────────────────────────────────────────

  cancelTournament(t: TournamentDto): void {
    if (!t.id || !confirm(`Cancel "${t.name}"?`)) return;
    this.svc.cancel(t.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => { this.svc.showToast('Tournament cancelled.'); this.load(); },
      error: err => this.svc.showToast(`❌ ${err?.error?.message ?? 'Error'}`)
    });
  }

  deleteTournament(t: TournamentDto): void {
    if (!t.id || !confirm(`Permanently delete "${t.name}"?`)) return;
    this.svc.delete(t.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => { this.svc.showToast('Tournament deleted.'); this.load(); },
      error: () => this.svc.showToast('❌ Error deleting tournament.')
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  pendingCount(t: TournamentDto): number {
    // Badge count — loaded lazily, just show icon without count on card
    return 0;
  }

  trackById(_: number, t: TournamentDto) { return t.id; }
  trackByRegId(_: number, r: TournamentRegistrationDto) { return r.id; }
 // ─────────────────────────────────────────────────────────────────────────────
// MÉTHODES À AJOUTER dans tournament.component.ts
// Colle ces méthodes dans la section "── Helpers ──" (à la fin de la classe)
// NE SUPPRIME RIEN D'EXISTANT — ajoute juste ces méthodes
// ─────────────────────────────────────────────────────────────────────────────

  // ── Stats pour la ligne de KPIs ───────────────────────────────────────────
  getTotalRegistered(): number {
    return this.allTournaments.reduce((sum, t) => sum + (t.registeredCount ?? 0), 0);
  }

  getTotalPrize(): number {
    return this.allTournaments.reduce((sum, t) => sum + (t.prizePool ?? 0), 0);
  }

  getCountByStatus(status: TournamentStatus): number {
    return this.allTournaments.filter(t => t.status === status).length;
  }

  // ── Classes CSS dynamiques ─────────────────────────────────────────────────
  getStatusClass(status: TournamentStatus): string {
    const map: Record<TournamentStatus, string> = {
      'UPCOMING':  'status-upcoming',
      'ONGOING':   'status-ongoing',
      'COMPLETED': 'status-completed',
      'CANCELLED': 'status-cancelled',
    };
    return map[status] ?? '';
  }

  getProgressClass(pct: number): string {
    if (pct >= 100) return 'progress-full';
    if (pct >= 75)  return 'progress-high';
    if (pct >= 40)  return 'progress-mid';
    return 'progress-low';
  }

  

}