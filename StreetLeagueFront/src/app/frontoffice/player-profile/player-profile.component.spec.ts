import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { PlayerProfileComponent } from './player-profile.component';
import { UserService }     from 'src/app/services/user.service';
import { TeamService }     from 'src/app/services/team.service';
import { MatchService }    from 'src/app/services/match.service';
import { TrainingService } from 'src/app/services/training.service';
import { UserProfile }     from 'src/app/models/user.model';
import { HttpClientTestingModule } from '@angular/common/http/testing';

// ── Mock data ─────────────────────────────────────────────────────────────────

const EMAIL = 'coach@test.com';

const mockProfile: UserProfile = {
  idUser:        1,
  fullName:      'Alice Martin',
  email:         EMAIL,
  role:          'COACH',
  teamCount:     0,
  matchCount:    0,
  trainingCount: 0
};

const mockTeams = [
  { idTeam: 1, name: 'Team Alpha', captainEmail: EMAIL,             captainFullName: 'Alice Martin' },
  { idTeam: 2, name: 'Team Beta',  captainEmail: 'other@test.com', captainFullName: 'Bob'          }
];

const mockMatches = [
  { idMatch: 10, teamAName: 'Team Alpha', teamBName: 'Team Beta',
    captainAEmail: EMAIL,          captainBEmail: 'other@test.com' },
  { idMatch: 11, teamAName: 'Other A',    teamBName: 'Other B',
    captainAEmail: 'x@x.com',     captainBEmail: 'y@y.com'        }
];

const mockTrainings = [
  { idTraining: 100, title: 'Sprint Drills', teamName: 'Team Alpha', status: 'PLANNED'   },
  { idTraining: 101, title: 'Strength Work', teamName: 'Other Team',  status: 'COMPLETED' }
];

// ── Suite ─────────────────────────────────────────────────────────────────────

