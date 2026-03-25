import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingsComponent } from './trainings.component';
import { TrainingService } from 'src/app/services/training.service';
import { TeamService } from 'src/app/services/team.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('TrainingsComponent', () => {
  let component: TrainingsComponent;
  let fixture: ComponentFixture<TrainingsComponent>;
  let trainingServiceSpy: jasmine.SpyObj<TrainingService>;
  let teamServiceSpy: jasmine.SpyObj<TeamService>;

  const mockTrainings = [
    { idTraining: 1, title: 'Soccer Techniques', status: 'PLANNED',
      participantCount: 10, teamName: 'Thunder FC',
      trainingDate: '2026-05-01T10:00:00', durationInMinutes: 60,
      location: 'Tunis', description: 'desc', exercises: '' }
  ];

  const mockTeams = [
    { idTeam: 1, name: 'Thunder FC', sport: 'Soccer' }
  ];

  beforeEach(async () => {
    trainingServiceSpy = jasmine.createSpyObj('TrainingService',
      ['getAllTrainings', 'addTraining', 'updateTraining', 'deleteTraining']);
    teamServiceSpy = jasmine.createSpyObj('TeamService', ['getAllTeams']);

    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));
    teamServiceSpy.getAllTeams.and.returnValue(of(mockTeams as any));

    await TestBed.configureTestingModule({
      declarations: [TrainingsComponent],
      imports: [RouterTestingModule, ReactiveFormsModule, FormsModule],
      providers: [
        { provide: TrainingService, useValue: trainingServiceSpy },
        { provide: TeamService,     useValue: teamServiceSpy     }
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(TrainingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load trainings on init', () => {
    expect(trainingServiceSpy.getAllTrainings).toHaveBeenCalled();
    expect(component.trainings.length).toBe(1);
  });

  it('should load teams on init', () => {
    expect(teamServiceSpy.getAllTeams).toHaveBeenCalled();
    expect(component.teams.length).toBe(1);
  });

  it('should init createTrainingForm on init', () => {
    expect(component.createTrainingForm).toBeDefined();
  });

  it('should init editTrainingForm on init', () => {
    expect(component.editTrainingForm).toBeDefined();
  });

  // ── loadTrainings ─────────────────────────────────────────

  it('loadTrainingsTest — should set errorMsg on error', () => {
    trainingServiceSpy.getAllTrainings.and.returnValue(
      throwError(() => ({ status: 500 }))
    );
    component.loadTrainings();
    expect(component.errorMsg).toContain('500');
    expect(component.isLoading).toBeFalse();
  });

  // ── createTraining ────────────────────────────────────────

  it('createTrainingTest — should not create if form invalid', () => {
    component.createTrainingForm.reset();
    component.createTraining();
    expect(trainingServiceSpy.addTraining).not.toHaveBeenCalled();
  });

  it('createTrainingTest — should not create if no team selected', () => {
    component.createTrainingForm.setValue({
      title: 'Soccer Techniques',
      description: '',
      trainingDate: '2027-01-01T10:00:00',
      durationInMinutes: 60,
      location: 'Tunis',
      exercises: ''
    });
    component.selectedTeamId = 0;
    component.createTraining();
    expect(component.errorMsg).toBe('Please select a team.');
  });

  it('createTrainingTest — should not create if date in past', () => {
    component.createTrainingForm.setValue({
      title: 'Soccer Techniques',
      description: '',
      trainingDate: '2020-01-01T10:00:00',
      durationInMinutes: 60,
      location: 'Tunis',
      exercises: ''
    });
    component.selectedTeamId = 1;
    component.createTraining();
    expect(component.errorMsg).toBe('Training date must be in the future.');
  });

  it('createTrainingTest — should create successfully', () => {
    trainingServiceSpy.addTraining.and.returnValue(of({} as any));
    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));

    component.createTrainingForm.setValue({
      title: 'Soccer Techniques',
      description: '',
      trainingDate: '2027-06-01T10:00:00',
      durationInMinutes: 60,
      location: 'Tunis',
      exercises: ''
    });
    component.selectedTeamId = 1;
    component.createTraining();

    expect(trainingServiceSpy.addTraining).toHaveBeenCalled();
    expect(component.successMsg).toBe('Training session created successfully!');
  });

  // ── openEditModal ─────────────────────────────────────────

  it('openEditModalTest — should populate editTrainingForm', () => {
    component.openEditModal(mockTrainings[0] as any);
    expect(component.showEditModal).toBeTrue();
    expect(component.editTrainingForm.value.title).toBe('Soccer Techniques');
  });

  // ── updateTraining ────────────────────────────────────────

  it('updateTrainingTest — should not update if form invalid', () => {
    component.editTrainingForm.get('title')?.setValue('A'); // trop court
    component.updateTraining();
    expect(trainingServiceSpy.updateTraining).not.toHaveBeenCalled();
  });

  it('updateTrainingTest — should not update if date in past', () => {
    component.editTrainingForm.patchValue({
      idTraining: 1,
      trainingDate: '2020-01-01T10:00:00'
    });
    component.updateTraining();
    expect(component.errorMsg).toBe('Training date must be in the future.');
  });

  it('updateTrainingTest — should update successfully', () => {
    trainingServiceSpy.updateTraining.and.returnValue(of({} as any));
    trainingServiceSpy.getAllTrainings.and.returnValue(of(mockTrainings as any));

    component.editTrainingForm.patchValue({
      idTraining: 1,
      title: 'Updated Title',
      trainingDate: ''
    });
    component.updateTraining();

    expect(trainingServiceSpy.updateTraining).toHaveBeenCalled();
    expect(component.successMsg).toBe('Training updated successfully!');
  });

  // ── deleteTraining ────────────────────────────────────────

  it('deleteTrainingTest — should delete on confirm', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    trainingServiceSpy.deleteTraining.and.returnValue(of(void 0));

    component.deleteTraining(1);

    expect(trainingServiceSpy.deleteTraining).toHaveBeenCalledWith(1);
    expect(component.trainings.find(t => t.idTraining === 1)).toBeUndefined();
  });

  it('deleteTrainingTest — should not delete when cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.deleteTraining(1);
    expect(trainingServiceSpy.deleteTraining).not.toHaveBeenCalled();
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

  // ── getProgress ───────────────────────────────────────────

  it('getProgressTest — should return 40 for 10/25', () => {
    expect(component.getProgress(10)).toBe(40);
  });

  it('getProgressTest — should cap at 100', () => {
    expect(component.getProgress(30)).toBe(100);
  });

  // ── resetForm ─────────────────────────────────────────────

  it('resetFormTest — should reset selectedTeamId to 0', () => {
    component.selectedTeamId = 5;
    component.resetForm();
    expect(component.selectedTeamId).toBe(0);
  });

  it('resetFormTest — should clear errorMsg', () => {
    component.errorMsg = 'some error';
    component.resetForm();
    expect(component.errorMsg).toBe('');
  });
});