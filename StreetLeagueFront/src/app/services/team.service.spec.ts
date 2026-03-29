import { TestBed } from '@angular/core/testing';
import { TeamService } from './team.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('TeamService', () => {
  let service:  TeamService;
  let httpMock: HttpTestingController;

  const base = 'http://localhost:8086/StreetLeague/team';

  const mockTeam = {
    idTeam: 1, name: 'Thunder FC', sport: 'Soccer',
    captainFullName: 'John', playerCount: 5,
    captainEmail: 'john@test.com', level: 'BEGINNER'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:   [HttpClientTestingModule],
      providers: [TeamService]
    });
    service  = TestBed.inject(TeamService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.setItem('TokenUserConnect', 'test-token');
    localStorage.setItem('EmailUserConnect', 'john@test.com');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ── getAllTeams ───────────────────────────────────────────

  it('getAllTeamsTest — should call GET /team/showTeams', () => {
    service.getAllTeams().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].name).toBe('Thunder FC');
    });

    const req = httpMock.expectOne(`${base}/showTeams`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([mockTeam]);
  });

  // ── getTeamById ───────────────────────────────────────────

  it('getTeamByIdTest — should call GET /team/showTeamById/1', () => {
    service.getTeamById(1).subscribe(res => {
      expect(res.name).toBe('Thunder FC');
    });

    const req = httpMock.expectOne(`${base}/showTeamById/1`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockTeam);
  });

  // ── addTeam ───────────────────────────────────────────────

  it('addTeamTest — should call POST /team/add', () => {
    const newTeam = { name: 'Thunder FC', sport: 'Soccer', level: 'BEGINNER' };

    service.addTeam(newTeam as any).subscribe(res => {
      expect(res.name).toBe('Thunder FC');
    });

    const req = httpMock.expectOne(`${base}/add?email=john@test.com`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newTeam);
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockTeam);
  });

  // ── deleteTeam ────────────────────────────────────────────

  it('deleteTeamTest — should call DELETE /team/delete/1', () => {
    service.deleteTeam(1, 'john@test.com').subscribe();

    const req = httpMock.expectOne(`${base}/delete/1?email=john@test.com`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(null);
  });

  // ── updateTeam ────────────────────────────────────────────

  it('updateTeamTest — should call PUT /team/update/1', () => {
    const updatedTeam = { ...mockTeam, name: 'Updated FC' };

    service.updateTeam(updatedTeam as any, 'john@test.com').subscribe(res => {
      expect(res.name).toBe('Thunder FC');
    });

    const req = httpMock.expectOne(`${base}/update/1?email=john@test.com`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedTeam);
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockTeam);
  });

  // ── joinTeam ──────────────────────────────────────────────

  it('joinTeamTest — should call POST /team/1/join', () => {
    service.joinTeam(1, 'john@test.com').subscribe();

    const req = httpMock.expectOne(`${base}/1/join?email=john@test.com`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockTeam);
  });

  // ── leaveTeam ─────────────────────────────────────────────

  it('leaveTeamTest — should call DELETE /team/1/leave', () => {
    service.leaveTeam(1, 'john@test.com').subscribe();

    const req = httpMock.expectOne(`${base}/1/leave?email=john@test.com`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(null);
  });

  // ── getMyTeams ────────────────────────────────────────────

  it('getMyTeamsTest — should call GET /team/my-teams with captainId', () => {
    service.getMyTeams(42).subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].name).toBe('Thunder FC');
    });

    const req = httpMock.expectOne(`${base}/my-teams?captainId=42`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([mockTeam]);
  });

  // ── getTeamsByCoach ───────────────────────────────────────

  it('getTeamsByCoachTest — should call GET /team/myTeams with email from localStorage', () => {
    service.getTeamsByCoach().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].name).toBe('Thunder FC');
    });

    const req = httpMock.expectOne(`${base}/myTeams?email=john@test.com`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([mockTeam]);
  });
});