import { environment } from 'src/environments/environment';
import { TestBed } from '@angular/core/testing';
import { MatchService } from './match.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('MatchService', () => {
  let service: MatchService;
  let httpMock: HttpTestingController;

  const base = `${environment.baseUrl}/match`;

  const mockMatch = {
    idMatch: 1, teamAName: 'Thunder FC', teamBName: 'Lions FC',
    matchDate: '2026-05-01T18:00:00', location: 'Park',
    status: 'SCHEDULED', scoreTeamA: 0, scoreTeamB: 0, captainName: 'John'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MatchService]
    });
    service  = TestBed.inject(MatchService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.setItem('TokenUserConnect',  'test-token');
    localStorage.setItem('EmailUserConnect',  'test@test.com');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ── getAllMatchs ───────────────────────────────────────────

  it('getAllMatchsTest — should call GET /match/showMatchs', () => {
    service.getAllMatchs().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].idMatch).toBe(1);
    });

    const req = httpMock.expectOne(`${base}/showMatchs`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([mockMatch]);
  });

  // ── getMatchById ──────────────────────────────────────────

  it('getMatchByIdTest — should call GET /match/showMatchById/1', () => {
    service.getMatchById(1).subscribe(res => {
      expect(res.idMatch).toBe(1);
    });

    const req = httpMock.expectOne(`${base}/showMatchById/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockMatch);
  });

  // ── addMatch ──────────────────────────────────────────────

  it('addMatchTest — should call POST /match/add', () => {
    const matchRequest = {
      matchDate: '2026-05-01T18:00:00',
      location: 'Central Park'
    };

    service.addMatch(matchRequest as any, 1, 2).subscribe(res => {
      expect(res.idMatch).toBe(1);
    });

    const req = httpMock.expectOne(
      `${base}/add?teamAId=1&teamBId=2&email=test@test.com`
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(matchRequest);
    req.flush(mockMatch);
  });

  // ── deleteMatch ───────────────────────────────────────────

  it('deleteMatchTest — should call DELETE /match/delete/1', () => {
    service.deleteMatch(1).subscribe();

    const req = httpMock.expectOne(
      `${base}/delete/1?email=test@test.com`
    );
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(null);
  });

  // ── updateMatch ───────────────────────────────────────────

  it('updateMatchTest — should call PUT /match/update', () => {
    const updateDto = { idMatch: 1, location: 'New Park' };

    service.updateMatch(updateDto).subscribe(res => {
      expect(res.idMatch).toBe(1);
    });

    const req = httpMock.expectOne(
      `${base}/update?email=test@test.com`
    );
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateDto);
    req.flush(mockMatch);
  });


  
});
