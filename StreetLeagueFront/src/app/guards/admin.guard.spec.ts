import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AdminGuard } from './admin.guard';
import { AuthService } from '../services/auth.service';

describe('AdminGuard', () => {
  let guard: AdminGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole']);
    routerSpy      = jasmine.createSpyObj('Router', ['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        AdminGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router,      useValue: routerSpy      },
      ]
    });

    guard = TestBed.inject(AdminGuard);
  });

  it('should create', () => {
    expect(guard).toBeTruthy();
  });

  it('canActivateTest - should return true when role is ROLE_ADMIN', () => {
    authServiceSpy.getRole.and.returnValue('ROLE_ADMIN');

    const result = guard.canActivate();

    expect(result).toBeTrue();
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });

  it('canActivateTest - should return false and redirect when role is ROLE_PLAYER', () => {
    authServiceSpy.getRole.and.returnValue('ROLE_PLAYER');

    const result = guard.canActivate();

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/admin-login');
  });

  it('canActivateTest - should return false and redirect when role is ROLE_COACH', () => {
    authServiceSpy.getRole.and.returnValue('ROLE_COACH');

    const result = guard.canActivate();

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/admin-login');
  });

  it('canActivateTest - should return false and redirect when role is null', () => {
    authServiceSpy.getRole.and.returnValue(null as any);

    const result = guard.canActivate();

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/admin-login');
  });
});