describe('PlayerProfileComponent', () => {
  let component:   PlayerProfileComponent;
  let fixture:     ComponentFixture<PlayerProfileComponent>;

  let userSvc:     jasmine.SpyObj<UserService>;
  let teamSvc:     jasmine.SpyObj<TeamService>;
  let matchSvc:    jasmine.SpyObj<MatchService>;
  let trainingSvc: jasmine.SpyObj<TrainingService>;
  let routerSpy:   jasmine.SpyObj<Router>;

  beforeEach(async () => {
    userSvc     = jasmine.createSpyObj('UserService',     ['getProfile', 'updateProfile', 'changePassword', 'deleteAccount']);
    teamSvc     = jasmine.createSpyObj('TeamService',     ['getAllTeams']);
    matchSvc    = jasmine.createSpyObj('MatchService',    ['getAllMatchs']);
    trainingSvc = jasmine.createSpyObj('TrainingService', ['getAllTrainings']);
    routerSpy   = jasmine.createSpyObj('Router',          ['navigate']);

    // Default happy-path stubs
    userSvc.getProfile.and.returnValue(of(mockProfile));
    teamSvc.getAllTeams.and.returnValue(of(mockTeams as any));
    matchSvc.getAllMatchs.and.returnValue(of(mockMatches as any));
    trainingSvc.getAllTrainings.and.returnValue(of(mockTrainings as any));

    localStorage.setItem('EmailUserConnect', EMAIL);

    await TestBed.configureTestingModule({
      imports:      [ReactiveFormsModule,
                     HttpClientTestingModule
      ],
      declarations: [PlayerProfileComponent],
      providers: [
        FormBuilder,
        { provide: UserService,     useValue: userSvc     },
        { provide: TeamService,     useValue: teamSvc     },
        { provide: MatchService,    useValue: matchSvc    },
        { provide: TrainingService, useValue: trainingSvc },
        { provide: Router,          useValue: routerSpy   }
      ]
    }).compileComponents();

    fixture   = TestBed.createComponent(PlayerProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => localStorage.clear());

  // ── Creation ───────────────────────────────────────────────────────────────

  describe('Creation', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should call all four services on init via forkJoin', () => {
      expect(userSvc.getProfile).toHaveBeenCalledTimes(1);
      expect(teamSvc.getAllTeams).toHaveBeenCalledTimes(1);
      expect(matchSvc.getAllMatchs).toHaveBeenCalledTimes(1);
      expect(trainingSvc.getAllTrainings).toHaveBeenCalledTimes(1);
    });

    it('should patch profileForm with the loaded fullName', () => {
      expect(component.profileForm.get('fullName')?.value).toBe('Alice Martin');
    });

    it('should default activeTab to "info"', () => {
      expect(component.activeTab).toBe('info');
    });

    it('should default showDeleteModal to false', () => {
      expect(component.showDeleteModal).toBeFalse();
    });
  });

  // ── forkJoin data filtering ────────────────────────────────────────────────

  describe('loadData filteringTest', () => {
    it('should count only teams where captainEmail matches', () => {
      // mockTeams[0].captainEmail === EMAIL → 1 team
      expect(component.profile?.teamCount).toBe(1);
    });

    it('should count matches involving my teams or my email', () => {
      // mockMatches[0] involves Team Alpha → 1 match
      expect(component.profile?.matchCount).toBe(1);
    });

    it('should count trainings linked to my team names', () => {
      // mockTrainings[0].teamName === 'Team Alpha' → 1 training
      expect(component.profile?.trainingCount).toBe(1);
    });

    it('should fall back to getProfile when forkJoin fails', () => {
      userSvc.getProfile.calls.reset();
      teamSvc.getAllTeams.and.returnValue(throwError(() => new Error('fail')));
      userSvc.getProfile.and.returnValue(of(mockProfile));

      component['loadData']();

      expect(userSvc.getProfile).toHaveBeenCalledTimes(1);
    });

    it('should set profile from fallback getProfile on forkJoin error', () => {
      teamSvc.getAllTeams.and.returnValue(throwError(() => new Error('fail')));
      userSvc.getProfile.and.returnValue(of(mockProfile));

      component['loadData']();

      expect(component.profile?.fullName).toBe('Alice Martin');
    });
  });

  // ── profileForm ────────────────────────────────────────────────────────────

  describe('profileForm validationTest', () => {
    it('should be invalid when fullName is empty', () => {
      component.profileForm.setValue({ fullName: '' });
      expect(component.profileForm.invalid).toBeTrue();
    });

    it('should be valid when fullName has a value', () => {
      component.profileForm.setValue({ fullName: 'Bob' });
      expect(component.profileForm.valid).toBeTrue();
    });
  });

  // ── passwordForm ───────────────────────────────────────────────────────────

  describe('passwordForm validationTest', () => {
    it('should be invalid when empty', () => {
      expect(component.passwordForm.invalid).toBeTrue();
    });

    it('should have mismatch error when passwords differ', () => {
      component.passwordForm.setValue({
        currentPassword: 'old123',
        newPassword:     'newPass1',
        confirmPassword: 'different'
      });
      expect(component.passwordForm.errors?.['mismatch']).toBeTrue();
    });

    it('should be valid when all fields match', () => {
      component.passwordForm.setValue({
        currentPassword: 'old123',
        newPassword:     'newPass1',
        confirmPassword: 'newPass1'
      });
      expect(component.passwordForm.valid).toBeTrue();
    });

    it('should be invalid when newPassword is shorter than 6 chars', () => {
      component.passwordForm.setValue({
        currentPassword: 'old',
        newPassword:     'abc',
        confirmPassword: 'abc'
      });
      expect(component.passwordForm.get('newPassword')?.errors?.['minlength']).toBeTruthy();
    });
  });

  // ── onUpdateProfile ────────────────────────────────────────────────────────

  describe('onUpdateProfileTest', () => {
    it('should not call updateProfile when form is invalid', () => {
      component.profileForm.setValue({ fullName: '' });
      component.onUpdateProfile();
      expect(userSvc.updateProfile).not.toHaveBeenCalled();
    });

    it('should not call updateProfile when profile is null', () => {
      component.profile = null;
      component.profileForm.setValue({ fullName: 'Bob' });
      component.onUpdateProfile();
      expect(userSvc.updateProfile).not.toHaveBeenCalled();
    });

    it('should call updateProfile and show profileSuccess on success', fakeAsync(() => {
      const updated = { ...mockProfile, fullName: 'Bob Smith' };
      userSvc.updateProfile.and.returnValue(of(updated));

      component.profileForm.setValue({ fullName: 'Bob Smith' });
      component.onUpdateProfile();

      expect(component.profileSuccess).toBe('Profile updated successfully!');
      tick(3001);
      expect(component.profileSuccess).toBe('');
    }));

    it('should preserve existing teamCount/matchCount/trainingCount after update', () => {
      component.profile = { ...mockProfile, teamCount: 3, matchCount: 5, trainingCount: 2 };
      const updated     = { ...mockProfile, fullName: 'Bob Smith', teamCount: 0, matchCount: 0, trainingCount: 0 };
      userSvc.updateProfile.and.returnValue(of(updated));

      component.profileForm.setValue({ fullName: 'Bob Smith' });
      component.onUpdateProfile();

      expect(component.profile?.teamCount).toBe(3);
      expect(component.profile?.matchCount).toBe(5);
      expect(component.profile?.trainingCount).toBe(2);
    });

    it('should store updated fullName in localStorage on success', () => {
      const updated = { ...mockProfile, fullName: 'Charlie' };
      userSvc.updateProfile.and.returnValue(of(updated));
      component.profileForm.setValue({ fullName: 'Charlie' });
      component.onUpdateProfile();
      expect(localStorage.getItem('userName')).toBe('Charlie');
    });

    it('should set profileError on failure', () => {
      userSvc.updateProfile.and.returnValue(throwError(() => new Error('err')));
      component.profileForm.setValue({ fullName: 'Bob' });
      component.onUpdateProfile();
      expect(component.profileError).toBe('Failed to update profile.');
    });
  });

  // ── onChangePassword ───────────────────────────────────────────────────────

  describe('onChangePasswordTest', () => {
    it('should not call changePassword when form is invalid', () => {
      component.onChangePassword();
      expect(userSvc.changePassword).not.toHaveBeenCalled();
    });

    it('should call changePassword and show passwordSuccess on success', fakeAsync(() => {
      userSvc.changePassword.and.returnValue(of('ok'));
      component.passwordForm.setValue({
        currentPassword: 'old123',
        newPassword:     'newPass1',
        confirmPassword: 'newPass1'
      });
      component.onChangePassword();

      expect(component.passwordSuccess).toBe('Password changed successfully!');
      tick(3001);
      expect(component.passwordSuccess).toBe('');
    }));

    it('should reset passwordForm after successful change', () => {
      userSvc.changePassword.and.returnValue(of('ok'));
      component.passwordForm.setValue({
        currentPassword: 'old123',
        newPassword:     'newPass1',
        confirmPassword: 'newPass1'
      });
      component.onChangePassword();
      expect(component.passwordForm.get('currentPassword')?.value).toBeNull();
    });

    it('should set passwordError from err.error on failure', () => {
      userSvc.changePassword.and.returnValue(
        throwError(() => ({ error: 'Wrong password' }))
      );
      component.passwordForm.setValue({
        currentPassword: 'bad',
        newPassword:     'newPass1',
        confirmPassword: 'newPass1'
      });
      component.onChangePassword();
      expect(component.passwordError).toBe('Wrong password');
    });

    it('should use default passwordError message when err.error is falsy', () => {
      userSvc.changePassword.and.returnValue(throwError(() => ({})));
      component.passwordForm.setValue({
        currentPassword: 'bad',
        newPassword:     'newPass1',
        confirmPassword: 'newPass1'
      });
      component.onChangePassword();
      expect(component.passwordError).toBe('Current password is incorrect.');
    });
  });

  // ── Delete account modal ───────────────────────────────────────────────────

  describe('deleteModalTest', () => {
    it('should open the modal', () => {
      component.openDeleteModal();
      expect(component.showDeleteModal).toBeTrue();
    });

    it('should close the modal and clear deleteError', () => {
      component.showDeleteModal = true;
      component.deleteError     = 'some error';
      component.closeDeleteModal();
      expect(component.showDeleteModal).toBeFalse();
      expect(component.deleteError).toBe('');
    });

    it('should navigate to /login and clear localStorage on confirmDelete', () => {
      userSvc.deleteAccount.and.returnValue(of('deleted'));
      localStorage.setItem('token', 'abc');
      component.confirmDelete();
      expect(localStorage.getItem('token')).toBeNull();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should set deleteError on confirmDelete failure', () => {
      userSvc.deleteAccount.and.returnValue(throwError(() => new Error('fail')));
      component.confirmDelete();
      expect(component.deleteError).toBe('Failed to delete account.');
    });
  });

  // ── Helper methods ─────────────────────────────────────────────────────────

  describe('getRoleBadgeClassTest', () => {
    it('should return bg-primary for PLAYER',    () => { component.profile = { ...mockProfile, role: 'PLAYER'   }; expect(component.getRoleBadgeClass()).toBe('bg-primary');   });
    it('should return bg-success for COACH',     () => { component.profile = { ...mockProfile, role: 'COACH'    }; expect(component.getRoleBadgeClass()).toBe('bg-success');   });
    it('should return bg-danger for ADMIN',      () => { component.profile = { ...mockProfile, role: 'ADMIN'    }; expect(component.getRoleBadgeClass()).toBe('bg-danger');    });
    it('should return bg-warning for SPONSOR',   () => { component.profile = { ...mockProfile, role: 'SPONSOR'  }; expect(component.getRoleBadgeClass()).toBe('bg-warning');   });
    it('should return bg-secondary for DELIVERY',() => { component.profile = { ...mockProfile, role: 'DELIVERY' }; expect(component.getRoleBadgeClass()).toBe('bg-secondary'); });
    it('should return bg-dark when profile is null', () => { component.profile = null; expect(component.getRoleBadgeClass()).toBe('bg-dark'); });
  });

  describe('getTabIndicatorLeftTest', () => {
    it('should return 0% for info tab',      () => { component.activeTab = 'info';     expect(component.getTabIndicatorLeft()).toBe('0%');      });
    it('should return 33.33% for password',  () => { component.activeTab = 'password'; expect(component.getTabIndicatorLeft()).toBe('33.33%');   });
    it('should return 66.66% for stats tab', () => { component.activeTab = 'stats';    expect(component.getTabIndicatorLeft()).toBe('66.66%');   });
  });

  describe('clampStatTest', () => {
    it('should return 4 for value 0',         () => expect(component.clampStat(0,   100)).toBe(4));
    it('should return 4 for negative value',  () => expect(component.clampStat(-5,  100)).toBe(4));
    it('should clamp to 100 when over max',   () => expect(component.clampStat(200, 100)).toBe(100));
    it('should compute correct percentage',   () => expect(component.clampStat(50,  100)).toBe(50));
  });

  describe('getPasswordStrengthTest', () => {
    it('should return 0 for empty password', () => {
      component.passwordForm.get('newPassword')?.setValue('');
      expect(component.getPasswordStrength()).toBe(0);
    });

    it('should return >= 1 for password >= 6 chars', () => {
      component.passwordForm.get('newPassword')?.setValue('abcdef');
      expect(component.getPasswordStrength()).toBeGreaterThanOrEqual(1);
    });

    it('should return 5 for a very strong password', () => {
      component.passwordForm.get('newPassword')?.setValue('Str0ng!Pass');
      expect(component.getPasswordStrength()).toBe(5);
    });

    it('should return Weak label for score <= 1', () => {
      component.passwordForm.get('newPassword')?.setValue('abc');
      expect(component.getStrengthLabel()).toBe('Weak');
    });

    it('should return Strong label for score > 3', () => {
      component.passwordForm.get('newPassword')?.setValue('Str0ng!Pass');
      expect(component.getStrengthLabel()).toBe('Strong');
    });

    it('should return "weak" class for short password', () => {
      component.passwordForm.get('newPassword')?.setValue('abc');
      expect(component.getStrengthClass()).toBe('weak');
    });

    it('should return "strong" class for strong password', () => {
      component.passwordForm.get('newPassword')?.setValue('Str0ng!Pass');
      expect(component.getStrengthClass()).toBe('strong');
    });

    it('should return "0%" width for empty password', () => {
      component.passwordForm.get('newPassword')?.setValue('');
      expect(component.getStrengthWidth()).toBe('0%');
    });

    it('should return "100%" width for max strength password', () => {
      component.passwordForm.get('newPassword')?.setValue('Str0ng!Pass');
      expect(component.getStrengthWidth()).toBe('100%');
    });
  });
});