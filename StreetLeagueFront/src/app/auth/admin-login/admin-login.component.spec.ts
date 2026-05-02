import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AdminLoginComponent } from './admin-login.component';

describe('AdminLoginComponent', () => {
  let component: AdminLoginComponent;
  let fixture: ComponentFixture<AdminLoginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminLoginComponent],
      imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule]
    });
    fixture = TestBed.createComponent(AdminLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
<<<<<<< HEAD

  // ── form initial state ────────────────────────────────────

  it('formTest — form should be invalid when empty', () => {
    expect(component.loginForm.invalid).toBeTrue();
  });

  it('formTest — email field should be required', () => {
    const email = component.loginForm.get('email');
    email?.setValue('');
    expect(email?.hasError('required')).toBeTrue();
  });

  it('formTest — email field should reject invalid format', () => {
    const email = component.loginForm.get('email');
    email?.setValue('not-an-email');
    expect(email?.hasError('email')).toBeTrue();
  });

  it('formTest — password field should be required', () => {
    const password = component.loginForm.get('password');
    password?.setValue('');
    expect(password?.hasError('required')).toBeTrue();
  });

  it('formTest — form should be valid with correct email and password', () => {
    component.loginForm.setValue({ email: 'admin@test.com', password: 'Pass123!' });
    expect(component.loginForm.valid).toBeTrue();
  });

  // ── onSubmit — invalid form ───────────────────────────────

  it('onSubmitTest — should not call login when form is invalid', () => {
    component.loginForm.setValue({ email: '', password: '' });
    component.onSubmit();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  // ── onSubmit — ADMIN success ──────────────────────────────

  it('onSubmitTest — should call authService.login with form values', () => {
    authServiceSpy.login.and.returnValue(of(mockAdminResponse));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'Pass123!' });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledOnceWith({
      email: 'admin@test.com',
      password: 'Pass123!'
    });
  });

  it('onSubmitTest — should set isLoading to true while submitting', () => {
    authServiceSpy.login.and.returnValue(of(mockAdminResponse));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'Pass123!' });

    component.onSubmit();

    // After observable completes, isLoading should be false
    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should navigate to /admin on successful ADMIN login', () => {
    authServiceSpy.login.and.returnValue(of(mockAdminResponse));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'Pass123!' });

    component.onSubmit();

    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/admin');
  });

  it('onSubmitTest — should not show errorMessage on successful ADMIN login', () => {
    authServiceSpy.login.and.returnValue(of(mockAdminResponse));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'Pass123!' });

    component.onSubmit();

    expect(component.errorMessage).toBe('');
  });

  // ── onSubmit — no-ADMIN role ─────────────────────────────

  it('onSubmitTest — should logout and show error when role is not ROLE_ADMIN', () => {
    authServiceSpy.login.and.returnValue(of(mockPlayerResponse));
    component.loginForm.setValue({ email: 'player@test.com', password: 'Pass123!' });

    component.onSubmit();

    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(component.errorMessage).toBe(
      'Accès refusé. Cette interface est réservée aux administrateurs.'
    );
  });

  it('onSubmitTest — should NOT navigate when role is not ROLE_ADMIN', () => {
    authServiceSpy.login.and.returnValue(of(mockPlayerResponse));
    component.loginForm.setValue({ email: 'player@test.com', password: 'Pass123!' });

    component.onSubmit();

    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });

  it('onSubmitTest — should set isLoading to false after no-ADMIN role response', () => {
    authServiceSpy.login.and.returnValue(of(mockPlayerResponse));
    component.loginForm.setValue({ email: 'player@test.com', password: 'Pass123!' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  // ── onSubmit — HTTP error ─────────────────────────────────

  it('onSubmitTest — should show error message on login failure', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ statut: 401 })));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'wrongpass' });

    component.onSubmit();

    expect(component.errorMessage).toBe('Email ou password incorrect.');
  });

  it('onSubmitTest — should set isLoading to false on error', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ statut: 401 })));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'wrongpass' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should NOT navigate on login error', () => {
    authServiceSpy.login.and.returnValue(throwError(() => ({ statut: 401 })));
    component.loginForm.setValue({ email: 'admin@test.com', password: 'wrongpass' });

    component.onSubmit();

    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  });
=======
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
});
