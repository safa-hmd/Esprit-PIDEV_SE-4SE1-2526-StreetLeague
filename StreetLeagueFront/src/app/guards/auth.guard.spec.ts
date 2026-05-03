import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import 'jasmine';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getRole']);
    routerSpy      = jasmine.createSpyObj('Router', ['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router,      useValue: routerSpy      },
      ]
    });

    guard = TestBed.inject(AuthGuard);

    // Nettoyer localStorage avant chaque test
    localStorage.removeItem('TokenUserConnect');
  });

  afterEach(() => {
    localStorage.removeItem('TokenUserConnect');
  });

  it('should create', () => {
    expect(guard).toBeTruthy();
  });

  it('canActivateTest - should return true when token exists', () => {
    localStorage.setItem('TokenUserConnect', 'fake_jwt_token');

    const result = guard.canActivate();

    expect(result).toBeTrue();
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });

  it('canActivateTest - should return false and redirect to /login when no token', () => {
    const result = guard.canActivate();

    expect(result).toBeFalse();
    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});