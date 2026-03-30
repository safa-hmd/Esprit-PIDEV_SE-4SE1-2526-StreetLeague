import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TournamentComponent } from './tournaments.component';
import { TournamentService } from 'src/app/services/tournament.service';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError, Subject, BehaviorSubject } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import {
  TournamentDto,
  TournamentRegistrationDto,
  TournamentStatus
} from './tournament.model';

describe('Backoffice TournamentComponent', () => {
  let component: TournamentComponent;
  let fixture: ComponentFixture<TournamentComponent>;
  let svcSpy: jasmine.SpyObj<TournamentService>;

  const toastSubject$   = new Subject<string>();
  const filtersSubject$ = new BehaviorSubject<any>({ search: '', sport: '', status: '' });

  const mockTournaments: TournamentDto[] = [
    {
      id: 1, name: 'Cup A', sportType: 'FOOTBALL', status: 'UPCOMING',
      tournamentType: 'TEAM', maxParticipants: 8, registeredCount: 3,
      prizePool: 500, location: 'Paris',
      startDate: '2026-06-01', endDate: '2026-06-10', registrationDeadline: '2026-05-25'
    },
    {
      id: 2, name: 'Open B', sportType: 'TENNIS', status: 'COMPLETED',
      tournamentType: 'INDIVIDUAL', maxParticipants: 16, registeredCount: 16,
      prizePool: 200, location: 'Lyon',
      startDate: '2026-04-01', endDate: '2026-04-05', registrationDeadline: '2026-03-25'
    }
  ];

  const mockRegs: TournamentRegistrationDto[] = [
    { id: 101, status: 'PENDING',    tournamentId: 1 },
    { id: 102, status: 'CONFIRMED',  tournamentId: 1 }
  ];

  beforeEach(async () => {
    svcSpy = jasmine.createSpyObj(
      'TournamentService',
      [
        'getAll', 'getRegistrationsByTournament', 'showToast',
        'acceptRegistration', 'rejectRegistration',
        'update', 'create', 'cancel', 'delete',
        'updateFilters', 'applyFilters', 'getFilters','sportBadge' 
      ],
      {
        toast$:   toastSubject$.asObservable(),
        filters$: filtersSubject$.asObservable()
      }
    );

    svcSpy.getAll.and.returnValue(of(mockTournaments));
    svcSpy.applyFilters.and.returnValue(mockTournaments);
    svcSpy.getFilters.and.returnValue({ search: '', sport: '', status: '' } as any);

    await TestBed.configureTestingModule({
      declarations: [TournamentComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TournamentService, useValue: svcSpy },
        FormBuilder
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
    TestBed.overrideComponent(TournamentComponent, {   // ← ajouté
  set: { template: '<div></div>' }
});

    fixture   = TestBed.createComponent(TournamentComponent);
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

  it('should build tournamentForm on init', () => {
    expect(component.tournamentForm).toBeTruthy();
  });

  it('should update toastMessage when toast$ emits', () => {
    toastSubject$.next('Test message');
    expect(component.toastMessage).toBe('Test message');
  });

  it('should apply filters when filters$ emits', () => {
    filtersSubject$.next({ search: 'cup', sport: '', status: '' });
    expect(svcSpy.applyFilters).toHaveBeenCalled();
  });

  // ── load error ────────────────────────────────────────────

  it('loadTest — should set errorMessage and stop loading on failure', () => {
    svcSpy.getAll.and.returnValue(throwError(() => ({ status: 500 })));
    component.load();
    expect(component.errorMessage).toBe('Failed to load tournaments.');
    expect(component.isLoading).toBeFalse();
  });

  // ── onSearch / onSportFilter / onStatusFilter ─────────────

  it('onSearchTest — should call updateFilters with search value', () => {
    const event = { target: { value: 'cup' } } as any;
    component.onSearch(event);
    expect(svcSpy.updateFilters).toHaveBeenCalledWith({ search: 'cup' });
  });

  it('onSportFilterTest — should call updateFilters with sport value', () => {
    const event = { target: { value: 'TENNIS' } } as any;
    component.onSportFilter(event);
    expect(svcSpy.updateFilters).toHaveBeenCalledWith({ sport: 'TENNIS' });
  });

  it('onStatusFilterTest — should call updateFilters with status value', () => {
    const event = { target: { value: 'COMPLETED' } } as any;
    component.onStatusFilter(event);
    expect(svcSpy.updateFilters).toHaveBeenCalledWith({ status: 'COMPLETED' });
  });

  // ── Stats ─────────────────────────────────────────────────

  it('getTotalRegisteredTest — should sum registeredCount across all tournaments', () => {
    expect(component.getTotalRegistered()).toBe(19);
  });

  it('getTotalPrizeTest — should sum prizePool across all tournaments', () => {
    expect(component.getTotalPrize()).toBe(700);
  });

  it('getCountByStatusTest — should count UPCOMING tournaments', () => {
    expect(component.getCountByStatus('UPCOMING')).toBe(1);
  });

  it('getCountByStatusTest — should count COMPLETED tournaments', () => {
    expect(component.getCountByStatus('COMPLETED')).toBe(1);
  });

  it('getCountByStatusTest — should return 0 for a status with no matches', () => {
    expect(component.getCountByStatus('CANCELLED')).toBe(0);
  });

  // ── getStatusClass ────────────────────────────────────────

  it('getStatusClassTest — UPCOMING returns status-upcoming', () => {
    expect(component.getStatusClass('UPCOMING')).toBe('status-upcoming');
  });

  it('getStatusClassTest — ONGOING returns status-ongoing', () => {
    expect(component.getStatusClass('ONGOING')).toBe('status-ongoing');
  });

  it('getStatusClassTest — COMPLETED returns status-completed', () => {
    expect(component.getStatusClass('COMPLETED')).toBe('status-completed');
  });

  it('getStatusClassTest — CANCELLED returns status-cancelled', () => {
    expect(component.getStatusClass('CANCELLED')).toBe('status-cancelled');
  });

  // ── getProgressClass ──────────────────────────────────────

  it('getProgressClassTest — 100% returns progress-full', () => {
    expect(component.getProgressClass(100)).toBe('progress-full');
  });

  it('getProgressClassTest — 80% returns progress-high', () => {
    expect(component.getProgressClass(80)).toBe('progress-high');
  });

  it('getProgressClassTest — 50% returns progress-mid', () => {
    expect(component.getProgressClass(50)).toBe('progress-mid');
  });

  it('getProgressClassTest — 20% returns progress-low', () => {
    expect(component.getProgressClass(20)).toBe('progress-low');
  });

  // ── openRequests ──────────────────────────────────────────

  it('openRequestsTest — should open modal and show only PENDING registrations', () => {
    svcSpy.getRegistrationsByTournament.and.returnValue(of(mockRegs));
    component.openRequests(mockTournaments[0]);
    expect(component.isRequestsModalOpen).toBeTrue();
    expect(component.selectedTournament).toBe(mockTournaments[0]);
    expect(component.pendingRegistrations.length).toBe(1);
    expect(component.pendingRegistrations[0].status).toBe('PENDING');
  });

  it('openRequestsTest — should call showToast on error', () => {
    svcSpy.getRegistrationsByTournament.and.returnValue(throwError(() => ({ status: 500 })));
    component.openRequests(mockTournaments[0]);
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('Failed'));
    expect(component.isLoadingRequests).toBeFalse();
  });

  // ── closeRequests ─────────────────────────────────────────

  it('closeRequestsTest — should close modal and clear state', () => {
    component.isRequestsModalOpen  = true;
    component.selectedTournament   = mockTournaments[0];
    component.pendingRegistrations = [mockRegs[0]];
    component.closeRequests();
    expect(component.isRequestsModalOpen).toBeFalse();
    expect(component.selectedTournament).toBeNull();
    expect(component.pendingRegistrations.length).toBe(0);
  });

  // ── acceptRegistration ────────────────────────────────────

  it('acceptRegistrationTest — should do nothing when reg has no id', () => {
    component.acceptRegistration({ status: 'PENDING' } as any);
    expect(svcSpy.acceptRegistration).not.toHaveBeenCalled();
  });

  it('acceptRegistrationTest — should remove accepted reg from pending list', () => {
    svcSpy.acceptRegistration.and.returnValue(of({} as TournamentRegistrationDto));
    svcSpy.getAll.and.returnValue(of(mockTournaments));
    component.pendingRegistrations = [mockRegs[0]];
    component.acceptRegistration(mockRegs[0]);
    expect(svcSpy.acceptRegistration).toHaveBeenCalledWith(101);
    expect(component.pendingRegistrations.find(r => r.id === 101)).toBeUndefined();
  });

  it('acceptRegistrationTest — should call showToast on error', () => {
    svcSpy.acceptRegistration.and.returnValue(throwError(() => ({ error: { message: 'Forbidden' } })));
    component.pendingRegistrations = [mockRegs[0]];
    component.acceptRegistration(mockRegs[0]);
    expect(svcSpy.showToast).toHaveBeenCalled();
  });

  // ── rejectRegistration ────────────────────────────────────

  it('rejectRegistrationTest — should do nothing when reg has no id', () => {
    component.rejectRegistration({ status: 'PENDING' } as any);
    expect(svcSpy.rejectRegistration).not.toHaveBeenCalled();
  });

  it('rejectRegistrationTest — should remove rejected reg from pending list', () => {
    svcSpy.rejectRegistration.and.returnValue(of({} as TournamentRegistrationDto));
    component.pendingRegistrations = [mockRegs[0]];
    component.rejectRegistration(mockRegs[0]);
    expect(svcSpy.rejectRegistration).toHaveBeenCalledWith(101);
    expect(component.pendingRegistrations.find(r => r.id === 101)).toBeUndefined();
  });

  it('rejectRegistrationTest — should call showToast on error', () => {
    svcSpy.rejectRegistration.and.returnValue(throwError(() => ({ error: { message: 'Error' } })));
    component.pendingRegistrations = [mockRegs[0]];
    component.rejectRegistration(mockRegs[0]);
    expect(svcSpy.showToast).toHaveBeenCalled();
  });

  // ── openCreate ────────────────────────────────────────────

  it('openCreateTest — should reset form and open modal without editing tournament', () => {
    component.openCreate();
    expect(component.isFormModalOpen).toBeTrue();
    expect(component.editingTournament).toBeNull();
    expect(component.tournamentForm.value.sportType).toBe('FOOTBALL');
    expect(component.tournamentForm.value.maxParticipants).toBe(8);
  });

  // ── openEdit ──────────────────────────────────────────────

  it('openEditTest — should patch form with tournament data and open modal', () => {
    component.openEdit(mockTournaments[0]);
    expect(component.isFormModalOpen).toBeTrue();
    expect(component.editingTournament).toBe(mockTournaments[0]);
    expect(component.tournamentForm.value.name).toBe('Cup A');
    expect(component.tournamentForm.value.sportType).toBe('FOOTBALL');
  });

  // ── closeForm ─────────────────────────────────────────────

  it('closeFormTest — should close modal and clear editingTournament', () => {
    component.isFormModalOpen   = true;
    component.editingTournament = mockTournaments[0];
    component.closeForm();
    expect(component.isFormModalOpen).toBeFalse();
    expect(component.editingTournament).toBeNull();
  });

  // ── submitForm ────────────────────────────────────────────

  it('submitFormTest — should do nothing when form is invalid', () => {
    component.tournamentForm.reset();
    component.submitForm();
    expect(svcSpy.create).not.toHaveBeenCalled();
    expect(svcSpy.update).not.toHaveBeenCalled();
  });

  it('submitFormTest — should call create when editingTournament is null', () => {
    svcSpy.create.and.returnValue(of(mockTournaments[0]));
    component.editingTournament = null;
    component.tournamentForm.setValue({
      name: 'New Cup', description: '', sportType: 'FOOTBALL', tournamentType: 'TEAM',
      startDate: '2026-07-01', endDate: '2026-07-10', registrationDeadline: '2026-06-25',
      maxParticipants: 8, location: 'Paris', prizePool: null
    });
    component.submitForm();
    expect(svcSpy.create).toHaveBeenCalled();
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('created'));
    expect(component.isSaving).toBeFalse();
  });

  it('submitFormTest — should call update when editingTournament is set', () => {
    svcSpy.update.and.returnValue(of(mockTournaments[0]));
    component.editingTournament = mockTournaments[0];
    component.tournamentForm.setValue({
      name: 'Cup A Updated', description: '', sportType: 'FOOTBALL', tournamentType: 'TEAM',
      startDate: '2026-07-01', endDate: '2026-07-10', registrationDeadline: '2026-06-25',
      maxParticipants: 8, location: 'Paris', prizePool: null
    });
    component.submitForm();
    expect(svcSpy.update).toHaveBeenCalledWith(1, jasmine.any(Object));
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('updated'));
  });

  it('submitFormTest — should show field validation errors on 400 with object error', () => {
    svcSpy.create.and.returnValue(throwError(() => ({
      status: 400, error: { name: 'too short', location: 'required' }
    })));
    component.editingTournament = null;
    component.tournamentForm.setValue({
      name: 'New Cup', description: '', sportType: 'FOOTBALL', tournamentType: 'TEAM',
      startDate: '2026-07-01', endDate: '2026-07-10', registrationDeadline: '2026-06-25',
      maxParticipants: 8, location: 'Paris', prizePool: null
    });
    component.submitForm();
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('name'));
    expect(component.isSaving).toBeFalse();
  });

  it('submitFormTest — should show message error on other errors', () => {
    svcSpy.create.and.returnValue(throwError(() => ({
      status: 409, error: { message: 'Tournament name already exists' }
    })));
    component.editingTournament = null;
    component.tournamentForm.setValue({
      name: 'New Cup', description: '', sportType: 'FOOTBALL', tournamentType: 'TEAM',
      startDate: '2026-07-01', endDate: '2026-07-10', registrationDeadline: '2026-06-25',
      maxParticipants: 8, location: 'Paris', prizePool: null
    });
    component.submitForm();
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('already exists'));
  });

  // ── cancelTournament ──────────────────────────────────────

  it('cancelTournamentTest — should do nothing when tournament has no id', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    component.cancelTournament({ ...mockTournaments[0], id: undefined });
    expect(svcSpy.cancel).not.toHaveBeenCalled();
  });

  it('cancelTournamentTest — should not cancel when confirm declined', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.cancelTournament(mockTournaments[0]);
    expect(svcSpy.cancel).not.toHaveBeenCalled();
  });

  it('cancelTournamentTest — should cancel and reload on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.cancel.and.returnValue(of({} as TournamentDto));
    const loadSpy = spyOn(component, 'load');
    component.cancelTournament(mockTournaments[0]);
    expect(svcSpy.cancel).toHaveBeenCalledWith(1);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('cancelTournamentTest — should call showToast on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.cancel.and.returnValue(throwError(() => ({ error: { message: 'Cannot cancel' } })));
    component.cancelTournament(mockTournaments[0]);
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('Cannot cancel'));
  });

  // ── deleteTournament ──────────────────────────────────────

  it('deleteTournamentTest — should do nothing when tournament has no id', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    component.deleteTournament({ ...mockTournaments[0], id: undefined });
    expect(svcSpy.delete).not.toHaveBeenCalled();
  });

  it('deleteTournamentTest — should not delete when confirm declined', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteTournament(mockTournaments[0]);
    expect(svcSpy.delete).not.toHaveBeenCalled();
  });

  it('deleteTournamentTest — should delete and reload on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.delete.and.returnValue(of(void 0));
    const loadSpy = spyOn(component, 'load');
    component.deleteTournament(mockTournaments[0]);
    expect(svcSpy.delete).toHaveBeenCalledWith(1);
    expect(loadSpy).toHaveBeenCalled();
  });

  it('deleteTournamentTest — should call showToast on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    svcSpy.delete.and.returnValue(throwError(() => ({ status: 500 })));
    component.deleteTournament(mockTournaments[0]);
    expect(svcSpy.showToast).toHaveBeenCalledWith(jasmine.stringContaining('Error'));
  });

  // ── ngOnDestroy ───────────────────────────────────────────

  it('should complete destroy$ on ngOnDestroy', () => {
    const spy = spyOn((component as any).destroy$, 'next');
    component.ngOnDestroy();
    expect(spy).toHaveBeenCalled();
  });
});