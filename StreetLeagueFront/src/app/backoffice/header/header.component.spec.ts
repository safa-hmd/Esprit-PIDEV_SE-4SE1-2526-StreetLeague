import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { HeaderComponent } from './header.component';
import { UserService } from 'src/app/services/user.service';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockProfile = {
    fullName: 'Alice Martin',
    email:    'alice@test.com'
  };

  beforeEach(() => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getProfile']);
    routerSpy      = jasmine.createSpyObj('Router', ['navigate']);

    // Default: getProfile succeeds
    userServiceSpy.getProfile.and.returnValue(of(mockProfile as any));

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [HeaderComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router,      useValue: routerSpy      }
      ]
    });

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();                                 // triggers ngOnInit
  });

  afterEach(() => localStorage.clear());

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── initial state ─────────────────────────────────────────

  it('initialStateTest — dropdownOpen should be false initially', () => {
    expect(component.dropdownOpen).toBeFalse();
  });

  it('initialStateTest — menuOpen should be false initially', () => {
    expect(component.menuOpen).toBeFalse();
  });

  it('initialStateTest — hasNotifications should be true initially', () => {
    expect(component.hasNotifications).toBeTrue();
  });

  it('initialStateTest — pageTitle should be Overview initially', () => {
    expect(component.pageTitle).toBe('Overview');
  });

  it('initialStateTest — adminName should be Alice Martin initially', () => {
    expect(component.adminName).toBe('Alice Martin');
  });

  it('initialStateTest — adminEmail should be alice@test.com initially', () => {
    expect(component.adminEmail).toBe('alice@test.com');
  });

  it('initialStateTest — adminInitial should be A initially', () => {
    expect(component.adminInitial).toBe('A');
  });

  it('initialStateTest — pageTitle should be Overview initially', () => {
    expect(component.pageTitle).toBe('Overview');
  });

  // ── ngOnInit — success ────────────────────────────────────

  it('ngOnInitTest — should call getProfile on init', () => {
    expect(userServiceSpy.getProfile).toHaveBeenCalled();
  });

  it('ngOnInitTest — should set adminName from profile', () => {
    expect(component.adminName).toBe('Alice Martin');
  });

  it('ngOnInitTest — should set adminEmail from profile', () => {
    expect(component.adminEmail).toBe('alice@test.com');
  });

  it('ngOnInitTest — should set adminInitial as first letter uppercased', () => {
    expect(component.adminInitial).toBe('A');
  });

  it('ngOnInitTest — should save fullName to localStorage on success', () => {
    expect(localStorage.getItem('userName')).toBe('Alice Martin');
  });

  // ── ngOnInit — error fallback ─────────────────────────────

  it('ngOnInitTest — should use localStorage userName as fallback on error', () => {
    localStorage.clear();                                    // ← reset to clean state
    localStorage.setItem('userName',  'Bob Fallback');
    localStorage.setItem('userEmail', 'bob@test.com');
    userServiceSpy.getProfile.and.returnValue(throwError(() => ({ statut: 401 })));

    fixture   = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.adminName).toBe('Bob Fallback');
  });

  it('ngOnInitTest — should use localStorage userEmail as fallback on error', () => {
    localStorage.clear();
    localStorage.setItem('userName',  'Bob Fallback');
    localStorage.setItem('userEmail', 'bob@test.com');
    userServiceSpy.getProfile.and.returnValue(throwError(() => ({ statut: 401 })));

    fixture   = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.adminEmail).toBe('bob@test.com');
  });

  it('ngOnInitTest — should derive adminInitial from fallback userName', () => {
    localStorage.clear();
    localStorage.setItem('userName', 'Bob Fallback');
    userServiceSpy.getProfile.and.returnValue(throwError(() => ({ statut: 401 })));

    fixture   = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.adminInitial).toBe('B');
  });

  it('ngOnInitTest — should default to Administrator when localStorage is empty on error', () => {
    localStorage.clear();                                    // ← garantit localStorage vide
    userServiceSpy.getProfile.and.returnValue(throwError(() => ({ statut: 401 })));

    fixture   = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.adminName).toBe('Administrator');
  });

  it('ngOnInitTest — adminInitial should be A when defaulting to Administrator', () => {
    localStorage.clear();
    userServiceSpy.getProfile.and.returnValue(throwError(() => ({ statut: 401 })));

    fixture   = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.adminInitial).toBe('A');
  });

  // ── toggleDropdown ────────────────────────────────────────

  it('toggleDropdownTest — should open dropdown when closed', () => {
    component.dropdownOpen = false;
    component.toggleDropdown();
    expect(component.dropdownOpen).toBeTrue();
  });

  it('toggleDropdownTest — should close dropdown when open', () => {
    component.dropdownOpen = true;
    component.toggleDropdown();
    expect(component.dropdownOpen).toBeFalse();
  });

  it('toggleDropdownTest — should toggle twice back to original state', () => {
    component.toggleDropdown();
    component.toggleDropdown();
    expect(component.dropdownOpen).toBeFalse();
  });

  // ── toggleMenu ────────────────────────────────────────────

  it('toggleMenuTest — should open menu when closed', () => {
    component.menuOpen = false;
    component.toggleMenu();
    expect(component.menuOpen).toBeTrue();
  });

  it('toggleMenuTest — should close menu when open', () => {
    component.menuOpen = true;
    component.toggleMenu();
    expect(component.menuOpen).toBeFalse();
  });

  // ── logout ────────────────────────────────────────────────

  it('logoutTest — should clear localStorage', () => {
    localStorage.setItem('TokenUserConnect', 'tok');
    localStorage.setItem('userName', 'Alice');

    component.logout();

    expect(localStorage.getItem('TokenUserConnect')).toBeNull();
    expect(localStorage.getItem('userName')).toBeNull();
  });

  it('logoutTest — should navigate to /admin-login', () => {
    component.logout();
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith(['/admin-login']);
  });

  // ── onDocumentClick ───────────────────────────────────────

  it('onDocumentClickTest — should close dropdown when clicking outside .account-wrap', () => {
    component.dropdownOpen = true;

    const outsideEl = document.createElement('div');
    document.body.appendChild(outsideEl);
    const event = new MouseEvent('click', { bubbles: true });
    Object.defineProperty(event, 'target', { value: outsideEl });

    component.onDocumentClick(event);

    expect(component.dropdownOpen).toBeFalse();
    document.body.removeChild(outsideEl);
  });

  it('onDocumentClickTest — should keep dropdown open when clicking inside .account-wrap', () => {
    component.dropdownOpen = true;

    const wrap  = document.createElement('div');
    wrap.classList.add('account-wrap');
    const inner = document.createElement('button');
    wrap.appendChild(inner);
    document.body.appendChild(wrap);

    const event = new MouseEvent('click', { bubbles: true });
    Object.defineProperty(event, 'target', { value: inner });

    component.onDocumentClick(event);

    expect(component.dropdownOpen).toBeTrue();
    document.body.removeChild(wrap);
  });
});
