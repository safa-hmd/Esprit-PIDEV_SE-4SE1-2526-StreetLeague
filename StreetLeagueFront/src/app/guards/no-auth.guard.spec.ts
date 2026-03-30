import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { NoAuthGuard } from './no-auth.guard';
import { AuthService } from '../services/auth.service';

describe('NoAuthGuard', () => {
  let guard: NoAuthGuard;
  let routerSpy: jasmine.SpyObj<Router>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const dummyRoute = {} as ActivatedRouteSnapshot;
  const state = (url: string): RouterStateSnapshot => ({ url } as RouterStateSnapshot);

  beforeEach(() => {
    routerSpy      = jasmine.createSpyObj('Router',      ['navigateByUrl']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole', 'normalizeRole', 'logout']);

    authServiceSpy.getRole.and.callFake(() => localStorage.getItem('RoleUserConnect'));
    authServiceSpy.normalizeRole.and.callFake((role: string | null) => {
      if (!role) return null;
      return role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    });

    TestBed.configureTestingModule({
      providers: [
        NoAuthGuard,
        { provide: Router,      useValue: routerSpy      },
        { provide: AuthService, useValue: authServiceSpy },
      ]
    });

    guard = TestBed.inject(NoAuthGuard);

    localStorage.removeItem('TokenUserConnect');
    localStorage.removeItem('RoleUserConnect');
  });

  afterEach(() => {
    localStorage.removeItem('TokenUserConnect');
    localStorage.removeItem('RoleUserConnect');
  });

  it('should create', () => {
    expect(guard).toBeTruthy();
  });

  it('canActivateTest - should return true when no token (not logged in)', () => {
    const result = guard.canActivate(dummyRoute, state('/'));

    expect(result).toBeTrue();
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });

  it('canActivateTest - should allow /login even when token and ROLE_ADMIN', () => {
    localStorage.setItem('TokenUserConnect', 'fake_token');
    localStorage.setItem('RoleUserConnect', 'ROLE_ADMIN');

    const result = guard.canActivate(dummyRoute, state('/login'));

    expect(result).toBeTrue();
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });

  it('canActivateTest - should return false and redirect to /admin when role is ROLE_ADMIN', () => {
    localStorage.setItem('TokenUserConnect', 'fake_token');
    localStorage.setItem('RoleUserConnect',  'ROLE_ADMIN');

    const result = guard.canActivate(dummyRoute, state('/'));

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/admin');
  });

  it('canActivateTest - should return false and redirect to /coach when role is ROLE_COACH', () => {
    localStorage.setItem('TokenUserConnect', 'fake_token');
    localStorage.setItem('RoleUserConnect',  'ROLE_COACH');

    const result = guard.canActivate(dummyRoute, state('/'));

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/coach');
  });

  it('canActivateTest - should return false and redirect to /client when role is ROLE_PLAYER', () => {
    localStorage.setItem('TokenUserConnect', 'fake_token');
    localStorage.setItem('RoleUserConnect',  'ROLE_PLAYER');

    const result = guard.canActivate(dummyRoute, state('/'));

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/client');
  });

  it('canActivateTest - should logout and return true when role is unknown', () => {
    localStorage.setItem('TokenUserConnect', 'fake_token');
    localStorage.setItem('RoleUserConnect',  'ROLE_UNKNOWN');

    const result = guard.canActivate(dummyRoute, state('/'));

    expect(result).toBeTrue();
    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });
});