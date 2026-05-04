import { environment } from 'src/environments/environment';
import { TestBed } from '@angular/core/testing';
import { TrainingService } from './training.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('TrainingService', () => {
  let service: TrainingService;
  let httpMock: HttpTestingController;

  const base = `${environment.baseUrl}/training`;

  const mockTraining = {
    idTraining: 1, title: 'Soccer Techniques', status: 'PLANNED',
    participantCount: 10, teamName: 'Thunder FC',
    trainingDate: '2026-05-01T10:00:00', durationInMinutes: 60,
    location: 'Tunis', description: 'desc', exercises: ''
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TrainingService]
    });
    service  = TestBed.inject(TrainingService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.setItem('TokenUserConnect', 'test-token');
    localStorage.setItem('EmailUserConnect', 'coach@test.com');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ── getAllTrainings ───────────────────────────────────────

  it('getAllTrainingsTest — should call GET /training/showTrainings', () => {
    service.getAllTrainings().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].title).toBe('Soccer Techniques');
    });

    const req = httpMock.expectOne(`${base}/showTrainings`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush([mockTraining]);
  });

  // ── getTrainingById ───────────────────────────────────────

  it('getTrainingByIdTest — should call GET /training/showTrainingById/1', () => {
    service.getTrainingById(1).subscribe(res => {
      expect(res.title).toBe('Soccer Techniques');
    });

    const req = httpMock.expectOne(`${base}/showTrainingById/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockTraining);
  });

  // ── addTraining ───────────────────────────────────────────

  it('addTrainingTest — should call POST /training/add', () => {
    const dto = {
      title: 'Soccer Techniques', description: 'desc',
      trainingDate: '2026-05-01T10:00:00',
      durationInMinutes: 60, location: 'Tunis', exercises: ''
    };

    service.addTraining(dto as any, 1).subscribe(res => {
      expect(res.title).toBe('Soccer Techniques');
    });

    const req = httpMock.expectOne(`${base}/add?teamId=1&email=coach@test.com`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush(mockTraining);
  });

  // ── updateTraining ────────────────────────────────────────

  it('updateTrainingTest — should call PUT /training/update', () => {
    const dto = { idTraining: 1, title: 'Updated Title' };

    service.updateTraining(dto as any).subscribe(res => {
      expect(res.idTraining).toBe(1);
    });

    const req = httpMock.expectOne(`${base}/update?email=coach@test.com`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(dto);
    req.flush(mockTraining);
  });

  // ── deleteTraining ────────────────────────────────────────

  it('deleteTrainingTest — should call DELETE /training/delete/1', () => {
    service.deleteTraining(1).subscribe();

    const req = httpMock.expectOne(`${base}/delete/1?email=coach@test.com`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(null);
  });

  // ── joinTraining ──────────────────────────────────────────

  it('joinTrainingTest — should call POST /training/1/join', () => {
    service.joinTraining(1).subscribe(res => {
      expect(res.idTraining).toBe(1);
    });

    const req = httpMock.expectOne(`${base}/1/join?email=coach@test.com`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockTraining);
  });

  // ── leaveTraining ─────────────────────────────────────────

  it('leaveTrainingTest — should call DELETE /training/1/leave', () => {
    service.leaveTraining(1).subscribe(res => {
      expect(res.idTraining).toBe(1);
    });

    const req = httpMock.expectOne(`${base}/1/leave?email=coach@test.com`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush(mockTraining);
  });
});
