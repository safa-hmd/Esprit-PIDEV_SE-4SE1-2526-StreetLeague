import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TournamentsComponent } from './tournaments.component';
import { TournamentService } from '../../services/tournament.service';
import { AuthService }       from '../../services/auth.service';
import { TeamService }       from '../../services/team.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import {
  TournamentDto,
  TournamentRegistrationDto,
  SportType,
  TournamentStatus
} from '../../backoffice/tournaments/tournament.model';

describe('Frontoffice TournamentsComponent', () => {
  let component: TournamentsComponent;
  let fixture: ComponentFixture<TournamentsComponent>;
  let svcSpy:     jasmine.SpyObj<TournamentService>;
  let authSpy:    jasmine.SpyObj<AuthService>;
  let teamSvcSpy: jasmine.SpyObj<TeamService>;

  const mockTournaments: TournamentDto[] = [
    {
      id: 1, name: 'Cup Alpha', sportType: 'FOOTBALL', status: 'UPCOMING',
      tournamentType: 'TEAM', maxParticipants: 8, registeredCount: 3,
      location: 'Tunis', startDate: '2026-06-01', endDate: '2026-06-10',
      registrationDeadline: '2026-05-25', prizePool: 500
    },
    {
      id: 2, name: 'Open Beta', sportType: 'TENNIS', status: 'COMPLETED',
      tournamentType: 'INDIVIDUAL', maxParticipants: 16, registeredCount: 16,
      location: 'Sfax', startDate: '2026-04-01', endDate: '2026-04-05',
      registrationDeadline: '2026-03-25', prizePool: 0
    }
  ];

  const mockTeams = [
    { idTeam: 1, name: 'Thunder FC', sport: 'Soccer',      captainFullName: 'John', playerCount: 5 },
    { idTeam: 2, name: 'Lions FC',   sport: 'Basketball',  captainFullName: 'Sara', playerCount: 3 }
  ];

  const mockRegistrations: TournamentRegistrationDto[] = [
    { id: 201, status: 'PENDING',   tournamentId: 1, registeredAt: '2026-05-01T10:00:00' },
    { id: 202, status: 'CONFIRMED', tournamentId: 2, registeredAt: '2026-04-01T09:00:00' }
  ];

  beforeEach(async () => {
    svcSpy     = jasmine.createSpyObj('TournamentService', [
      'getAll', 'registerTeam', 'registerPlayer',
      'getRegistrationsByPlayer', 'getTeamRegistrationsByPlayer', 'cancelRegistration'
    ]);
    authSpy    = jasmine.createSpyObj('AuthService', ['getUserId', 'getEmail']);
    teamSvcSpy = jasmine.createSpyObj('TeamService', ['getAllTeams']);

    svcSpy.getAll.and.returnValue(of(mockTournaments));
    authSpy.getUserId.and.returnValue('42');
    authSpy.getEmail.and.returnValue('player@test.com');
    teamSvcSpy.getAllTeams.and.returnValue(of(mockTeams as any));

    await TestBed.configureTestingModule({
      declarations: [TournamentsComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TournamentService, useValue: svcSpy     },
        { provide: AuthService,       useValue: authSpy    },
        { provide: TeamService,       useValue: teamSvcSpy },
        FormBuilder
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture   = TestBed.createComponent(TournamentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load tournaments on init', () => {
    expect(svcSpy.getAll).toHaveBeenCalled();
    expect(component.allTournaments.length).toBe(2);
  });

  it('should set currentUserId from auth service on init', () => {
    expect(component.currentUserId).toBe(42);
  });

  it('should set currentUserEmail from auth service on init', () => {
    expect(component.currentUserEmail).toBe('player@test.com');
  });

  it('filteredTournaments should equal allTournaments after init', () => {
    expect(component.filteredTournaments.length).toBe(2);
  });

  // ── load error ────────────────────────────────────────────

  it('loadTest — should set errorMessage and stop loading on failure', () => {
    svcSpy.getAll.and.returnValue(throwError(() => ({ status: 500 })));
    component.load();
    expect(component.errorMessage).toBe('Failed to load tournaments. Please try again.');
    expect(component.isLoading).toBeFalse();
  });

  // ── applyFilters ──────────────────────────────────────────

  it('applyFiltersTest — should filter by tournament name', () => {
    component.searchTerm = 'alpha';
    (component as any).applyFilters();
    expect(component.filteredTournaments.length).toBe(1);
    expect(component.filteredTournaments[0].name).toBe('Cup Alpha');
  });

  it('applyFiltersTest — should filter by location', () => {
    component.searchTerm = 'sfax';
    (component as any).applyFilters();
    expect(component.filteredTournaments.length).toBe(1);
    expect(component.filteredTournaments[0].location).toBe('Sfax');
  });

  it('applyFiltersTest — should filter by sport type', () => {
    component.sportFilter = 'TENNIS';
    (component as any).applyFilters();
    expect(component.filteredTournaments.length).toBe(1);
    expect(component.filteredTournaments[0].sportType).toBe('TENNIS');
  });

  it('applyFiltersTest — should filter by status', () => {
    component.statusFilter = 'UPCOMING';
    (component as any).applyFilters();
    expect(component.filteredTournaments.length).toBe(1);
    expect(component.filteredTournaments[0].status).toBe('UPCOMING');
  });

  it('applyFiltersTest — should return all when no filter applied', () => {
    (component as any).applyFilters();
    expect(component.filteredTournaments.length).toBe(2);
  });

  it('applyFiltersTest — should return empty when no match', () => {
    component.searchTerm = 'zzzzz';
    (component as any).applyFilters();
    expect(component.filteredTournaments.length).toBe(0);
  });

  // ── onSearch / onSportFilter / onStatusFilter ─────────────

  it('onSearchTest — should update searchTerm and apply filters', () => {
    const event = { target: { value: 'alpha' } } as any;
    component.onSearch(event);
    expect(component.searchTerm).toBe('alpha');
    expect(component.filteredTournaments.length).toBe(1);
  });

  it('onSportFilterTest — should update sportFilter and apply filters', () => {
    const event = { target: { value: 'TENNIS' } } as any;
    component.onSportFilter(event);
    expect(component.sportFilter).toBe('TENNIS');
    expect(component.filteredTournaments.length).toBe(1);
  });

  it('onStatusFilterTest — should update statusFilter and apply filters', () => {
    const event = { target: { value: 'COMPLETED' } } as any;
    component.onStatusFilter(event);
    expect(component.statusFilter).toBe('COMPLETED');
    expect(component.filteredTournaments.length).toBe(1);
  });

  // ── openRegister ──────────────────────────────────────────

  it('openRegisterTest — should show toast when user is not logged in', () => {
    component.currentUserId = null;
    const toastSpy = spyOn<any>(component, 'showToast');
    component.openRegister(mockTournaments[0]);
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('log in'), 'error');
    expect(component.isRegisterModalOpen).toBeFalse();
  });

  it('openRegisterTest — should load teams for TEAM tournament', () => {
    component.openRegister(mockTournaments[0]); // TEAM
    expect(teamSvcSpy.getAllTeams).toHaveBeenCalled();
    expect(component.availableTeams.length).toBe(2);
    expect(component.isRegisterModalOpen).toBeTrue();
  });

  it('openRegisterTest — should not load teams for INDIVIDUAL tournament', () => {
    teamSvcSpy.getAllTeams.calls.reset();
    component.openRegister(mockTournaments[1]); // INDIVIDUAL
    expect(teamSvcSpy.getAllTeams).not.toHaveBeenCalled();
    expect(component.isRegisterModalOpen).toBeTrue();
  });

  it('openRegisterTest — should pre-select team when only one team is available', () => {
    teamSvcSpy.getAllTeams.and.returnValue(of([mockTeams[0]] as any));
    component.openRegister(mockTournaments[0]);
    expect(component.registerForm.value.teamId).toBe(mockTeams[0].idTeam);
  });

  it('openRegisterTest — should show toast when team loading fails', () => {
    teamSvcSpy.getAllTeams.and.returnValue(throwError(() => ({ status: 500 })));
    const toastSpy = spyOn<any>(component, 'showToast');
    component.openRegister(mockTournaments[0]);
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('Failed to load teams'), 'error');
  });

  // ── closeRegister ─────────────────────────────────────────

  it('closeRegisterTest — should close modal and clear state', () => {
    component.isRegisterModalOpen = true;
    component.selectedTournament  = mockTournaments[0];
    component.availableTeams      = mockTeams as any;
    component.closeRegister();
    expect(component.isRegisterModalOpen).toBeFalse();
    expect(component.selectedTournament).toBeNull();
    expect(component.availableTeams.length).toBe(0);
  });

  // ── isTeamTournament ──────────────────────────────────────

  it('isTeamTournamentTest — should return true for TEAM tournament', () => {
    component.selectedTournament = mockTournaments[0];
    expect(component.isTeamTournament()).toBeTrue();
  });

  it('isTeamTournamentTest — should return false for INDIVIDUAL tournament', () => {
    component.selectedTournament = mockTournaments[1];
    expect(component.isTeamTournament()).toBeFalse();
  });

  it('isTeamTournamentTest — should return false when no tournament selected', () => {
    component.selectedTournament = null;
    expect(component.isTeamTournament()).toBeFalse();
  });

  // ── submitRegistration ────────────────────────────────────

  it('submitRegistrationTest — should do nothing when no tournament selected', () => {
    component.selectedTournament = null;
    component.submitRegistration();
    expect(svcSpy.registerTeam).not.toHaveBeenCalled();
    expect(svcSpy.registerPlayer).not.toHaveBeenCalled();
  });

  it('submitRegistrationTest — should do nothing when form is invalid', () => {
    component.selectedTournament = mockTournaments[0]; // TEAM
    component.currentUserId = 42;
    component.registerForm.get('teamId')!.setValidators([Validators.required]);
    component.registerForm.get('teamId')!.setValue(null);
    component.registerForm.get('teamId')!.updateValueAndValidity();
    component.submitRegistration();
    expect(svcSpy.registerTeam).not.toHaveBeenCalled();
  });

  it('submitRegistrationTest — should registerTeam for TEAM tournament', () => {
    svcSpy.registerTeam.and.returnValue(of({} as any));
    svcSpy.getAll.and.returnValue(of(mockTournaments));
    component.selectedTournament = mockTournaments[0];
    component.currentUserId = 42;
    component.registerForm.get('teamId')!.setValue(1);
    component.registerForm.get('teamId')!.setErrors(null);
    component.submitRegistration();
    expect(svcSpy.registerTeam).toHaveBeenCalledWith(1, 1);
    expect(component.isRegisterModalOpen).toBeFalse();
  });

  it('submitRegistrationTest — should registerPlayer for INDIVIDUAL tournament', () => {
    svcSpy.registerPlayer.and.returnValue(of({} as any));
    svcSpy.getAll.and.returnValue(of(mockTournaments));
    component.selectedTournament = mockTournaments[1];
    component.currentUserId = 42;
    component.registerForm.get('teamId')!.setErrors(null);
    component.submitRegistration();
    expect(svcSpy.registerPlayer).toHaveBeenCalledWith(2, 42);
  });

  it('submitRegistrationTest — should show toast on error', () => {
    svcSpy.registerPlayer.and.returnValue(throwError(() => ({
      error: { message: 'Already registered' }
    })));
    const toastSpy = spyOn<any>(component, 'showToast');
    component.selectedTournament = mockTournaments[1];
    component.currentUserId = 42;
    component.registerForm.get('teamId')!.setErrors(null);
    component.submitRegistration();
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('Already registered'), 'error');
    expect(component.isSubmitting).toBeFalse();
  });

  // ── openMyRegistrations ───────────────────────────────────

  it('openMyRegistrationsTest — should show toast when user not logged in', () => {
    component.currentUserId = null;
    const toastSpy = spyOn<any>(component, 'showToast');
    component.openMyRegistrations();
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('log in'), 'error');
    expect(component.isMyRegsModalOpen).toBeFalse();
  });

  it('openMyRegistrationsTest — should load and merge individual + team registrations', () => {
    svcSpy.getRegistrationsByPlayer.and.returnValue(of([mockRegistrations[0]]));
    svcSpy.getTeamRegistrationsByPlayer.and.returnValue(of([mockRegistrations[1]]));
    component.openMyRegistrations();
    expect(component.myRegistrations.length).toBe(2);
    expect(component.isMyRegsModalOpen).toBeTrue();
    expect(component.isLoadingMyRegs).toBeFalse();
  });

  it('openMyRegistrationsTest — should sort registrations by registeredAt descending', () => {
    svcSpy.getRegistrationsByPlayer.and.returnValue(of([mockRegistrations[0]]));
    svcSpy.getTeamRegistrationsByPlayer.and.returnValue(of([mockRegistrations[1]]));
    component.openMyRegistrations();
    const dates = component.myRegistrations.map(r => new Date(r.registeredAt ?? '').getTime());
    expect(dates[0]).toBeGreaterThanOrEqual(dates[1]);
  });

  it('openMyRegistrationsTest — should show toast and close modal on error', () => {
    svcSpy.getRegistrationsByPlayer.and.returnValue(throwError(() => ({ status: 500 })));
    svcSpy.getTeamRegistrationsByPlayer.and.returnValue(of([]));
    const toastSpy = spyOn<any>(component, 'showToast');
    component.openMyRegistrations();
    expect(toastSpy).toHaveBeenCalledWith(jasmine.stringContaining('Failed'), 'error');
    expect(component.isMyRegsModalOpen).toBeFalse();
  });

  // ── closeMyRegistrations ──────────────────────────────────

  it('closeMyRegistrationsTest — should close my registrations modal', () => {
    component.isMyRegsModalOpen = true;
    component.closeMyRegistrations();
    expect(component.isMyRegsModalOpen).toBeFalse();
  });

  // ── cancelMyRegistration ──────────────────────────────────

  it('cancelMyRegistrationTest — should do nothing when reg has no id', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    component.cancelMyRegistration({ status: 'PENDING' } as any);
    expect(svcSpy.cancelRegistration).not.toHaveBeenCalled();
  });

  it('cancelMyRegistrationTest — should not cancel when confirm declined', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.cancelMyRegistration(mockRegistrations[0]);
    expect(svcSpy.cancelRegistration).not.toHaveBeenCalled();
  });

  it('cancelMyRegistrationTest — should cancel and remove reg from list on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.cancelRegistration.and.returnValue(of({} as TournamentRegistrationDto));
    svcSpy.getAll.and.returnValue(of(mockTournaments));
    component.myRegistrations = [...mockRegistrations];
    component.cancelMyRegistration(mockRegistrations[0]);
    expect(svcSpy.cancelRegistration).toHaveBeenCalledWith(201);
    expect(component.myRegistrations.find(r => r.id === 201)).toBeUndefined();
  });

  it('cancelMyRegistrationTest — should show toast on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.cancelRegistration.and.returnValue(throwError(() => ({ error: { message: 'Error' } })));
    const toastSpy = spyOn<any>(component, 'showToast');
    component.myRegistrations = [...mockRegistrations];
    component.cancelMyRegistration(mockRegistrations[0]);
    expect(toastSpy).toHaveBeenCalled();
  });

  // ── canRegister ───────────────────────────────────────────

  it('canRegisterTest — should return true for UPCOMING tournament', () => {
    expect(component.canRegister(mockTournaments[0])).toBeTrue();
  });

  it('canRegisterTest — should return false for COMPLETED tournament', () => {
    expect(component.canRegister(mockTournaments[1])).toBeFalse();
  });

  // ── isFull ────────────────────────────────────────────────

  it('isFullTest — should return false when spots are available', () => {
    expect(component.isFull(mockTournaments[0])).toBeFalse(); // 3/8
  });

  it('isFullTest — should return true when at max capacity', () => {
    expect(component.isFull(mockTournaments[1])).toBeTrue(); // 16/16
  });

  // ── getProgress ───────────────────────────────────────────

  it('getProgressTest — should return correct fill percentage', () => {
    expect(component.getProgress(mockTournaments[0])).toBe(38); // round(3/8 * 100)
  });

  it('getProgressTest — should return 100 when tournament is full', () => {
    expect(component.getProgress(mockTournaments[1])).toBe(100);
  });

  it('getProgressTest — should return 0 when maxParticipants is 0', () => {
    expect(component.getProgress({ ...mockTournaments[0], maxParticipants: 0 })).toBe(0);
  });

  // ── sportEmoji ────────────────────────────────────────────

  it('sportEmojiTest — FOOTBALL returns ⚽', () => {
    expect(component.sportEmoji('FOOTBALL')).toBe('⚽');
  });

  it('sportEmojiTest — BASKETBALL returns 🏀', () => {
    expect(component.sportEmoji('BASKETBALL')).toBe('🏀');
  });

  it('sportEmojiTest — VOLLEYBALL returns 🏐', () => {
    expect(component.sportEmoji('VOLLEYBALL')).toBe('🏐');
  });

  it('sportEmojiTest — unknown returns 🏅', () => {
    expect(component.sportEmoji('OTHER')).toBe('🏅');
  });

  // ── statusLabel ───────────────────────────────────────────

  it('statusLabelTest — UPCOMING returns Open', () => {
    expect(component.statusLabel('UPCOMING')).toBe('Open');
  });

  it('statusLabelTest — ONGOING returns In Progress', () => {
    expect(component.statusLabel('ONGOING')).toBe('In Progress');
  });

  it('statusLabelTest — COMPLETED returns Completed', () => {
    expect(component.statusLabel('COMPLETED')).toBe('Completed');
  });

  it('statusLabelTest — CANCELLED returns Cancelled', () => {
    expect(component.statusLabel('CANCELLED')).toBe('Cancelled');
  });

  // ── regStatusLabel ────────────────────────────────────────

  it('regStatusLabelTest — PENDING returns Pending', () => {
    expect(component.regStatusLabel('PENDING')).toBe('Pending');
  });

  it('regStatusLabelTest — CONFIRMED returns Confirmed', () => {
    expect(component.regStatusLabel('CONFIRMED')).toBe('Confirmed');
  });

  it('regStatusLabelTest — undefined returns —', () => {
    expect(component.regStatusLabel(undefined)).toBe('—');
  });

  // ── regStatusClass ────────────────────────────────────────

  it('regStatusClassTest — PENDING returns reg-pending', () => {
    expect(component.regStatusClass('PENDING')).toBe('reg-pending');
  });

  it('regStatusClassTest — CONFIRMED returns reg-confirmed', () => {
    expect(component.regStatusClass('CONFIRMED')).toBe('reg-confirmed');
  });

  it('regStatusClassTest — CANCELLED returns reg-cancelled', () => {
    expect(component.regStatusClass('CANCELLED')).toBe('reg-cancelled');
  });

  it('regStatusClassTest — undefined returns empty string', () => {
    expect(component.regStatusClass(undefined)).toBe('');
  });

  // ── ngOnDestroy ───────────────────────────────────────────

  it('should complete destroy$ on ngOnDestroy', () => {
    const spy = spyOn((component as any).destroy$, 'next');
    component.ngOnDestroy();
    expect(spy).toHaveBeenCalled();
  });
});