import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TeamComponent } from './team.component';
import { TeamService } from '../../services/team.service';
import { MatchService } from '../../services/match.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('TeamComponent', () => {
  let component: TeamComponent;
  let fixture: ComponentFixture<TeamComponent>;
  let teamServiceSpy: jasmine.SpyObj<TeamService>;
  let matchServiceSpy: jasmine.SpyObj<MatchService>;
  let router: Router;

  const mockTeams = [
    { idTeam: 1, name: 'Thunder FC', sport: 'Soccer', level: 'BEGINNER',
      captainEmail: 'test@test.com', captainFullName: 'John',
      playerCount: 5, victories: 2, defeats: 1, matches: 3, description: '' },
    { idTeam: 2, name: 'Lions FC', sport: 'Basketball', level: 'ADVANCED',
      captainEmail: 'other@test.com', captainFullName: 'Sara',
      playerCount: 3, victories: 1, defeats: 2, matches: 3, description: '' }
  ];

  const mockMatches = [
    { idMatch: 1, teamAName: 'Thunder FC', teamBName: 'Lions FC',
      matchDate: '2026-05-01T18:00:00', location: 'Park',
      status: 'SCHEDULED', scoreTeamA: 0, scoreTeamB: 0,
      captainAEmail: 'test@test.com', captainBEmail: 'other@test.com',
      captainName: 'John' }
  ];

  beforeEach(async () => {
    teamServiceSpy  = jasmine.createSpyObj('TeamService',
      ['getAllTeams', 'addTeam', 'updateTeam', 'deleteTeam', 'joinTeam']);
    matchServiceSpy = jasmine.createSpyObj('MatchService',
      ['getAllMatchs', 'addMatch', 'deleteMatch']);

    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));
    matchServiceSpy.getAllMatchs.and.returnValue(of(mockMatches as any));

    await TestBed.configureTestingModule({
      declarations: [TeamComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, FormsModule],
      providers: [
        { provide: TeamService,    useValue: teamServiceSpy  },
        { provide: MatchService,   useValue: matchServiceSpy },
        { provide: ActivatedRoute, useValue: { queryParams: of({}) } }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(TeamComponent);
    component = fixture.componentInstance;
    localStorage.setItem('EmailUserConnect', 'test@test.com');
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

  it('should init createTeamForm', () => {
    expect(component.createTeamForm).toBeDefined();
  });

  it('should init createMatchForm', () => {
    expect(component.createMatchForm).toBeDefined();
  });

  // ── isMyTeam ──────────────────────────────────────────────

  it('isMyTeamTest — should return true for my team', () => {
    expect(component.isMyTeam(mockTeams[0] as any)).toBeTrue();
  });

  it('isMyTeamTest — should return false for other team', () => {
    expect(component.isMyTeam(mockTeams[1] as any)).toBeFalse();
  });

  // ── isCaptain ─────────────────────────────────────────────

  it('isCaptainTest — should be true when user is captain of a team', () => {
    expect(component.isCaptain).toBeTrue();
  });

  // ── myTeams / otherTeams ──────────────────────────────────

  it('myTeamsTest — should contain only my teams', () => {
    expect(component.myTeams.length).toBe(1);
    expect(component.myTeams[0].captainEmail).toBe('test@test.com');
  });

  it('otherTeamsTest — should contain only other teams', () => {
    expect(component.otherTeams.length).toBe(1);
    expect(component.otherTeams[0].captainEmail).toBe('other@test.com');
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

  // ── onSearch ──────────────────────────────────────────────

  it('onSearchTest — should filter teams by name', () => {
    component.onSearch('thunder');
    expect(component.filteredTeams.length).toBe(1);
    expect(component.filteredTeams[0].name).toBe('Thunder FC');
  });

  it('onSearchTest — should return all when query empty', () => {
    component.onSearch('');
    expect(component.filteredTeams.length).toBe(2);
  });

  it('onSearchTest — should return empty when no match', () => {
    component.onSearch('zzzzz');
    expect(component.filteredTeams.length).toBe(0);
  });

  // ── onSearchMatch ─────────────────────────────────────────

  it('onSearchMatchTest — should filter matches by teamAName', () => {
    component.onSearchMatch('thunder');
    expect(component.filteredMatches.length).toBe(1);
  });

  it('onSearchMatchTest — should filter matches by location', () => {
    component.onSearchMatch('park');
    expect(component.filteredMatches.length).toBe(1);
  });

  it('onSearchMatchTest — should return empty when no match', () => {
    component.onSearchMatch('zzzzz');
    expect(component.filteredMatches.length).toBe(0);
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

  // ── createTeam ────────────────────────────────────────────

  it('createTeamTest — should not create if form invalid', () => {
    component.createTeamForm.reset();
    component.createTeam();
    expect(teamServiceSpy.addTeam).not.toHaveBeenCalled();
  });

  it('createTeamTest — should create successfully', () => {
    teamServiceSpy.addTeam.and.returnValue(of({ name: 'Thunder FC' } as any));
    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));

    component.createTeamForm.setValue({
      name: 'Thunder FC', sport: 'Soccer',
      level: 'BEGINNER', description: ''
    });
    component.createTeam();
    expect(teamServiceSpy.addTeam).toHaveBeenCalled();
    expect(component.successMsg).toContain('Thunder FC');
  });

  // ── openEditTeamModal ─────────────────────────────────────

  it('openEditTeamModalTest — should populate editTeamForm', () => {
    component.openEditTeamModal(mockTeams[0] as any);
    expect(component.showEditTeamModal).toBeTrue();
    expect(component.editTeamForm.value.name).toBe('Thunder FC');
  });

  // ── updateTeam ────────────────────────────────────────────

  it('updateTeamTest — should not update if form invalid', () => {
    component.editTeamForm.get('name')?.setValue('');
    component.updateTeam();
    expect(teamServiceSpy.updateTeam).not.toHaveBeenCalled();
  });

  it('updateTeamTest — should update successfully', () => {
    teamServiceSpy.updateTeam.and.returnValue(of({} as any));
    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));

    component.editTeamForm.setValue({
      idTeam: 1, name: 'Updated FC',
      sport: 'Soccer', level: 'BEGINNER', description: ''
    });
    component.updateTeam();
    expect(teamServiceSpy.updateTeam).toHaveBeenCalled();
    expect(component.successMsg).toBe('Team updated successfully!');
  });

  // ── deleteTeam ────────────────────────────────────────────

  it('deleteTeamTest — should delete on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    teamServiceSpy.deleteTeam.and.returnValue(of(void 0));
    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));

    component.deleteTeam(1);
    expect(teamServiceSpy.deleteTeam).toHaveBeenCalledWith(1, 'test@test.com');
  });

  it('deleteTeamTest — should not delete when cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteTeam(1);
    expect(teamServiceSpy.deleteTeam).not.toHaveBeenCalled();
  });

  // ── joinTeam ──────────────────────────────────────────────

  it('joinTeamTest — should join successfully', () => {
    teamServiceSpy.joinTeam.and.returnValue(of({} as any));
    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));

    component.joinTeam(2);
    expect(teamServiceSpy.joinTeam).toHaveBeenCalled();
    expect(component.successMsg).toBe('You joined the team successfully!');
  });

  // ── createMatch ───────────────────────────────────────────

  it('createMatchTest — should not create if no Team A', () => {
    component.teamAId = 0;
    component.createMatch();
    expect(component.errorMsg).toBe('Please select Team A.');
  });

  it('createMatchTest — should not create if no Team B', () => {
    component.teamAId = 1;
    component.teamBId = 0;
    component.createMatch();
    expect(component.errorMsg).toBe('Please select Team B.');
  });

  it('createMatchTest — should not create if date in past', () => {
    component.teamAId = 1;
    component.teamBId = 2;
    component.createMatchForm.setValue({
      matchDate: '2020-01-01T10:00:00',
      location: 'Central Park'
    });
    component.createMatch();
    expect(component.errorMsg).toBe('Match date must be in the future.');
  });

  it('createMatchTest — should create successfully', () => {
    matchServiceSpy.addMatch.and.returnValue(of({} as any));
    matchServiceSpy.getAllMatchs.and.returnValue(of(mockMatches as any));

    component.teamAId = 1;
    component.teamBId = 2;
    component.createMatchForm.setValue({
      matchDate: '2027-06-01T18:00:00',
      location: 'Central Park'
    });
    component.createMatch();
    expect(matchServiceSpy.addMatch).toHaveBeenCalled();
    expect(component.successMsg).toBe('Match created successfully!');
  });

  // ── deleteMatch ───────────────────────────────────────────

  it('deleteMatchTest — should delete on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    matchServiceSpy.deleteMatch.and.returnValue(of(void 0));

    component.deleteMatch(1);
    expect(matchServiceSpy.deleteMatch).toHaveBeenCalledWith(1);
    expect(component.matches.find(m => m.idMatch === 1)).toBeUndefined();
  });

  it('deleteMatchTest — should not delete when cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteMatch(1);
    expect(matchServiceSpy.deleteMatch).not.toHaveBeenCalled();
  });

  // ── canDeleteMatch ────────────────────────────────────────

  it('canDeleteMatchTest — should return true for captainA', () => {
    expect(component.canDeleteMatch(mockMatches[0] as any)).toBeTrue();
  });

  it('canDeleteMatchTest — should return false for stranger', () => {
    const match = { ...mockMatches[0], captainAEmail: 'a@a.com', captainBEmail: 'b@b.com' };
    expect(component.canDeleteMatch(match as any)).toBeFalse();
  });

  // ── onTeamAChange ─────────────────────────────────────────

  it('onTeamAChangeTest — should set captainAName', () => {
    component.teamAId = 1;
    component.onTeamAChange();
    expect(component.captainAName).toBe('John');
  });

  it('onTeamAChangeTest — should reset teamBId', () => {
    component.teamBId = 2;
    component.teamAId = 1;
    component.onTeamAChange();
    expect(component.teamBId).toBe(0);
  });

  // ── onTeamBChange ─────────────────────────────────────────

  it('onTeamBChangeTest — should set captainBName', () => {
    component.teamBId = 2;
    component.onTeamBChange();
    expect(component.captainBName).toBe('Sara');
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

  // ── navigation ────────────────────────────────────────────

  it('goToDetailsTest — should navigate to detail-match', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goToDetails(mockMatches[0] as any);
    expect(navigateSpy).toHaveBeenCalledWith(['/client/detail-match', 1]);
  });

  it('goToTeamDetailTest — should navigate to detail-team', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goToTeamDetail(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/client/detail-team', 1]);
  });
});