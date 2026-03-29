import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';

import { SelectRoleComponent } from './select-role.component';

describe('SelectRoleComponent', () => {
  let component: SelectRoleComponent;
  let fixture: ComponentFixture<SelectRoleComponent>;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;
  let queryParamsSubject: Subject<any>;

  const API_URL = 'http://localhost:8086/StreetLeague/auth/complete-google-register';

  beforeEach(() => {
    routerSpy          = jasmine.createSpyObj('Router', ['navigateByUrl']);
    queryParamsSubject = new Subject<any>();

    TestBed.configureTestingModule({
      declarations: [SelectRoleComponent],
      imports:      [HttpClientTestingModule],
      providers: [
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: { queryParams: queryParamsSubject.asObservable() }
        }
      ]
    });

    fixture   = TestBed.createComponent(SelectRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── initial state ─────────────────────────────────────────

  it('initialStateTest — selectedRole should be PLAYER by default', () => {
    expect(component.selectedRole).toBe('PLAYER');
  });

  it('initialStateTest — isLoading should be false initially', () => {
    expect(component.isLoading).toBeFalse();
  });

  it('initialStateTest — errorMessage should be empty initially', () => {
    expect(component.errorMessage).toBe('');
  });

  it('initialStateTest — roles list should contain 4 roles', () => {
    expect(component.roles.length).toBe(4);
  });

  it('initialStateTest — roles list should contain PLAYER, COACH, SPONSOR, DELIVERY', () => {
    const values = component.roles.map(r => r.value);
    expect(values).toContain('PLAYER');
    expect(values).toContain('COACH');
    expect(values).toContain('SPONSOR');
    expect(values).toContain('DELIVERY');
  });

  // ── ngOnInit ──────────────────────────────────────────────

  it('ngOnInitTest — should read email from queryParams', () => {
    queryParamsSubject.next({ email: 'john@test.com', name: 'John Doe' });
    expect(component.email).toBe('john@test.com');
  });

  it('ngOnInitTest — should read fullName from queryParams', () => {
    queryParamsSubject.next({ email: 'john@test.com', name: 'John Doe' });
    expect(component.fullName).toBe('John Doe');
  });

  it('ngOnInitTest — should default email to empty string when not in params', () => {
    queryParamsSubject.next({});
    expect(component.email).toBe('');
  });

  it('ngOnInitTest — should default fullName to empty string when not in params', () => {
    queryParamsSubject.next({});
    expect(component.fullName).toBe('');
  });

  // ── selectRole ────────────────────────────────────────────

  it('selectRoleTest — should change selectedRole to COACH', () => {
    component.selectRole('COACH');
    expect(component.selectedRole).toBe('COACH');
  });

  it('selectRoleTest — should change selectedRole to SPONSOR', () => {
    component.selectRole('SPONSOR');
    expect(component.selectedRole).toBe('SPONSOR');
  });

  it('selectRoleTest — should change selectedRole to DELIVERY', () => {
    component.selectRole('DELIVERY');
    expect(component.selectedRole).toBe('DELIVERY');
  });

  // ── confirm — request ─────────────────────────────────────

  it('confirmTest — should call POST complete-google-register with correct body', () => {
    queryParamsSubject.next({ email: 'john@test.com', name: 'John Doe' });
    component.selectRole('COACH');

    component.confirm();

    const req = httpMock.expectOne(API_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      email:    'john@test.com',
      fullName: 'John Doe',
      role:     'COACH'
    });
    req.flush({ token: 't', email: 'john@test.com', role: 'ROLE_COACH', idUser: '1' });
  });

  it('confirmTest — should set isLoading to true while request is pending', () => {
    component.confirm();
    expect(component.isLoading).toBeTrue();
    httpMock.expectOne(API_URL).flush({ token: 't', email: 'a@a.com', role: 'PLAYER', idUser: '1' });
  });

  // ── confirm — success / localStorage ─────────────────────

  it('confirmTest — should save token to localStorage on success', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush({
      token: 'my-token', email: 'john@test.com', role: 'ROLE_COACH', idUser: '42'
    });
    expect(localStorage.getItem('TokenUserConnect')).toBe('my-token');
  });

  it('confirmTest — should save all fields to localStorage on success', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush({
      token: 'tok', email: 'john@test.com', role: 'ROLE_ADMIN', idUser: '7'
    });
    expect(localStorage.getItem('EmailUserConnect')).toBe('john@test.com');
    expect(localStorage.getItem('RoleUserConnect')).toBe('ROLE_ADMIN');
    expect(localStorage.getItem('UserIdConnect')).toBe('7');
  });

  it('confirmTest — should set isLoading to false after success', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush({
      token: 't', email: 'a@a.com', role: 'PLAYER', idUser: '1'
    });
    expect(component.isLoading).toBeFalse();
  });

  // ── confirm — redirect by role ────────────────────────────

  it('confirmTest — should navigate to /admin for ROLE_ADMIN', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush({
      token: 't', email: 'a@a.com', role: 'ROLE_ADMIN', idUser: '1'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/admin');
  });

  it('confirmTest — should navigate to /coach for ROLE_COACH', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush({
      token: 't', email: 'c@c.com', role: 'ROLE_COACH', idUser: '2'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/coach');
  });

  it('confirmTest — should navigate to /client for default role', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush({
      token: 't', email: 'p@p.com', role: 'PLAYER', idUser: '3'
    });
    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/client');
  });

  // ── confirm — error ───────────────────────────────────────

  it('confirmTest — should set errorMessage on failure', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush(null, { status: 500, statusText: 'Server Error' });
    expect(component.errorMessage).toBe('Une erreur est survenue. Veuillez réessayer.');
  });

  it('confirmTest — should set isLoading to false after error', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush(null, { status: 500, statusText: 'Server Error' });
    expect(component.isLoading).toBeFalse();
  });

  it('confirmTest — should NOT navigate on error', () => {
    component.confirm();
    httpMock.expectOne(API_URL).flush(null, { status: 500, statusText: 'Server Error' });
    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });
});