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
    routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        AdminGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });
    guard = TestBed.inject(AdminGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow activation for ROLE_ADMIN', () => {
    authServiceSpy.getRole.and.returnValue('ROLE_ADMIN');
    expect(guard.canActivate()).toBeTrue();
  });

  it('should redirect for non-admin users', () => {
    authServiceSpy.getRole.and.returnValue('ROLE_USER');
    expect(guard.canActivate()).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/admin-login');
  });
});
