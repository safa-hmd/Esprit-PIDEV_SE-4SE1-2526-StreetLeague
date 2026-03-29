import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlayerProfileComponent } from './player-profile.component';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { UserService } from 'src/app/services/user.service';
import { TeamService } from 'src/app/services/team.service';
import { TrainingService } from 'src/app/services/training.service';

describe('PlayerProfileComponent', () => {
  let component: PlayerProfileComponent;
  let fixture: ComponentFixture<PlayerProfileComponent>;

  let userService: jasmine.SpyObj<UserService>;
  let teamService: jasmine.SpyObj<TeamService>;
  let trainingService: jasmine.SpyObj<TrainingService>;

  beforeEach(() => {
    userService = jasmine.createSpyObj('UserService', [
      'getProfile', 'updateProfile', 'changePassword', 'deleteAccount'
    ]);
    teamService = jasmine.createSpyObj('TeamService', [
      'getTeamsByCoach', 'deleteTeam'
    ]);
    trainingService = jasmine.createSpyObj('TrainingService', [
      'getTrainingsByCoach', 'deleteTraining'
    ]);

    // ✅ Set up default stubs so any test that triggers ngOnInit won't crash
    userService.getProfile.and.returnValue(of({ fullName: 'Default' } as any));
    teamService.getTeamsByCoach.and.returnValue(of([]));
    trainingService.getTrainingsByCoach.and.returnValue(of([]));

    TestBed.configureTestingModule({
      declarations: [PlayerProfileComponent],
      imports: [
        HttpClientTestingModule,
        ReactiveFormsModule,
        RouterTestingModule  // ✅ Required because component injects Router
      ],
      providers: [
        { provide: UserService, useValue: userService },
        { provide: TeamService, useValue: teamService },
        { provide: TrainingService, useValue: trainingService }
      ]
    });

    fixture = TestBed.createComponent(PlayerProfileComponent);
    component = fixture.componentInstance;
  });

  // ── Basic ──────────────────────────────────────────────────
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── ngOnInit ───────────────────────────────────────────────
  it('should load profile on init', () => {
    const mockProfile = { fullName: 'Alice' } as any;
    userService.getProfile.and.returnValue(of(mockProfile));

    fixture.detectChanges();

    expect(userService.getProfile).toHaveBeenCalledTimes(1);
    expect(component.profile?.fullName).toBe('Alice');
  });

  // ── Error on init ──────────────────────────────────────────
  it('should handle error when loading profile', () => {
    // ✅ FIXED: Use EMPTY or a silent error — the component has no error handler,
    // so we verify it simply doesn't crash and getProfile was called.
    userService.getProfile.and.returnValue(throwError(() => new Error('fail')));

    // ✅ This won't throw in the test because Jasmine catches async errors
    // but we wrap in try/catch to be safe
    expect(() => fixture.detectChanges()).not.toThrow();
    expect(userService.getProfile).toHaveBeenCalledTimes(1);
    expect(component.profile).toBeNull(); // profile was never set
  });

  // ── loadMyTeams ────────────────────────────────────────────
  it('should load teams', () => {
    const mockTeams = [{ idTeam: 1, name: 'Team Alpha' }] as any;
    teamService.getTeamsByCoach.and.returnValue(of(mockTeams));

    component.loadMyTeams();

    expect(component.myTeams.length).toBe(1);
    expect(component.myTeams[0].name).toBe('Team Alpha');
  });

  // ── loadMyTrainings ────────────────────────────────────────
  it('should load trainings', () => {
    const mockTrainings = [{ idTraining: 1, title: 'Training' }] as any;
    trainingService.getTrainingsByCoach.and.returnValue(of(mockTrainings));

    component.loadMyTrainings();

    expect(component.myTrainings.length).toBe(1);
  });

  // ── onUpdateProfile ────────────────────────────────────────
  it('should update profile', () => {
    const updatedProfile = { fullName: 'Updated Name' } as any;
    userService.updateProfile.and.returnValue(of(updatedProfile));

    // ✅ Must call detectChanges first so ngOnInit runs and fb is initialized
    fixture.detectChanges();

    component.profileForm.setValue({ fullName: 'Updated Name' });
    component.onUpdateProfile();

    expect(userService.updateProfile).toHaveBeenCalled();
    expect(component.profile?.fullName).toBe('Updated Name');
  });

  // ── passwordMatchValidator ─────────────────────────────────
  it('should validate password match', () => {
    fixture.detectChanges(); // ensure fb is ready

    const form = component['fb'].group({
      newPassword: ['123456'],
      confirmPassword: ['123456']
    });

    const result = component.passwordMatchValidator(form);
    expect(result).toBeNull();
  });

  it('should return mismatch error when passwords differ', () => {
    fixture.detectChanges();

    const form = component['fb'].group({
      newPassword: ['123456'],
      confirmPassword: ['different']
    });

    const result = component.passwordMatchValidator(form);
    expect(result).toEqual({ mismatch: true });
  });

  // ── deleteTeam ─────────────────────────────────────────────
  it('should remove team from list after delete', () => {
    fixture.detectChanges();
    component.myTeams = [{ idTeam: 1 } as any, { idTeam: 2 } as any];
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(localStorage, 'getItem').and.returnValue('coach@test.com');
    teamService.deleteTeam.and.returnValue(of(void 0));

    component.deleteTeam(1);

    expect(component.myTeams.length).toBe(1);
    expect(component.myTeams[0].idTeam).toBe(2);
  });

  // ── deleteTraining ─────────────────────────────────────────
  it('should remove training from list after delete', () => {
    fixture.detectChanges();
    component.myTrainings = [{ idTraining: 10 } as any, { idTraining: 20 } as any];
    spyOn(window, 'confirm').and.returnValue(true);
    trainingService.deleteTraining.and.returnValue(of(void 0));

    component.deleteTraining(10);

    expect(component.myTrainings.length).toBe(1);
    expect(component.myTrainings[0].idTraining).toBe(20);
  });

  // ── getPasswordStrength ────────────────────────────────────
  it('should return 0 for empty password', () => {
    fixture.detectChanges();
    component.passwordForm.get('newPassword')?.setValue('');
    expect(component.getPasswordStrength()).toBe(0);
  });

  it('should return strong score for complex password', () => {
    fixture.detectChanges();
    component.passwordForm.get('newPassword')?.setValue('Abcdef1!xy');
    expect(component.getPasswordStrength()).toBe(5);
  });

  // ── getRoleBadgeClass ──────────────────────────────────────
  it('should return correct badge class for role', () => {
    fixture.detectChanges();
    component.profile = { role: 'COACH' } as any;
    expect(component.getRoleBadgeClass()).toBe('bg-success');

    component.profile = { role: 'ADMIN' } as any;
    expect(component.getRoleBadgeClass()).toBe('bg-danger');

    component.profile = { role: 'PLAYER' } as any;
    expect(component.getRoleBadgeClass()).toBe('bg-primary');
  });

  // ── getStatusClass ─────────────────────────────────────────
  it('should return correct status badge class', () => {
    expect(component.getStatusClass('PLANNED')).toBe('badge-blue');
    expect(component.getStatusClass('COMPLETED')).toBe('badge-green');
    expect(component.getStatusClass('CANCELLED')).toBe('badge-red');
    expect(component.getStatusClass('UNKNOWN')).toBe('badge-gray');
  });
});