import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';

import { HomeComponent }   from './home.component';
import { TeamService }     from 'src/app/services/team.service';
import { MatchService }    from 'src/app/services/match.service';
import { TrainingService } from 'src/app/services/training.service';

// ── Minimal stubs ─────────────────────────────────────────────────────────────
const makeTeams = (overrides: any[] = []) =>
  overrides.map((o, i) => ({ id: i + 1, nom: `Team${i}`, sport: 'Football', playerCount: 10, ...o }));

const makeMatches = (statuses: string[]) =>
  statuses.map((status, i) => ({ id: i + 1, status, homeTeam: 'A', awayTeam: 'B' }));

const makeTrainings = (statuses: string[]) =>
  statuses.map((status, i) => ({ id: i + 1, status, titre: `Training${i}` }));

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let teamServiceSpy:     jasmine.SpyObj<TeamService>;
  let matchServiceSpy:    jasmine.SpyObj<MatchService>;
  let trainingServiceSpy: jasmine.SpyObj<TrainingService>;
  let routerSpy:          jasmine.SpyObj<Router>;

  beforeEach(() => {
    teamServiceSpy     = jasmine.createSpyObj('TeamService',     ['getAllTeams']);
    matchServiceSpy    = jasmine.createSpyObj('MatchService',    ['getAllMatches']);
    trainingServiceSpy = jasmine.createSpyObj('TrainingService', ['getAllTrainings']);
    routerSpy          = jasmine.createSpyObj('Router',          ['navigate']);

    // Default happy-path responses
    teamServiceSpy.getAllTeams.and.returnValue(of(makeTeams([
      { nom: 'Alpha', playerCount: 20 },
      { nom: 'Beta',  playerCount: 5  },
      { nom: 'Gamma', playerCount: 15 },
    ]) as any));
    matchServiceSpy.getAllMatches.and.returnValue(of(makeMatches([
      'SCHEDULED', 'ONGOING', 'FINISHED', 'CANCELLED', 'SCHEDULED'
    ]) as any));
    trainingServiceSpy.getAllTrainings.and.returnValue(of(makeTrainings([
      'PLANNED', 'COMPLETED', 'CANCELLED', 'PLANNED'
    ]) as any));

    TestBed.configureTestingModule({
      declarations: [HomeComponent],
      providers: [
        { provide: TeamService,     useValue: teamServiceSpy     },
        { provide: MatchService,    useValue: matchServiceSpy    },
        { provide: TrainingService, useValue: trainingServiceSpy },
        { provide: Router,          useValue: routerSpy          },
      ]
    });

    fixture   = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    localStorage.clear();
  });

  afterEach(() => localStorage.clear());

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  // ── ngOnInit — adminName ──────────────────────────────────

  it('ngOnInitTest — should read adminName from localStorage', () => {
    localStorage.setItem('EmailUserConnect', 'admin@test.com');
    fixture.detectChanges();
    expect(component.adminName).toBe('admin@test.com');
  });

  it('ngOnInitTest — should default adminName to Admin when localStorage is empty', () => {
    fixture.detectChanges();
    expect(component.adminName).toBe('Admin');
  });

  // ── loadAllData — teams ───────────────────────────────────

  it('loadAllDataTest — should set totalTeams from getAllTeams', () => {
    fixture.detectChanges();
    expect(component.totalTeams).toBe(3);
  });

  it('loadAllDataTest — should sum playerCount for totalPlayers', () => {
    fixture.detectChanges();
    expect(component.totalPlayers).toBe(40); // 20 + 5 + 15
  });

  it('loadAllDataTest — should set topTeams to first 6 teams', () => {
    teamServiceSpy.getAllTeams.and.returnValue(of(makeTeams(
      Array.from({ length: 8 }, (_, i) => ({ nom: `T${i}` }))
    ) as any));
    fixture.detectChanges();
    expect(component.topTeams.length).toBe(6);
  });

  it('loadAllDataTest — should set topTeams to all teams when fewer than 6', () => {
    fixture.detectChanges();
    expect(component.topTeams.length).toBe(3);
  });

  it('loadAllDataTest — should set isLoadingTeams to false after success', () => {
    fixture.detectChanges();
    expect(component.isLoadingTeams).toBeFalse();
  });

  it('loadAllDataTest — should set isLoadingTeams to false on teams error', () => {
    teamServiceSpy.getAllTeams.and.returnValue(throwError(() => ({ statut: 500 })));
    fixture.detectChanges();
    expect(component.isLoadingTeams).toBeFalse();
  });

  // ── loadAllData — matches ─────────────────────────────────

  it('loadAllDataTest — should set totalMatches from getAllMatches', () => {
    fixture.detectChanges();
    expect(component.totalMatches).toBe(5);
  });

  it('loadAllDataTest — should set recentMatches to first 5 matches', () => {
    matchServiceSpy.getAllMatches.and.returnValue(of(makeMatches(
      Array.from({ length: 10 }, () => 'SCHEDULED')
    ) as any));
    fixture.detectChanges();
    expect(component.recentMatches.length).toBe(5);
  });

  it('loadAllDataTest — should set recentMatches to all when fewer than 5', () => {
    matchServiceSpy.getAllMatches.and.returnValue(
      of(makeMatches(['SCHEDULED', 'ONGOING']) as any)
    );
    fixture.detectChanges();
    expect(component.recentMatches.length).toBe(2);
  });

  it('loadAllDataTest — should set isLoadingMatches to false after success', () => {
    fixture.detectChanges();
    expect(component.isLoadingMatches).toBeFalse();
  });

  it('loadAllDataTest — should set isLoadingMatches to false on matches error', () => {
    matchServiceSpy.getAllMatches.and.returnValue(throwError(() => ({ statut: 500 })));
    fixture.detectChanges();
    expect(component.isLoadingMatches).toBeFalse();
  });

  // ── loadAllData — trainings ───────────────────────────────

  it('loadAllDataTest — should set totalTrainings from getAllTrainings', () => {
    fixture.detectChanges();
    expect(component.totalTrainings).toBe(4);
  });

  it('loadAllDataTest — should set recentTrainings to first 4 trainings', () => {
    trainingServiceSpy.getAllTrainings.and.returnValue(of(makeTrainings(
      Array.from({ length: 10 }, () => 'PLANNED')
    ) as any));
    fixture.detectChanges();
    expect(component.recentTrainings.length).toBe(4);
  });

  it('loadAllDataTest — should set recentTrainings to all when fewer than 4', () => {
    trainingServiceSpy.getAllTrainings.and.returnValue(
      of(makeTrainings(['PLANNED']) as any)
    );
    fixture.detectChanges();
    expect(component.recentTrainings.length).toBe(1);
  });

  it('loadAllDataTest — should set isLoadingTrainings to false after success', () => {
    fixture.detectChanges();
    expect(component.isLoadingTrainings).toBeFalse();
  });

  it('loadAllDataTest — should set isLoadingTrainings to false on trainings error', () => {
    trainingServiceSpy.getAllTrainings.and.returnValue(throwError(() => ({ statut: 500 })));
    fixture.detectChanges();
    expect(component.isLoadingTrainings).toBeFalse();
  });

  // ── checkReady / dataReady ────────────────────────────────

  it('checkReadyTest — should set dataReady to true when all services respond', fakeAsync(() => {
    fixture.detectChanges();
    tick(100);
    expect(component.dataReady).toBeTrue();
  }));

  it('checkReadyTest — should set dataReady to true even when one service errors', fakeAsync(() => {
    matchServiceSpy.getAllMatches.and.returnValue(throwError(() => ({ statut: 500 })));
    fixture.detectChanges();
    tick(100);
    expect(component.dataReady).toBeTrue();
  }));

  it('checkReadyTest — should set dataReady to true even when all services error', fakeAsync(() => {
    teamServiceSpy.getAllTeams.and.returnValue(throwError(() => ({ statut: 500 })));
    matchServiceSpy.getAllMatches.and.returnValue(throwError(() => ({ statut: 500 })));
    trainingServiceSpy.getAllTrainings.and.returnValue(throwError(() => ({ statut: 500 })));
    fixture.detectChanges();
    tick(100);
    expect(component.dataReady).toBeTrue();
  }));

  it('checkReadyTest — should NOT set dataReady until all 3 services have responded', () => {
    // Block matches from emitting — dataReady must stay false
    matchServiceSpy.getAllMatches.and.returnValue(new Observable(() => {}));
    fixture.detectChanges();
    expect(component.dataReady).toBeFalse();
  });

  // ── goTo ──────────────────────────────────────────────────

  it('goToTest — should navigate to the given path', () => {
    fixture.detectChanges();
    component.goTo('/admin/teams');
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/admin/teams']);
  });

  it('goToTest — should navigate to /admin/matches', () => {
    fixture.detectChanges();
    component.goTo('/admin/matches');
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/admin/matches']);
  });

  it('goToTest — should navigate to /admin/trainings', () => {
    fixture.detectChanges();
    component.goTo('/admin/trainings');
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/admin/trainings']);
  });

  // ── getMatchStatusClass ───────────────────────────────────

  it('getMatchStatusClassTest — SCHEDULED returns badge-scheduled', () => {
    fixture.detectChanges();
    expect(component.getMatchStatusClass('SCHEDULED')).toBe('badge-scheduled');
  });

  it('getMatchStatusClassTest — ONGOING returns badge-ongoing', () => {
    fixture.detectChanges();
    expect(component.getMatchStatusClass('ONGOING')).toBe('badge-ongoing');
  });

  it('getMatchStatusClassTest — FINISHED returns badge-finished', () => {
    fixture.detectChanges();
    expect(component.getMatchStatusClass('FINISHED')).toBe('badge-finished');
  });

  it('getMatchStatusClassTest — CANCELLED returns badge-cancelled', () => {
    fixture.detectChanges();
    expect(component.getMatchStatusClass('CANCELLED')).toBe('badge-cancelled');
  });

  it('getMatchStatusClassTest — unknown status returns badge-default', () => {
    fixture.detectChanges();
    expect(component.getMatchStatusClass('UNKNOWN')).toBe('badge-default');
  });

  // ── getTrainingStatusClass ────────────────────────────────

  it('getTrainingStatusClassTest — PLANNED returns badge-scheduled', () => {
    fixture.detectChanges();
    expect(component.getTrainingStatusClass('PLANNED')).toBe('badge-scheduled');
  });

  it('getTrainingStatusClassTest — COMPLETED returns badge-finished', () => {
    fixture.detectChanges();
    expect(component.getTrainingStatusClass('COMPLETED')).toBe('badge-finished');
  });

  it('getTrainingStatusClassTest — CANCELLED returns badge-cancelled', () => {
    fixture.detectChanges();
    expect(component.getTrainingStatusClass('CANCELLED')).toBe('badge-cancelled');
  });

  it('getTrainingStatusClassTest — unknown status returns badge-default', () => {
    fixture.detectChanges();
    expect(component.getTrainingStatusClass('UNKNOWN')).toBe('badge-default');
  });

  // ── getProgressColor ──────────────────────────────────────

  it('getProgressColorTest — count 0 (0%) returns blue (#4e8a9f)', () => {
    fixture.detectChanges();
    expect(component.getProgressColor(0)).toBe('#4e8a9f');
  });

  it('getProgressColorTest — count 10 (40%) returns blue (#4e8a9f)', () => {
    fixture.detectChanges();
    expect(component.getProgressColor(10)).toBe('#4e8a9f');
  });

  it('getProgressColorTest — count 19 (76%) returns orange (#e87040)', () => {
    fixture.detectChanges();
    expect(component.getProgressColor(19)).toBe('#e87040');
  });

  it('getProgressColorTest — count 25 (100%) returns red (#f87171)', () => {
    fixture.detectChanges();
    expect(component.getProgressColor(25)).toBe('#f87171');
  });

  it('getProgressColorTest — count 26 (>100%) returns red (#f87171)', () => {
    fixture.detectChanges();
    expect(component.getProgressColor(26)).toBe('#f87171');
  });
});

