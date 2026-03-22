import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service  = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // ── create ────────────────────────────────────────────────

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ── register ──────────────────────────────────────────────

  it('registerTest — should call POST /auth/register', () => {
    const req = {
      fullName: 'John Doe', email: 'john@test.com',
      password: 'Pass123!', role: 'PLAYER' as any
    };
    service.register(req).subscribe(res => {
      expect(res).toBe('success');
    });

    const httpReq = httpMock.expectOne(
      'http://localhost:8086/StreetLeague/auth/register'
    );
    expect(httpReq.request.method).toBe('POST');
    expect(httpReq.request.body).toEqual(req);
    httpReq.flush('success');
  });

  // ── login ─────────────────────────────────────────────────

  it('loginTest — should call POST /auth/login', () => {
    const req = { email: 'john@test.com', password: 'Pass123!' };
    const mockResponse = {
      token: 'mytoken', email: 'john@test.com',
      role: 'PLAYER', id: '1'
    };

    service.login(req).subscribe(res => {
      expect(res.token).toBe('mytoken');
      expect(res.email).toBe('john@test.com');
    });

    const httpReq = httpMock.expectOne(
      'http://localhost:8086/StreetLeague/auth/login'
    );
    expect(httpReq.request.method).toBe('POST');
    httpReq.flush(mockResponse);
  });

  it('loginTest — should save token to localStorage', () => {
    const req = { email: 'john@test.com', password: 'Pass123!' };
    const mockResponse = {
      token: 'mytoken', email: 'john@test.com',
      role: 'PLAYER', id: '42'
    };

    service.login(req).subscribe();

    httpMock.expectOne(
      'http://localhost:8086/StreetLeague/auth/login'
    ).flush(mockResponse);

    expect(localStorage.getItem('TokenUserConnect')).toBe('mytoken');
    expect(localStorage.getItem('EmailUserConnect')).toBe('john@test.com');
    expect(localStorage.getItem('RoleUserConnect')).toBe('PLAYER');
    expect(localStorage.getItem('UserIdConnect')).toBe('42');
  });

  // ── logout ────────────────────────────────────────────────

  it('logoutTest — should clear localStorage', () => {
    localStorage.setItem('TokenUserConnect', 'mytoken');
    localStorage.setItem('EmailUserConnect', 'john@test.com');
    localStorage.setItem('RoleUserConnect',  'PLAYER');
    localStorage.setItem('UserIdConnect',    '42');

    service.logout();

    expect(localStorage.getItem('TokenUserConnect')).toBeNull();
    expect(localStorage.getItem('EmailUserConnect')).toBeNull();
    expect(localStorage.getItem('RoleUserConnect')).toBeNull();
    expect(localStorage.getItem('UserIdConnect')).toBeNull();
  });

  // ── isLoggedIn ────────────────────────────────────────────

  it('isLoggedInTest — should return true when token exists', () => {
    localStorage.setItem('TokenUserConnect', 'mytoken');
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('isLoggedInTest — should return false when no token', () => {
    localStorage.removeItem('TokenUserConnect');
    expect(service.isLoggedIn()).toBeFalse();
  });

  // ── getRole ───────────────────────────────────────────────

  it('getRoleTest — should return role from localStorage', () => {
    localStorage.setItem('RoleUserConnect', 'ADMIN');
    expect(service.getRole()).toBe('ADMIN');
  });

  it('getRoleTest — should return null when no role', () => {
    localStorage.removeItem('RoleUserConnect');
    expect(service.getRole()).toBeNull();
  });

  // ── getToken ──────────────────────────────────────────────

  it('getTokenTest — should return token from localStorage', () => {
    localStorage.setItem('TokenUserConnect', 'mytoken');
    expect(service.getToken()).toBe('mytoken');
  });

  it('getTokenTest — should return null when no token', () => {
    localStorage.removeItem('TokenUserConnect');
    expect(service.getToken()).toBeNull();
  });
});