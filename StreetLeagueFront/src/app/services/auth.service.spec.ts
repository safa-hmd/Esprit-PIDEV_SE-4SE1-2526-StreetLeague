import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService, AuthResponse, LoginRequest, RegisterRequest } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const BASE = 'http://localhost:8086/StreetLeague/auth';

  // ── Données fictives réutilisables ──────────────────────
  const fakeAuthResponse: AuthResponse = {
    id: 42,
    token: 'fake-jwt-token',
    email: 'ahmed@test.com',
    role: 'DELIVERY'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    // Nettoyer localStorage avant chaque test
    localStorage.clear();
  });

  afterEach(() => {
    // Vérifier qu'il n'y a pas de requêtes HTTP en attente
    httpMock.verify();
    localStorage.clear();
  });

  // ════════════════════════════════════════════════════════
  // 1. Création du service
  // ════════════════════════════════════════════════════════
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ════════════════════════════════════════════════════════
  // 2. login()
  // ════════════════════════════════════════════════════════
  describe('login()', () => {

    it('should call POST /auth/login with credentials', () => {
      const req: LoginRequest = { email: 'ahmed@test.com', password: '123456' };

      service.login(req).subscribe();

      const httpReq = httpMock.expectOne(`${BASE}/login`);
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush(fakeAuthResponse);
    });

    it('should save token in localStorage after login', () => {
      const req: LoginRequest = { email: 'ahmed@test.com', password: '123456' };

      service.login(req).subscribe();

      httpMock.expectOne(`${BASE}/login`).flush(fakeAuthResponse);

      expect(localStorage.getItem('TokenUserConnect')).toBe('fake-jwt-token');
    });

    it('should save email in localStorage after login', () => {
      service.login({ email: 'ahmed@test.com', password: '123456' }).subscribe();
      httpMock.expectOne(`${BASE}/login`).flush(fakeAuthResponse);
      expect(localStorage.getItem('EmailUserConnect')).toBe('ahmed@test.com');
    });

    it('should save role in localStorage after login', () => {
      service.login({ email: 'ahmed@test.com', password: '123456' }).subscribe();
      httpMock.expectOne(`${BASE}/login`).flush(fakeAuthResponse);
      expect(localStorage.getItem('RoleUserConnect')).toBe('DELIVERY');
    });

    it('should save id in localStorage after login', () => {
      service.login({ email: 'ahmed@test.com', password: '123456' }).subscribe();
      httpMock.expectOne(`${BASE}/login`).flush(fakeAuthResponse);
      expect(localStorage.getItem('IdUserConnect')).toBe('42');
    });

    it('should return the AuthResponse observable', () => {
      let result: AuthResponse | undefined;
      service.login({ email: 'ahmed@test.com', password: '123456' })
        .subscribe(r => result = r);

      httpMock.expectOne(`${BASE}/login`).flush(fakeAuthResponse);
      expect(result).toEqual(fakeAuthResponse);
    });
  });

  // ════════════════════════════════════════════════════════
  // 3. logout()
  // ════════════════════════════════════════════════════════
  describe('logout()', () => {

    beforeEach(() => {
      // Simuler un utilisateur connecté
      localStorage.setItem('TokenUserConnect', 'fake-jwt-token');
      localStorage.setItem('EmailUserConnect', 'ahmed@test.com');
      localStorage.setItem('RoleUserConnect', 'DELIVERY');
      localStorage.setItem('IdUserConnect', '42');
    });

    it('should remove token from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('TokenUserConnect')).toBeNull();
    });

    it('should remove email from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('EmailUserConnect')).toBeNull();
    });

    it('should remove role from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('RoleUserConnect')).toBeNull();
    });

    it('should remove id from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('IdUserConnect')).toBeNull();
    });
  });

  // ════════════════════════════════════════════════════════
  // 4. isLoggedIn()
  // ════════════════════════════════════════════════════════
  describe('isLoggedIn()', () => {

    it('should return true when token exists', () => {
      localStorage.setItem('TokenUserConnect', 'fake-jwt-token');
      expect(service.isLoggedIn()).toBeTrue();
    });

    it('should return false when no token', () => {
      expect(service.isLoggedIn()).toBeFalse();
    });
  });

  // ════════════════════════════════════════════════════════
  // 5. getRole()
  // ════════════════════════════════════════════════════════
  describe('getRole()', () => {

    it('should return role from localStorage', () => {
      localStorage.setItem('RoleUserConnect', 'ADMIN');
      expect(service.getRole()).toBe('ADMIN');
    });

    it('should return null when no role', () => {
      expect(service.getRole()).toBeNull();
    });
  });

  // ════════════════════════════════════════════════════════
  // 6. getUserId()
  // ════════════════════════════════════════════════════════
  describe('getUserId()', () => {

    it('should return numeric id from localStorage', () => {
      localStorage.setItem('IdUserConnect', '42');
      expect(service.getUserId()).toBe(42);
    });

    it('should return null when no id stored', () => {
      expect(service.getUserId()).toBeNull();
    });
  });

  // ════════════════════════════════════════════════════════
  // 7. getEmail()
  // ════════════════════════════════════════════════════════
  describe('getEmail()', () => {

    it('should return email from localStorage', () => {
      localStorage.setItem('EmailUserConnect', 'ahmed@test.com');
      expect(service.getEmail()).toBe('ahmed@test.com');
    });

    it('should return null when no email', () => {
      expect(service.getEmail()).toBeNull();
    });
  });

  // ════════════════════════════════════════════════════════
  // 8. register()
  // ════════════════════════════════════════════════════════
  describe('register()', () => {

    it('should call POST /auth/register', () => {
      const req: RegisterRequest = {
        fullName: 'Ahmed Ben Ali',
        email: 'ahmed@test.com',
        password: '123456',
        role: 'DELIVERY'
      };

      service.register(req).subscribe();

      const httpReq = httpMock.expectOne(`${BASE}/register`);
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush('User created: ahmed@test.com');
    });
  });

  // ════════════════════════════════════════════════════════
  // 9. getToken()
  // ════════════════════════════════════════════════════════
  describe('getToken()', () => {

    it('should return token from localStorage', () => {
      localStorage.setItem('TokenUserConnect', 'fake-jwt-token');
      expect(service.getToken()).toBe('fake-jwt-token');
    });

    it('should return null when no token', () => {
      expect(service.getToken()).toBeNull();
    });
  });

});