import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TeamsComponent } from './teams.component';
import { TeamService } from 'src/app/services/team.service';
import { MatchService } from 'src/app/services/match.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('TeamsComponent', () => {
  let component: TeamsComponent;
  let fixture: ComponentFixture<TeamsComponent>;
  let teamServiceSpy: jasmine.SpyObj<TeamService>;
  let matchServiceSpy: jasmine.SpyObj<MatchService>;
  let router: Router;

  const mockTeams = [
    { idTeam: 1, name: 'Thunder FC', sport: 'Soccer', captainFullName: 'John' },
    { idTeam: 2, name: 'Lions FC',   sport: 'Basketball', captainFullName: 'Sara' }
  ];

  const mockMatches = [
    { idMatch: 1, teamAName: 'Thunder FC', teamBName: 'Lions FC',
      status: 'SCHEDULED', matchDate: '2026-05-01T18:00:00',
      location: 'Tunis', scoreTeamA: 0, scoreTeamB: 0, captainName: 'John' }
  ];

  beforeEach(async () => {
    teamServiceSpy  = jasmine.createSpyObj('TeamService',  ['getAllTeams']);
    matchServiceSpy = jasmine.createSpyObj('MatchService', ['getAllMatchs']);

    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));
    matchServiceSpy.getAllMatchs.and.returnValue(of(mockMatches as any));

    await TestBed.configureTestingModule({
      declarations: [TeamsComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: TeamService,  useValue: teamServiceSpy  },
        { provide: MatchService, useValue: matchServiceSpy },
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(TeamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load teams on init', () => {
    expect(teamServiceSpy.getAllTeams).toHaveBeenCalled();
    expect(component.teams.length).toBe(2);
  });

  it('should load matches on init', () => {
    expect(matchServiceSpy.getAllMatchs).toHaveBeenCalled();
    expect(component.matches.length).toBe(1);
  });

  it('activeTab should be my-teams by default', () => {
    expect(component.activeTab).toBe('my-teams');
  });

  // ── loadTeams error ───────────────────────────────────────

  it('loadTeamsTest — should set errorMsg on error', () => {
    teamServiceSpy.getAllTeams.and.returnValue(
      throwError(() => ({ status: 500 }))
    );
    component.loadTeams();
    expect(component.errorMsg).toContain('500');
  });

  // ── loadMatches error ─────────────────────────────────────

  it('loadMatchesTest — should set errorMsg on error', () => {
    matchServiceSpy.getAllMatchs.and.returnValue(
      throwError(() => ({ status: 500 }))
    );
    component.loadMatches();
    expect(component.errorMsg).toContain('500');
    expect(component.isLoadingMatches).toBeFalse();
  });

  // ── switchTab ─────────────────────────────────────────────

  it('switchTabTest — should switch to matches tab', () => {
    component.switchTab('matches');
    expect(component.activeTab).toBe('matches');
  });

  it('switchTabTest — should switch to my-teams tab', () => {
    component.switchTab('my-teams');
    expect(component.activeTab).toBe('my-teams');
  });

  it('switchTabTest — should reload matches when empty', () => {
    component.matches = [];
    component.switchTab('matches');
    expect(matchServiceSpy.getAllMatchs).toHaveBeenCalled();
  });

  // ── goToTeamDetail ────────────────────────────────────────

  it('goToTeamDetailTest — should navigate to detail-team', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goToTeamDetail(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/coach/detail-team', 1]);
  });

  // ── goToDetails ───────────────────────────────────────────

  it('goToDetailsTest — should navigate to detail-match', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goToDetails(mockMatches[0] as any);
    expect(navigateSpy).toHaveBeenCalledWith(['/coach/detail-match', 1]);
  });

  // ── getStatusClass ────────────────────────────────────────

  it('getStatusClassTest — SCHEDULED returns badge-blue', () => {
    expect(component.getStatusClass('SCHEDULED')).toBe('badge-blue');
  });

  it('getStatusClassTest — ONGOING returns badge-orange', () => {
    expect(component.getStatusClass('ONGOING')).toBe('badge-orange');
  });

  it('getStatusClassTest — FINISHED returns badge-green', () => {
    expect(component.getStatusClass('FINISHED')).toBe('badge-green');
  });

  it('getStatusClassTest — CANCELLED returns badge-red', () => {
    expect(component.getStatusClass('CANCELLED')).toBe('badge-red');
  });

  it('getStatusClassTest — unknown returns badge-gray', () => {
    expect(component.getStatusClass('UNKNOWN')).toBe('badge-gray');
  });
});