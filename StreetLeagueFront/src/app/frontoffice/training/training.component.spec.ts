import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingComponent } from './training.component';
import { TrainingService } from 'src/app/services/training.service';
import { of, throwError } from 'rxjs';

describe('TrainingComponent', () => {
  let component: TrainingComponent;
  let fixture: ComponentFixture<TrainingComponent>;
  let trainingServiceSpy: jasmine.SpyObj<TrainingService>;

  const mockTrainings = [
    { idTraining: 1, title: 'Soccer Techniques', status: 'PLANNED',
      participantCount: 10, teamName: 'Thunder FC',
      trainingDate: '2026-05-01T10:00:00', durationInMinutes: 60,
      location: 'Tunis', description: 'desc', exercises: '' },
    { idTraining: 2, title: 'Basketball Drills', status: 'COMPLETED',
      participantCount: 25, teamName: 'Lions FC',
      trainingDate: '2026-04-01T10:00:00', durationInMinutes: 90,
      location: 'Sfax', description: 'desc2', exercises: '' }
  ];

  beforeEach(async () => {
    trainingServiceSpy = jasmine.createSpyObj('TrainingService',
      ['getAllTrainings', 'joinTraining', 'leaveTraining']);

    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));

    await TestBed.configureTestingModule({
      declarations: [TrainingComponent],
      providers: [
        { provide: TrainingService, useValue: trainingServiceSpy }
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(TrainingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load trainings on init', () => {
    expect(trainingServiceSpy.getAllTrainings).toHaveBeenCalled();
    expect(component.trainings.length).toBe(2);
  });

  it('isLoading should be false after load', () => {
    expect(component.isLoading).toBeFalse();
  });

  // ── loadTrainings error ───────────────────────────────────

  it('loadTrainingsTest — should set errorMsg on error', () => {
    trainingServiceSpy.getAllTrainings.and.returnValue(
      throwError(() => ({ status: 500 }))
    );
    component.loadTrainings();
    expect(component.errorMsg).toContain('500');
    expect(component.isLoading).toBeFalse();
  });

  // ── joinTraining ──────────────────────────────────────────

  it('joinTrainingTest — should join successfully', () => {
    trainingServiceSpy.joinTraining.and.returnValue(of({} as any));
    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));

    component.joinTraining(1);

    expect(trainingServiceSpy.joinTraining).toHaveBeenCalledWith(1);
    expect(component.successMsg).toBe('Successfully joined the session!');
    expect(component.joinedTrainingIds.has(1)).toBeTrue();
  });

  it('joinTrainingTest — should set errorMsg on error', () => {
    trainingServiceSpy.joinTraining.and.returnValue(
      throwError(() => ({ error: { message: 'Already joined' }, status: 400 }))
    );
    component.joinTraining(1);
    expect(component.errorMsg).toBe('Already joined');
  });

  it('joinTrainingTest — should clear errorMsg before joining', () => {
    component.errorMsg = 'old error';
    trainingServiceSpy.joinTraining.and.returnValue(of({} as any));
    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));
    component.joinTraining(1);
    expect(component.errorMsg).toBe('');
  });

  // ── leaveTraining ─────────────────────────────────────────

  it('leaveTrainingTest — should leave successfully', () => {
    component.joinedTrainingIds.add(1);
    trainingServiceSpy.leaveTraining.and.returnValue(of({} as any));
    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));

    component.leaveTraining(1);

    expect(trainingServiceSpy.leaveTraining).toHaveBeenCalledWith(1);
    expect(component.successMsg).toBe('You have left the session.');
    expect(component.joinedTrainingIds.has(1)).toBeFalse();
  });

  it('leaveTrainingTest — should set errorMsg on error', () => {
    trainingServiceSpy.leaveTraining.and.returnValue(
      throwError(() => ({ error: { message: 'Not a member' }, status: 400 }))
    );
    component.leaveTraining(1);
    expect(component.errorMsg).toBe('Not a member');
  });

  // ── hasJoined ─────────────────────────────────────────────

  it('hasJoinedTest — should return true when joined', () => {
    component.joinedTrainingIds.add(1);
    expect(component.hasJoined(1)).toBeTrue();
  });

  it('hasJoinedTest — should return false when not joined', () => {
    expect(component.hasJoined(99)).toBeFalse();
  });

  // ── getStatusClass ────────────────────────────────────────

  it('getStatusClassTest — PLANNED returns badge-blue', () => {
    expect(component.getStatusClass('PLANNED')).toBe('badge-blue');
  });

  it('getStatusClassTest — COMPLETED returns badge-green', () => {
    expect(component.getStatusClass('COMPLETED')).toBe('badge-green');
  });

  it('getStatusClassTest — CANCELLED returns badge-red', () => {
    expect(component.getStatusClass('CANCELLED')).toBe('badge-red');
  });

  it('getStatusClassTest — unknown returns badge-gray', () => {
    expect(component.getStatusClass('UNKNOWN')).toBe('badge-gray');
  });

  // ── getProgress ───────────────────────────────────────────

  it('getProgressTest — should return 40 for 10/25', () => {
    expect(component.getProgress(10)).toBe(40);
  });

  it('getProgressTest — should return 100 for 25/25', () => {
    expect(component.getProgress(25)).toBe(100);
  });

  it('getProgressTest — should cap at 100 when over max', () => {
    expect(component.getProgress(30)).toBe(100);
  });

  it('getProgressTest — should return 0 for 0', () => {
    expect(component.getProgress(0)).toBe(0);
  });
});