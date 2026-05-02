import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from 'src/app/services/auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login', 'loginWithGoogle']);

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'client', component: LoginComponent },
          { path: 'coach',  component: LoginComponent },
        ]),
        ReactiveFormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init loginForm with empty fields', () => {
    expect(component.loginForm.value.email).toBe('');
    expect(component.loginForm.value.password).toBe('');
  });

  it('should have PLAYER as default role', () => {
    expect(component.selectedRole).toBe('PLAYER');
  });

  it('loginForm should be invalid when empty', () => {
    expect(component.loginForm.invalid).toBeTrue();
  });

  // ── selectRole ────────────────────────────────────────────

  it('selectRoleTest — should clear errorMessage', () => {
    component.errorMessage = 'some error';
    component.selectRole('COACH');
    expect(component.errorMessage).toBe('');
  });

  it('selectRoleTest — should update selectedRole', () => {
    component.selectRole('COACH');
    expect(component.selectedRole).toBe('COACH');
  });

  it('selectRoleTest — should update emailPlaceholder', () => {
    component.selectRole('COACH');
    expect(component.emailPlaceholder).toBe('coach@streetleague.com');
  });

  // ── roleLabel ─────────────────────────────────────────────

  it('roleLabelTest — PLAYER returns Player', () => {
    component.selectedRole = 'PLAYER';
    expect(component.roleLabel).toBe('Player');        // ✅ corrigé (était 'Joueur')
  });

  it('roleLabelTest — COACH returns Coach', () => {
    component.selectedRole = 'COACH';
    expect(component.roleLabel).toBe('Coach');
  });

  it('roleLabelTest — SPONSOR returns Sponsor', () => {
    component.selectedRole = 'SPONSOR';
    expect(component.roleLabel).toBe('Sponsor');
  });

  it('roleLabelTest — DELIVERY returns Delivery', () => {
    component.selectedRole = 'DELIVERY';
    expect(component.roleLabel).toBe('Delivery');
  });

  // ── emailPlaceholder ──────────────────────────────────────

  it('emailPlaceholderTest — PLAYER returns player@streetleague.com', () => {
    component.selectedRole = 'PLAYER';
    expect(component.emailPlaceholder).toBe('player@streetleague.com'); // ✅ corrigé
  });

  it('emailPlaceholderTest — COACH returns coach@streetleague.com', () => {
    component.selectedRole = 'COACH';
    expect(component.emailPlaceholder).toBe('coach@streetleague.com');
  });

  it('emailPlaceholderTest — SPONSOR returns sponsor@streetleague.com', () => {
    component.selectedRole = 'SPONSOR';
    expect(component.emailPlaceholder).toBe('sponsor@streetleague.com');
  });

  it('emailPlaceholderTest — DELIVERY returns delivery@streetleague.com', () => {
    component.selectedRole = 'DELIVERY';
    expect(component.emailPlaceholder).toBe('delivery@streetleague.com');
  });

  // ── Form validation ───────────────────────────────────────

  it('loginFormTest — should be invalid with bad email', () => {
    component.loginForm.setValue({ email: 'notanemail', password: '123456' });
    expect(component.loginForm.invalid).toBeTrue();
  });

  it('loginFormTest — should be valid with correct credentials', () => {
    component.loginForm.setValue({ email: 'test@test.com', password: '123456' });
    expect(component.loginForm.valid).toBeTrue();
  });

  it('loginFormTest — should be invalid with empty password', () => {
    component.loginForm.setValue({ email: 'test@test.com', password: '' });
    expect(component.loginForm.invalid).toBeTrue();
  });

  // ── onSubmit ──────────────────────────────────────────────

  it('onSubmitTest — should not call login if form invalid', () => {
    component.loginForm.setValue({ email: '', password: '' });
    component.onSubmit();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('onSubmitTest — should call login with correct values', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'test@test.com', role: 'PLAYER'
    } as any));

    component.loginForm.setValue({ email: 'test@test.com', password: '123456' });
    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      email: 'test@test.com', password: '123456'
    });
  });

  it('onSubmitTest — should save token on success', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'mytoken', email: 'test@test.com', role: 'PLAYER'
    } as any));

    component.loginForm.setValue({ email: 'test@test.com', password: '123456' });
    component.onSubmit();

    expect(localStorage.getItem('TokenUserConnect')).toBe('mytoken');
    expect(localStorage.getItem('EmailUserConnect')).toBe('test@test.com');
    expect(localStorage.getItem('RoleUserConnect')).toBe('PLAYER');
  });

  it('onSubmitTest — should set isLoading to false after success', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'test@test.com', role: 'PLAYER'
    } as any));

    component.loginForm.setValue({ email: 'test@test.com', password: '123456' });
    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should set errorMessage on login failure', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ statut: 401 })));

    component.loginForm.setValue({ email: 'test@test.com', password: 'wrongpass' });
    component.onSubmit();

    expect(component.errorMessage).toBe('Email ou password incorrect.');
    expect(component.isLoading).toBeFalse();
  });

  // ── redirectByRole ────────────────────────────────────────

  it('redirectByRoleTest — ROLE_COACH navigates to /coach', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'coach@test.com', role: 'ROLE_COACH'
    } as any));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    component.loginForm.setValue({ email: 'coach@test.com', password: '123456' });
    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith('/coach');
  });

  it('redirectByRoleTest — PLAYER navigates to /client', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'player@test.com', role: 'PLAYER'
    } as any));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    component.loginForm.setValue({ email: 'player@test.com', password: '123456' });
    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith('/client');
  });

  it('redirectByRoleTest — SPONSOR navigates to /client', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'sponsor@test.com', role: 'SPONSOR'
    } as any));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    component.loginForm.setValue({ email: 'sponsor@test.com', password: '123456' });
    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith('/client');
  });

  it('redirectByRoleTest — DELIVERY navigates to /client', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'delivery@test.com', role: 'DELIVERY'
    } as any));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    component.loginForm.setValue({ email: 'delivery@test.com', password: '123456' });
    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith('/client');
  });
<<<<<<< HEAD

  it('redirectByRoleTest — ROLE_ADMIN navigates to /admin', () => {
    authServiceSpy.login.and.returnValue(of({
      token: 'abc', email: 'admin@test.com', role: 'ROLE_ADMIN'
    } as any));
    const navigateSpy = spyOn(router, 'navigateByUrl');

    component.loginForm.setValue({ email: 'admin@test.com', password: '123456' });
    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith('/admin');
  });
});
=======
});
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
