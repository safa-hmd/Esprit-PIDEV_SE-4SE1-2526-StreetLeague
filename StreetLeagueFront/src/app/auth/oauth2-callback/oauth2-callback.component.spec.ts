import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';

import { OAuth2CallbackComponent } from './oauth2-callback.component';

describe('OAuth2CallbackComponent', () => {
  let component: OAuth2CallbackComponent;
  let fixture: ComponentFixture<OAuth2CallbackComponent>;
  let routerSpy: jasmine.SpyObj<Router>;
  let queryParamsSubject: Subject<any>;

  beforeEach(() => {
    routerSpy          = jasmine.createSpyObj('Router', ['navigateByUrl']);
    queryParamsSubject = new Subject<any>();

    TestBed.configureTestingModule({
      declarations: [OAuth2CallbackComponent],
      providers: [
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: { queryParams: queryParamsSubject.asObservable() }
        }
      ]
    });

    fixture   = TestBed.createComponent(OAuth2CallbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── no token ──────────────────────────────────────────────

  it('noTokenTest — should redirect to /login when token is missing', () => {
    queryParamsSubject.next({});
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/login');
  });

  it('noTokenTest — should NOT save anything to localStorage when token is missing', () => {
    queryParamsSubject.next({ email: 'john@test.com', role: 'PLAYER', id: '1' });
    expect(localStorage.getItem('TokenUserConnect')).toBeNull();
    expect(localStorage.getItem('EmailUserConnect')).toBeNull();
    expect(localStorage.getItem('RoleUserConnect')).toBeNull();
    expect(localStorage.getItem('UserIdConnect')).toBeNull();
  });

  // ── localStorage ─────────────────────────────────────────

  it('localStorageTest — should save all params to localStorage when token is present', () => {
    queryParamsSubject.next({
      token: 'my-token',
      email: 'john@test.com',
      role:  'ROLE_ADMIN',
      id:    '42'
    });

    expect(localStorage.getItem('TokenUserConnect')).toBe('my-token');
    expect(localStorage.getItem('EmailUserConnect')).toBe('john@test.com');
    expect(localStorage.getItem('RoleUserConnect')).toBe('ROLE_ADMIN');
    expect(localStorage.getItem('UserIdConnect')).toBe('42');
  });

  // ── redirectByRole ────────────────────────────────────────

  it('redirectTest — should navigate to /admin for ROLE_ADMIN', () => {
    queryParamsSubject.next({
      token: 'tok', email: 'a@a.com', role: 'ROLE_ADMIN', id: '1'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/admin');
  });

  it('redirectTest — should navigate to /coach for ROLE_COACH', () => {
    queryParamsSubject.next({
      token: 'tok', email: 'c@c.com', role: 'ROLE_COACH', id: '2'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/coach');
  });

  it('redirectTest — should navigate to /client for PLAYER role (default)', () => {
    queryParamsSubject.next({
      token: 'tok', email: 'p@p.com', role: 'PLAYER', id: '3'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/client');
  });

  it('redirectTest — should navigate to /client for unknown role (default)', () => {
    queryParamsSubject.next({
      token: 'tok', email: 'x@x.com', role: 'UNKNOWN_ROLE', id: '4'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/client');
  });

  it('redirectTest — should navigate to /client for ROLE_SPONSOR (default)', () => {
    queryParamsSubject.next({
      token: 'tok', email: 's@s.com', role: 'ROLE_SPONSOR', id: '5'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/client');
  });
});