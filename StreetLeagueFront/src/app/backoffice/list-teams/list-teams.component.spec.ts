import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListTeamsComponent } from './list-teams.component';
import { TeamService } from 'src/app/services/team.service';
import { MatchService } from 'src/app/services/match.service';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('ListTeamsComponent', () => {
  let component: ListTeamsComponent;
  let fixture: ComponentFixture<ListTeamsComponent>;
  let teamServiceSpy: jasmine.SpyObj<TeamService>;
  let matchServiceSpy: jasmine.SpyObj<MatchService>;

  const mockTeams = [
    { idTeam: 1, nom: 'Thunder FC', sport: 'Soccer', captainFullName: 'John', playerCount: 5 },
    { idTeam: 2, nom: 'Lions FC',   sport: 'Basketball', captainFullName: 'Sara', playerCount: 3 }
  ];

  const mockMatches = [
    { idMatch: 1, teamAName: 'Thunder FC', teamBName: 'Lions FC',
      lieu: 'Park', statut: 'SCHEDULED', matchDate: '2026-05-01T18:00:00',
      scoreTeamA: 0, scoreTeamB: 0, captainName: 'John' },
    { idMatch: 2, teamAName: 'Team A', teamBName: 'Team B',
      lieu: 'Arena', statut: 'FINISHED', matchDate: '2026-04-01T18:00:00',
      scoreTeamA: 2, scoreTeamB: 1, captainName: 'Ali' }
  ];

  beforeEach(async () => {
    teamServiceSpy  = jasmine.createSpyObj('TeamService',  ['getAllTeams', 'deleteTeam']);
    matchServiceSpy = jasmine.createSpyObj('MatchService', ['getAllMatches', 'deleteMatch']);

    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));
    matchServiceSpy.getAllMatches.and.returnValue(of(mockMatches as any));

    await TestBed.configureTestingModule({
      declarations: [ListTeamsComponent],
      imports: [FormsModule], 
      providers: [
        { provide: TeamService,  useValue: teamServiceSpy  },
        { provide: MatchService, useValue: matchServiceSpy }
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(ListTeamsComponent);
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
    expect(matchServiceSpy.getAllMatches).toHaveBeenCalled();
    expect(component.matches.length).toBe(2);
  });

  it('filteredTeams should equal teams after load', () => {
    expect(component.filteredTeams.length).toBe(2);
  });

  it('filteredMatches should equal matches after load', () => {
    expect(component.filteredMatches.length).toBe(2);
  });

  // ── loadTeams error ───────────────────────────────────────

  it('should set errorMsg when loadTeams fails', () => {
    teamServiceSpy.getAllTeams.and.returnValue(throwError(() => ({ statut: 500 })));
    component.loadTeams();
    expect(component.errorMsg).toBe('Error loading teams.');
    expect(component.isLoading).toBeFalse();
  });

  // ── loadMatches error ─────────────────────────────────────

  it('should set matchErrorMsg when loadMatches fails', () => {
    matchServiceSpy.getAllMatches.and.returnValue(throwError(() => ({ statut: 500 })));
    component.loadMatches();
    expect(component.matchErrorMsg).toBe('Error loading matches.');
    expect(component.isLoadingMatches).toBeFalse();
  });

  // ── applyFilter ───────────────────────────────────────────

  it('applyFilterTest — should filter teams by name', () => {
    component.searchQuery = 'thunder';
    component.applyFilter();
    expect(component.filteredTeams.length).toBe(1);
    expect(component.filteredTeams[0].nom).toBe('Thunder FC');
  });

  it('applyFilterTest — should filter teams by captainFullName', () => {
    component.searchQuery = 'sara';
    component.applyFilter();
    expect(component.filteredTeams.length).toBe(1);
    expect(component.filteredTeams[0].captainFullName).toBe('Sara');
  });

  it('applyFilterTest — should return all teams when query is empty', () => {
    component.searchQuery = '';
    component.applyFilter();
    expect(component.filteredTeams.length).toBe(2);
  });

  it('applyFilterTest — should return empty when no match', () => {
    component.searchQuery = 'zzzzz';
    component.applyFilter();
    expect(component.filteredTeams.length).toBe(0);
  });

  // ── applyMatchFilter ──────────────────────────────────────

  it('applyMatchFilterTest — should filter matches by teamAName', () => {
    component.matchSearchQuery = 'thunder';
    component.applyMatchFilter();
    expect(component.filteredMatches.length).toBe(1);
  });

  it('applyMatchFilterTest — should filter matches by location', () => {
    component.matchSearchQuery = 'arena';
    component.applyMatchFilter();
    expect(component.filteredMatches.length).toBe(1);
  });

  it('applyMatchFilterTest — should return all when query is empty', () => {
    component.matchSearchQuery = '';
    component.applyMatchFilter();
    expect(component.filteredMatches.length).toBe(2);
  });

  // ── getStatusClass ────────────────────────────────────────

  it('getStatusClassTest — SCHEDULED returns a-badge-blue', () => {
    expect(component.getStatusClass('SCHEDULED')).toBe('a-badge-blue');
  });

  it('getStatusClassTest — ONGOING returns a-badge-orange', () => {
    expect(component.getStatusClass('ONGOING')).toBe('a-badge-orange');
  });

  it('getStatusClassTest — FINISHED returns a-badge-green', () => {
    expect(component.getStatusClass('FINISHED')).toBe('a-badge-green');
  });

  it('getStatusClassTest — CANCELLED returns a-badge-red', () => {
    expect(component.getStatusClass('CANCELLED')).toBe('a-badge-red');
  });

  it('getStatusClassTest — unknown returns a-badge-gray', () => {
    expect(component.getStatusClass('UNKNOWN')).toBe('a-badge-gray');
  });

  // ── deleteTeam ────────────────────────────────────────────

  it('deleteTeamTest — should delete team on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    localStorage.setItem('EmailUserConnect', 'admin@test.com');
    teamServiceSpy.deleteTeam.and.returnValue(of(void 0));

    const team = mockTeams[0] as any;
    component.deleteTeam(team);

    expect(teamServiceSpy.deleteTeam).toHaveBeenCalledWith(1, 'admin@test.com');
    expect(component.teams.find(t => t.idTeam === 1)).toBeUndefined();
  });

  it('deleteTeamTest — should not delete when confirm cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteTeam(mockTeams[0] as any);
    expect(teamServiceSpy.deleteTeam).not.toHaveBeenCalled();
  });

  it('deleteTeamTest — should set errorMsg when no email', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    localStorage.removeItem('EmailUserConnect');
    component.deleteTeam(mockTeams[0] as any);
    expect(component.errorMsg).toBe('Admin not connected.');
  });

  it('deleteTeamTest — should set errorMsg on delete error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    localStorage.setItem('EmailUserConnect', 'admin@test.com');
    teamServiceSpy.deleteTeam.and.returnValue(
      throwError(() => ({ error: { message: 'Forbidden' }, statut: 403 }))
    );
    component.deleteTeam(mockTeams[0] as any);
    expect(component.errorMsg).toContain('Thunder FC');
  });

  // ── deleteMatch ───────────────────────────────────────────

  it('deleteMatchTest — should delete match on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    matchServiceSpy.deleteMatch.and.returnValue(of(void 0));

    component.deleteMatch(1);

    expect(matchServiceSpy.deleteMatch).toHaveBeenCalledWith(1);
    expect(component.matches.find(m => m.idMatch === 1)).toBeUndefined();
    expect(component.filteredMatches.find(m => m.idMatch === 1)).toBeUndefined();
  });

  it('deleteMatchTest — should not delete when confirm cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteMatch(1);
    expect(matchServiceSpy.deleteMatch).not.toHaveBeenCalled();
  });

  it('deleteMatchTest — should set matchErrorMsg on error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    matchServiceSpy.deleteMatch.and.returnValue(
      throwError(() => ({ statut: 500 }))
    );
    component.deleteMatch(1);
    expect(component.matchErrorMsg).toBe('Cannot delete this match.');
  });
});

