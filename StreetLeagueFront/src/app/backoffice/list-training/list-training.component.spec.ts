import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListTrainingComponent } from './list-training.component';
import { TrainingService } from 'src/app/services/training.service';
import { of, throwError } from 'rxjs';

describe('ListTrainingComponent', () => {
  let component: ListTrainingComponent;
  let fixture: ComponentFixture<ListTrainingComponent>;
  let trainingServiceSpy: jasmine.SpyObj<TrainingService>;

  const mockTrainings = [
    { idTraining: 1, titre: 'Soccer Techniques', statut: 'PLANNED',
      participantCount: 10, teamName: 'Thunder FC',
      trainingDate: '2026-05-01T10:00:00', durationInMinutes: 60,
      lieu: 'Tunis', description: 'desc', exercises: '' },
    { idTraining: 2, titre: 'Basketball Drills', statut: 'COMPLETED',
      participantCount: 25, teamName: 'Lions FC',
      trainingDate: '2026-04-01T10:00:00', durationInMinutes: 90,
      lieu: 'Sfax', description: 'desc2', exercises: '' },
    { idTraining: 3, titre: 'Volleyball Basics', statut: 'CANCELLED',
      participantCount: 5, teamName: 'Eagles',
      trainingDate: '2026-03-01T10:00:00', durationInMinutes: 45,
      lieu: 'Sousse', description: 'desc3', exercises: '' }
  ];

  beforeEach(async () => {
    trainingServiceSpy = jasmine.createSpyObj('TrainingService',
      ['getAllTrainings', 'deleteTraining']);

    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));

    await TestBed.configureTestingModule({
      declarations: [ListTrainingComponent],
      providers: [
        { provide: TrainingService, useValue: trainingServiceSpy }
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(ListTrainingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load trainings on init', () => {
    expect(trainingServiceSpy.getAllTrainings).toHaveBeenCalled();
    expect(component.trainings.length).toBe(3);
  });

  it('isLoading should be false after load', () => {
    expect(component.isLoading).toBeFalse();
  });

  // ── loadTrainings error ───────────────────────────────────

  it('loadTrainingsTest — should set errorMsg when load fails', () => {
    trainingServiceSpy.getAllTrainings.and.returnValue(
      throwError(() => ({ statut: 500 }))
    );
    component.loadTrainings();
    expect(component.errorMsg).toContain('500');
    expect(component.isLoading).toBeFalse();
  });

  // ── Computed stats ────────────────────────────────────────

  it('plannedCountTest — should return 1 planned training', () => {
    expect(component.plannedCount).toBe(1);
  });

  it('completedCountTest — should return 1 completed training', () => {
    expect(component.completedCount).toBe(1);
  });

  it('totalParticipantsTest — should return sum of all participants', () => {
    expect(component.totalParticipants).toBe(40); // 10 + 25 + 5
  });

  it('activeCoachesTest — should return 0 when coaches list is empty', () => {
    expect(component.activeCoaches).toBe(0);
  });

  // ── getStatusClass ────────────────────────────────────────

  it('getStatusClassTest — PLANNED returns a-badge-blue', () => {
    expect(component.getStatusClass('PLANNED')).toBe('a-badge-blue');
  });

  it('getStatusClassTest — COMPLETED returns a-badge-green', () => {
    expect(component.getStatusClass('COMPLETED')).toBe('a-badge-green');
  });

  it('getStatusClassTest — CANCELLED returns a-badge-red', () => {
    expect(component.getStatusClass('CANCELLED')).toBe('a-badge-red');
  });

  it('getStatusClassTest — unknown returns a-badge-gray', () => {
    expect(component.getStatusClass('UNKNOWN')).toBe('a-badge-gray');
  });

  // ── getCoachStatusClass ───────────────────────────────────

  it('getCoachStatusClassTest — Active returns a-badge-green', () => {
    expect(component.getCoachStatusClass('Active')).toBe('a-badge-green');
  });

  it('getCoachStatusClassTest — Pending returns a-badge-orange', () => {
    expect(component.getCoachStatusClass('Pending')).toBe('a-badge-orange');
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

  it('getProgressTest — should return 0 for 0 participants', () => {
    expect(component.getProgress(0)).toBe(0);
  });

  // ── getProgressColor ──────────────────────────────────────

  it('getProgressColorTest — should return teal for low count', () => {
    expect(component.getProgressColor(5)).toBe('var(--teal)');
  });

  it('getProgressColorTest — should return orange for 75%+', () => {
    expect(component.getProgressColor(20)).toBe('var(--orange)');
  });

  it('getProgressColorTest — should return red when full', () => {
    expect(component.getProgressColor(25)).toBe('var(--red)');
  });

  // ── deleteTraining ────────────────────────────────────────

  it('deleteTrainingTest — should delete training on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    trainingServiceSpy.deleteTraining.and.returnValue(of(void 0));

    component.deleteTraining(1);

    expect(trainingServiceSpy.deleteTraining).toHaveBeenCalledWith(1);
    expect(component.trainings.find(t => t.idTraining === 1)).toBeUndefined();
  });

  it('deleteTrainingTest — should not delete when confirm cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteTraining(1);
    expect(trainingServiceSpy.deleteTraining).not.toHaveBeenCalled();
  });

  it('deleteTrainingTest — should set successMsg after delete', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    trainingServiceSpy.deleteTraining.and.returnValue(of(void 0));
    component.deleteTraining(1);
    expect(component.successMsg).toBe('Training deleted.');
  });

  it('deleteTrainingTest — should set errorMsg on delete error', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    trainingServiceSpy.deleteTraining.and.returnValue(
      throwError(() => ({ statut: 403 }))
    );
    component.deleteTraining(1);
    expect(component.errorMsg).toContain('403');
  });
});

