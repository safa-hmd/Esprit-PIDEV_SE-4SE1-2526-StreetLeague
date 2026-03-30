// src/app/frontoffice/tournaments/tournaments.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule }                  from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { TournamentService }   from '../../services/tournament.service';
import { TeamService }         from '../../services/team.service';
import { AuthService }         from '../../services/auth.service';
import {
  TournamentDto,
  TournamentRegistrationDto,
  SportType,
  TournamentStatus
} from '../../backoffice/tournaments/tournament.model';
import { Team } from '../../models/team.model';
import { forkJoin, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';


@Component({
  selector: 'app-tournaments',
  templateUrl: './tournaments.component.html',
  styleUrls: ['./tournaments.component.css']
})
export class TournamentsComponent implements OnInit, OnDestroy {

  // ── Data ──────────────────────────────────────────────────────────────────
  allTournaments: TournamentDto[]      = [];
  filteredTournaments: TournamentDto[] = [];
  isLoading    = false;
  errorMessage = '';

  // ── Toast ─────────────────────────────────────────────────────────────────
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  // ── Register modal ────────────────────────────────────────────────────────
  isRegisterModalOpen  = false;
  selectedTournament: TournamentDto | null = null;
  registerForm!: FormGroup;
  isSubmitting = false;

  // ── Teams (pour tournois TEAM) ────────────────────────────────────────────
  availableTeams: Team[]    = [];
  isLoadingTeams            = false;
  currentUserId: number | null = null;
  currentUserEmail: string | null = null;

  // ── My registrations modal ────────────────────────────────────────────────
  isMyRegsModalOpen  = false;
  myRegistrations: TournamentRegistrationDto[] = [];
  isLoadingMyRegs    = false;

  // ── Filters ───────────────────────────────────────────────────────────────
  searchTerm   = '';
  sportFilter: SportType | '' = '';
  statusFilter: TournamentStatus | '' = '';

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
  ];

  private destroy$ = new Subject<void>();

  constructor(
    public svc: TournamentService,
    public auth: AuthService,
    private teamService: TeamService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    // Récupérer les infos de l'utilisateur connecté depuis le JWT
    this.currentUserId    = this.auth.getUserId() ? +this.auth.getUserId()! : null;
    this.currentUserEmail = this.auth.getEmail();

    // Formulaire : teamId requis seulement pour les tournois TEAM
    this.registerForm = this.fb.group({
      teamId: [null]
    });

    this.load();
  }

  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  // ── Load ──────────────────────────────────────────────────────────────────

  load(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.svc.getAll().pipe(takeUntil(this.destroy$)).subscribe({
      next: data => {
        this.allTournaments = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load tournaments. Please try again.';
        this.isLoading = false;
      }
    });
  }

  // ── Filters ───────────────────────────────────────────────────────────────

  onSearch(e: Event): void {
    this.searchTerm = (e.target as HTMLInputElement).value;
    this.applyFilters();
  }

  onSportFilter(e: Event): void {
    this.sportFilter = (e.target as HTMLSelectElement).value as SportType | '';
    this.applyFilters();
  }

  onStatusFilter(e: Event): void {
    this.statusFilter = (e.target as HTMLSelectElement).value as TournamentStatus | '';
    this.applyFilters();
  }

  private applyFilters(): void {
    this.filteredTournaments = this.allTournaments.filter(t => {
      const matchSearch = !this.searchTerm ||
        t.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (t.location ?? '').toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchSport  = !this.sportFilter  || t.sportType === this.sportFilter;
      const matchStatus = !this.statusFilter || t.status    === this.statusFilter;
      return matchSearch && matchSport && matchStatus;
    });
  }

  // ── Register modal ────────────────────────────────────────────────────────

  openRegister(t: TournamentDto): void {
    // Vérifier que l'utilisateur est connecté
    if (!this.currentUserId) {
      this.showToast('❌ Please log in to register for a tournament.', 'error');
      return;
    }

    this.selectedTournament = t;
    this.availableTeams = [];
    this.registerForm.reset({ teamId: null });

    // Si tournoi TEAM → charger les équipes disponibles
    //if (t.tournamentType === 'TEAM') {
      //this.loadTeams();
    //}
    // ← Ajoute / retire Validators.required selon le type de tournoi
  const teamIdCtrl = this.registerForm.get('teamId')!;
  if (t.tournamentType === 'TEAM') {
    teamIdCtrl.setValidators([Validators.required]);
    this.loadTeams();
  } else {
    teamIdCtrl.clearValidators();
  }
  teamIdCtrl.updateValueAndValidity();  // ← indispensable pour que Angular recalcule


    this.isRegisterModalOpen = true;
  }

  closeRegister(): void {
    this.isRegisterModalOpen = false;
    this.selectedTournament  = null;
    this.availableTeams      = [];
  }

  /**
   * Charge toutes les équipes disponibles pour que le joueur choisisse la sienne.
   * On utilise GET /team/showTeams car il n'existe pas d'endpoint "mes équipes en tant que membre".
   */
  private loadTeams(): void {
    this.isLoadingTeams = true;
    this.teamService.getAllTeams().pipe(takeUntil(this.destroy$)).subscribe({
      next: teams => {
        this.availableTeams = teams;
        this.isLoadingTeams = false;
        // Pré-sélectionner si une seule équipe
        if (teams.length === 1) {
          this.registerForm.patchValue({ teamId: teams[0].idTeam });
        }
      },
      error: () => {
        this.isLoadingTeams = false;
        this.showToast('❌ Failed to load teams.', 'error');
      }
    });
  }

  isTeamTournament(): boolean {
    return this.selectedTournament?.tournamentType === 'TEAM';
  }

  submitRegistration(): void {
    if (!this.selectedTournament?.id || !this.currentUserId) return;

    // Validation selon le type
    //if (this.isTeamTournament()) {
      //const teamId = this.registerForm.value.teamId;
      //if (!teamId) {
       // this.showToast('❌ Please select a team.', 'error');
       // return;
    // }
    //}
    //
    if (this.registerForm.invalid) return;

    this.isSubmitting = true;
    const tid = this.selectedTournament.id!;

    const call$ = this.isTeamTournament()
      ? this.svc.registerTeam(tid, +this.registerForm.value.teamId)
      : this.svc.registerPlayer(tid, this.currentUserId);

    call$.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.closeRegister();
        this.showToast('✅ Registration submitted! Waiting for admin approval.', 'success');
        this.load();
      },
      error: err => {
        this.isSubmitting = false;
        const msg = err?.error?.message ?? err?.error ?? 'Registration failed.';
        this.showToast(`❌ ${msg}`, 'error');
      }
    });
  }

  // ── My Registrations modal ────────────────────────────────────────────────

  openMyRegistrations(): void {
  if (!this.currentUserId) {
    this.showToast('❌ Unable to identify user. Please log in again.', 'error');
    return;
  }

  this.isMyRegsModalOpen = true;
  this.isLoadingMyRegs   = true;
  this.myRegistrations   = [];

  forkJoin({
    individual: this.svc.getRegistrationsByPlayer(this.currentUserId),
    team:       this.svc.getTeamRegistrationsByPlayer(this.currentUserId)
  })
  .pipe(takeUntil(this.destroy$))
  .subscribe({
    next: ({ individual, team }) => {
      this.myRegistrations = [...individual, ...team]
        .sort((a, b) =>
          new Date(b.registeredAt ?? '').getTime() -
          new Date(a.registeredAt ?? '').getTime()
        );
      this.isLoadingMyRegs = false;
    },
    error: () => {
      this.showToast('❌ Failed to load your registrations.', 'error');
      this.isLoadingMyRegs = false;
      this.isMyRegsModalOpen = false;
    }
  });
}

  closeMyRegistrations(): void { this.isMyRegsModalOpen = false; }

  cancelMyRegistration(reg: TournamentRegistrationDto): void {
    if (!reg.id || !confirm('Cancel this registration?')) return;
    this.svc.cancelRegistration(reg.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.myRegistrations = this.myRegistrations.filter(r => r.id !== reg.id);
        this.showToast('Registration cancelled.', 'success');
        this.load();
      },
      error: err => this.showToast(`❌ ${err?.error?.message ?? 'Error'}`, 'error')
    });
  }

  // ── UI Helpers ────────────────────────────────────────────────────────────

  canRegister(t: TournamentDto): boolean {
    return t.status === 'UPCOMING';
  }

  isFull(t: TournamentDto): boolean {
    return (t.registeredCount ?? 0) >= t.maxParticipants;
  }

  getProgress(t: TournamentDto): number {
    return t.maxParticipants
      ? Math.round(((t.registeredCount ?? 0) / t.maxParticipants) * 100)
      : 0;
  }

  sportEmoji(s: SportType): string {
    const map: Record<SportType, string> = {
      FOOTBALL: '⚽', BASKETBALL: '🏀', VOLLEYBALL: '🏐',
      TENNIS: '🎾', PADEL: '🏓', OTHER: '🏅'
    };
    return map[s] ?? '🏅';
  }

  statusLabel(s: TournamentStatus): string {
    const map: Record<TournamentStatus, string> = {
      UPCOMING: 'Open', ONGOING: 'In Progress',
      COMPLETED: 'Completed', CANCELLED: 'Cancelled'
    };
    return map[s] ?? s;
  }

  regStatusLabel(s?: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pending', CONFIRMED: 'Confirmed', CANCELLED: 'Cancelled'
    };
    return s ? (map[s] ?? s) : '—';
  }

  regStatusClass(s?: string): string {
    const map: Record<string, string> = {
      PENDING: 'reg-pending', CONFIRMED: 'reg-confirmed', CANCELLED: 'reg-cancelled'
    };
    return s ? (map[s] ?? '') : '';
  }

  trackById(_: number, t: TournamentDto) { return t.id; }
  trackByRegId(_: number, r: TournamentRegistrationDto) { return r.id; }

  private showToast(msg: string, type: 'success' | 'error'): void {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => this.toastMessage = null, 4000);
  }
}