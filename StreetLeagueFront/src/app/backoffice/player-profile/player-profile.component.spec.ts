import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { PlayerProfileComponent } from './player-profile.component';
import { UserService } from 'src/app/services/user.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

const mockProfile: any = {
  id: '1',
  fullName: 'John Doe',
  email: 'john@test.com',
  role: 'PLAYER',
  matchesPlayed: 10,
  wins: 6,
};

describe('PlayerProfileComponent', () => {
  let component: PlayerProfileComponent;
  let fixture: ComponentFixture<PlayerProfileComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    userServiceSpy = jasmine.createSpyObj('UserService', [
      'getProfile', 'updateProfile', 'changePassword', 'deleteAccount'
    ]);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    userServiceSpy.getProfile.and.returnValue(of(mockProfile));

    TestBed.configureTestingModule({
      declarations: [PlayerProfileComponent],
      imports: [ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router,      useValue: routerSpy      }
      ]
    });

    fixture   = TestBed.createComponent(PlayerProfileComponent);
    component = fixture.componentInstance;
    localStorage.clear();
    fixture.detectChanges(); // triggers ngOnInit
  });

  afterEach(() => localStorage.clear());

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── ngOnInit ──────────────────────────────────────────────

  it('ngOnInitTest — should call getProfile on init', () => {
    expect(userServiceSpy.getProfile).toHaveBeenCalled();
  });

  it('ngOnInitTest — should set profile from getProfile response', () => {
    expect(component.profile).toEqual(mockProfile);
  });

  it('ngOnInitTest — should patch profileForm with fullName', () => {
    expect(component.profileForm.get('fullName')?.value).toBe('John Doe');
  });

  it('ngOnInitTest — activeTab should be info by default', () => {
    expect(component.activeTab).toBe('info');
  });

  it('ngOnInitTest — showDeleteModal should be false initially', () => {
    expect(component.showDeleteModal).toBeFalse();
  });

  // ── profileForm validation ────────────────────────────────

  it('profileFormTest — should be invalid when fullName is empty', () => {
    component.profileForm.get('fullName')?.setValue('');
    expect(component.profileForm.invalid).toBeTrue();
  });

  it('profileFormTest — should be valid with a fullName', () => {
    component.profileForm.get('fullName')?.setValue('Jane Doe');
    expect(component.profileForm.valid).toBeTrue();
  });

  // ── passwordForm validation ───────────────────────────────

  it('passwordFormTest — should be invalid when empty', () => {
    expect(component.passwordForm.invalid).toBeTrue();
  });

  it('passwordFormTest — should require currentPassword', () => {
    component.passwordForm.get('currentPassword')?.setValue('');
    expect(component.passwordForm.get('currentPassword')?.hasError('required')).toBeTrue();
  });

  it('passwordFormTest — newPassword should enforce minLength 6', () => {
    component.passwordForm.get('newPassword')?.setValue('abc');
    expect(component.passwordForm.get('newPassword')?.hasError('minlength')).toBeTrue();
  });

  it('passwordFormTest — should have mismatch error when passwords differ', () => {
    component.passwordForm.setValue({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!',
      confirmPassword: 'Different!'
    });
    expect(component.passwordForm.hasError('mismatch')).toBeTrue();
  });

  it('passwordFormTest — should be valid when all fields correct and passwords match', () => {
    component.passwordForm.setValue({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    expect(component.passwordForm.valid).toBeTrue();
  });

  // ── onUpdateProfile ───────────────────────────────────────

  it('onUpdateProfileTest — should not call updateProfile when form invalid', () => {
    component.profileForm.get('fullName')?.setValue('');
    component.onUpdateProfile();
    expect(userServiceSpy.updateProfile).not.toHaveBeenCalled();
  });

  it('onUpdateProfileTest — should call updateProfile with form value', () => {
    userServiceSpy.updateProfile.and.returnValue(of(mockProfile));
    component.profileForm.get('fullName')?.setValue('Jane Doe');
    component.onUpdateProfile();
    expect(userServiceSpy.updateProfile).toHaveBeenCalledWith({ fullName: 'Jane Doe' });
  });

  it('onUpdateProfileTest — should set profileSuccess on success', () => {
    userServiceSpy.updateProfile.and.returnValue(of({ ...mockProfile, fullName: 'Jane' }));
    component.profileForm.get('fullName')?.setValue('Jane');
    component.onUpdateProfile();
    expect(component.profileSuccess).toBe('Profile updated successfully!');
  });

  it('onUpdateProfileTest — should save fullName to localStorage on success', () => {
    userServiceSpy.updateProfile.and.returnValue(of({ ...mockProfile, fullName: 'Jane' }));
    component.profileForm.get('fullName')?.setValue('Jane');
    component.onUpdateProfile();
    expect(localStorage.getItem('userName')).toBe('Jane');
  });

  it('onUpdateProfileTest — should clear profileSuccess after 3000ms', fakeAsync(() => {
    userServiceSpy.updateProfile.and.returnValue(of(mockProfile));
    component.profileForm.get('fullName')?.setValue('John Doe');
    component.onUpdateProfile();
    tick(3000);
    expect(component.profileSuccess).toBe('');
  }));

  it('onUpdateProfileTest — should set profileError on failure', () => {
    userServiceSpy.updateProfile.and.returnValue(throwError(() => ({ status: 500 })));
    component.profileForm.get('fullName')?.setValue('Jane Doe');
    component.onUpdateProfile();
    expect(component.profileError).toBe('Failed to update profile. Please try again.');
  });

  // ── onChangePassword ──────────────────────────────────────

  it('onChangePasswordTest — should not call changePassword when form invalid', () => {
    component.onChangePassword();
    expect(userServiceSpy.changePassword).not.toHaveBeenCalled();
  });

  it('onChangePasswordTest — should call changePassword with currentPassword and newPassword', () => {
    userServiceSpy.changePassword.and.returnValue(of('OK'));
    component.passwordForm.setValue({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    component.onChangePassword();
    expect(userServiceSpy.changePassword).toHaveBeenCalledWith({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!'
    });
  });

  it('onChangePasswordTest — should set passwordSuccess on success', () => {
    userServiceSpy.changePassword.and.returnValue(of('OK'));
    component.passwordForm.setValue({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    component.onChangePassword();
    expect(component.passwordSuccess).toBe('Password changed successfully!');
  });

  it('onChangePasswordTest — should reset passwordForm on success', () => {
    userServiceSpy.changePassword.and.returnValue(of('OK'));
    component.passwordForm.setValue({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    component.onChangePassword();
    expect(component.passwordForm.get('currentPassword')?.value).toBeNull();
  });

  it('onChangePasswordTest — should clear passwordSuccess after 3000ms', fakeAsync(() => {
    userServiceSpy.changePassword.and.returnValue(of('OK'));
    component.passwordForm.setValue({
      currentPassword: 'OldPass1!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    component.onChangePassword();
    tick(3000);
    expect(component.passwordSuccess).toBe('');
  }));

  it('onChangePasswordTest — should set passwordError from err.error on failure', () => {
    userServiceSpy.changePassword.and.returnValue(
      throwError(() => ({ error: 'Wrong current password.' }))
    );
    component.passwordForm.setValue({
      currentPassword: 'WrongPass!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    component.onChangePassword();
    expect(component.passwordError).toBe('Wrong current password.');
  });

  it('onChangePasswordTest — should use fallback passwordError when err.error is absent', () => {
    userServiceSpy.changePassword.and.returnValue(throwError(() => ({})));
    component.passwordForm.setValue({
      currentPassword: 'WrongPass!',
      newPassword: 'NewPass1!',
      confirmPassword: 'NewPass1!'
    });
    component.onChangePassword();
    expect(component.passwordError).toBe('Current password is incorrect.');
  });

  // ── delete modal ──────────────────────────────────────────

  it('openDeleteModalTest — should set showDeleteModal to true', () => {
    component.openDeleteModal();
    expect(component.showDeleteModal).toBeTrue();
  });

  it('closeDeleteModalTest — should set showDeleteModal to false', () => {
    component.showDeleteModal = true;
    component.closeDeleteModal();
    expect(component.showDeleteModal).toBeFalse();
  });

  it('closeDeleteModalTest — should clear deleteError', () => {
    component.deleteError = 'some error';
    component.closeDeleteModal();
    expect(component.deleteError).toBe('');
  });

  // ── confirmDelete ─────────────────────────────────────────

  it('confirmDeleteTest — should call deleteAccount', () => {
    userServiceSpy.deleteAccount.and.returnValue(of('deleted'));
    component.confirmDelete();
    expect(userServiceSpy.deleteAccount).toHaveBeenCalled();
  });

  it('confirmDeleteTest — should clear localStorage on success', () => {
    localStorage.setItem('TokenUserConnect', 'tok');
    userServiceSpy.deleteAccount.and.returnValue(of('deleted'));
    component.confirmDelete();
    expect(localStorage.getItem('TokenUserConnect')).toBeNull();
  });

  it('confirmDeleteTest — should navigate to /login on success', () => {
    userServiceSpy.deleteAccount.and.returnValue(of('deleted'));
    component.confirmDelete();
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/login']);
  });

  it('confirmDeleteTest — should set deleteError on failure', () => {
    userServiceSpy.deleteAccount.and.returnValue(throwError(() => ({ status: 500 })));
    component.confirmDelete();
    expect(component.deleteError).toBe('Failed to delete account. Please try again.');
  });

  // ── getRoleBadgeClass ─────────────────────────────────────

  it('getRoleBadgeClassTest — PLAYER returns bg-primary', () => {
    component.profile = { ...mockProfile, role: 'PLAYER' };
    expect(component.getRoleBadgeClass()).toBe('bg-primary');
  });

  it('getRoleBadgeClassTest — COACH returns bg-success', () => {
    component.profile = { ...mockProfile, role: 'COACH' };
    expect(component.getRoleBadgeClass()).toBe('bg-success');
  });

  it('getRoleBadgeClassTest — ADMIN returns bg-danger', () => {
    component.profile = { ...mockProfile, role: 'ADMIN' };
    expect(component.getRoleBadgeClass()).toBe('bg-danger');
  });

  it('getRoleBadgeClassTest — SPONSOR returns bg-warning', () => {
    component.profile = { ...mockProfile, role: 'SPONSOR' };
    expect(component.getRoleBadgeClass()).toBe('bg-warning');
  });

  it('getRoleBadgeClassTest — DELIVERY returns bg-secondary', () => {
    component.profile = { ...mockProfile, role: 'DELIVERY' };
    expect(component.getRoleBadgeClass()).toBe('bg-secondary');
  });

  it('getRoleBadgeClassTest — unknown role returns bg-dark', () => {
    component.profile = { ...mockProfile, role: 'UNKNOWN' };
    expect(component.getRoleBadgeClass()).toBe('bg-dark');
  });

  it('getRoleBadgeClassTest — null profile returns bg-dark', () => {
    component.profile = null;
    expect(component.getRoleBadgeClass()).toBe('bg-dark');
  });

  // ── getTabIndicatorLeft ───────────────────────────────────

  it('getTabIndicatorLeftTest — info tab returns 0%', () => {
    component.activeTab = 'info';
    expect(component.getTabIndicatorLeft()).toBe('0%');
  });

  it('getTabIndicatorLeftTest — password tab returns 33.33%', () => {
    component.activeTab = 'password';
    expect(component.getTabIndicatorLeft()).toBe('33.33%');
  });

  it('getTabIndicatorLeftTest — stats tab returns 66.66%', () => {
    component.activeTab = 'stats';
    expect(component.getTabIndicatorLeft()).toBe('66.66%');
  });

  // ── clampStat ─────────────────────────────────────────────

  it('clampStatTest — zero val returns 4 (min)', () => {
    expect(component.clampStat(0, 100)).toBe(4);
  });

  it('clampStatTest — negative val returns 4 (min)', () => {
    expect(component.clampStat(-5, 100)).toBe(4);
  });

  it('clampStatTest — value equal to max returns 100', () => {
    expect(component.clampStat(100, 100)).toBe(100);
  });

  it('clampStatTest — value exceeding max is clamped to 100', () => {
    expect(component.clampStat(150, 100)).toBe(100);
  });

  it('clampStatTest — mid-range value returns correct percentage', () => {
    expect(component.clampStat(50, 100)).toBe(50);
  });

  // ── getPasswordStrength ───────────────────────────────────

  it('getPasswordStrengthTest — empty password returns 0', () => {
    component.passwordForm.get('newPassword')?.setValue('');
    expect(component.getPasswordStrength()).toBe(0);
  });

  it('getPasswordStrengthTest — short lowercase returns 1', () => {
    component.passwordForm.get('newPassword')?.setValue('abcdef');
    expect(component.getPasswordStrength()).toBe(1);
  });

  it('getPasswordStrengthTest — long uppercase+digit returns 4', () => {
    component.passwordForm.get('newPassword')?.setValue('Abcdefgh12');
    expect(component.getPasswordStrength()).toBe(4);
  });

  it('getPasswordStrengthTest — full strong password returns 5', () => {
    component.passwordForm.get('newPassword')?.setValue('Abcdefgh1!');
    expect(component.getPasswordStrength()).toBe(5);
  });

  // ── getStrengthLabel ──────────────────────────────────────

  it('getStrengthLabelTest — score 0 returns Weak', () => {
    component.passwordForm.get('newPassword')?.setValue('');
    expect(component.getStrengthLabel()).toBe('Weak');
  });

  it('getStrengthLabelTest — score 2 returns Fair', () => {
    component.passwordForm.get('newPassword')?.setValue('abcdefghij'); // length>=6 + length>=10 = 2
    expect(component.getStrengthLabel()).toBe('Fair');
  });

  it('getStrengthLabelTest — score 5 returns Strong', () => {
    component.passwordForm.get('newPassword')?.setValue('Abcdefgh1!');
    expect(component.getStrengthLabel()).toBe('Strong');
  });

  // ── getStrengthClass ──────────────────────────────────────

  it('getStrengthClassTest — score 0 returns weak', () => {
    component.passwordForm.get('newPassword')?.setValue('');
    expect(component.getStrengthClass()).toBe('weak');
  });

  it('getStrengthClassTest — score 5 returns strong', () => {
    component.passwordForm.get('newPassword')?.setValue('Abcdefgh1!');
    expect(component.getStrengthClass()).toBe('strong');
  });

  // ── getStrengthWidth ──────────────────────────────────────

  it('getStrengthWidthTest — score 0 returns 0%', () => {
    component.passwordForm.get('newPassword')?.setValue('');
    expect(component.getStrengthWidth()).toBe('0%');
  });

  it('getStrengthWidthTest — score 5 returns 100%', () => {
    component.passwordForm.get('newPassword')?.setValue('Abcdefgh1!');
    expect(component.getStrengthWidth()).toBe('100%');
  });
});