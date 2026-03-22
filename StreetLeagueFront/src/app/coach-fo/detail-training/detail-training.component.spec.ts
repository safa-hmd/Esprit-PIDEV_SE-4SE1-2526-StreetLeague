import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailTrainingComponent } from './detail-training.component';
import { TrainingService } from 'src/app/services/training.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('DetailTrainingComponent', () => {
  let component: DetailTrainingComponent;
  let fixture: ComponentFixture<DetailTrainingComponent>;
  let trainingServiceSpy: jasmine.SpyObj<TrainingService>;
  let router: Router;

  const mockTraining = {
    idTraining: 1, title: 'Soccer Techniques', status: 'PLANNED',
    participantCount: 10, teamName: 'Thunder FC',
    trainingDate: '2026-05-01T10:00:00', durationInMinutes: 60,
    location: 'Tunis', description: 'desc', exercises: ''
  };

  beforeEach(async () => {
    trainingServiceSpy = jasmine.createSpyObj('TrainingService', ['getTrainingById']);
    trainingServiceSpy.getTrainingById.and.returnValue(of(mockTraining as any));

    await TestBed.configureTestingModule({
      declarations: [DetailTrainingComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: TrainingService, useValue: trainingServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } }
        }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(DetailTrainingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load training on init', () => {
    expect(trainingServiceSpy.getTrainingById).toHaveBeenCalledWith(1);
    expect(component.training).toEqual(mockTraining as any);
    expect(component.isLoading).toBeFalse();
  });

  // ── ngOnInit error ────────────────────────────────────────

  it('ngOnInitTest — should set errorMsg when training not found', () => {
    trainingServiceSpy.getTrainingById.and.returnValue(
      throwError(() => ({ status: 404 }))
    );
    component.ngOnInit();
    expect(component.errorMsg).toContain('404');
    expect(component.isLoading).toBeFalse();
  });

  // ── goBack ────────────────────────────────────────────────

  it('goBackTest — should navigate to trainingCoach', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.goBack();
    expect(navigateSpy).toHaveBeenCalledWith(['/coach/trainingCoach']);
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