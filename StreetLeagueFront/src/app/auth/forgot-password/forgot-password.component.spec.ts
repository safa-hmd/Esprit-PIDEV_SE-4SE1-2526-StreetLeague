import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { ForgotPasswordComponent } from './forgot-password.component';
import { AuthService } from 'src/app/services/auth.service';

describe('ForgotPasswordComponent', () => {
  let component: ForgotPasswordComponent;
  let fixture: ComponentFixture<ForgotPasswordComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['forgotPassword']);

    TestBed.configureTestingModule({
      declarations: [ForgotPasswordComponent],
      imports:      [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    fixture   = TestBed.createComponent(ForgotPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ── form initial state ────────────────────────────────────

  it('formTest — form should be invalid when empty', () => {
    expect(component.form.invalid).toBeTrue();
  });

  it('formTest — email field should be required', () => {
    const email = component.form.get('email');
    email?.setValue('');
    expect(email?.hasError('required')).toBeTrue();
  });

  it('formTest — email field should reject invalid format', () => {
    const email = component.form.get('email');
    email?.setValue('not-an-email');
    expect(email?.hasError('email')).toBeTrue();
  });

  it('formTest — form should be valid with a correct email', () => {
    component.form.setValue({ email: 'john@test.com' });
    expect(component.form.valid).toBeTrue();
  });

  // ── onSubmit — invalid form ───────────────────────────────

  it('onSubmitTest — should not call forgotPassword when form is invalid', () => {
    component.form.setValue({ email: '' });
    component.onSubmit();
    expect(authServiceSpy.forgotPassword).not.toHaveBeenCalled();
  });

  // ── onSubmit — success ────────────────────────────────────

  it('onSubmitTest — should call forgotPassword with the email value', () => {
    authServiceSpy.forgotPassword.and.returnValue(of('Email sent'));
    component.form.setValue({ email: 'john@test.com' });

    component.onSubmit();

    expect(authServiceSpy.forgotPassword).toHaveBeenCalledOnceWith('john@test.com');
  });

  it('onSubmitTest — should set successMsg on success', () => {
    authServiceSpy.forgotPassword.and.returnValue(of('Email sent'));
    component.form.setValue({ email: 'john@test.com' });

    component.onSubmit();

    expect(component.successMsg).toBe(
      'Un email de réinitialisation a été envoyé à votre adresse.'
    );
  });

  it('onSubmitTest — should clear errorMsg on success', () => {
    authServiceSpy.forgotPassword.and.returnValue(of('Email sent'));
    component.errorMsg = 'old error';
    component.form.setValue({ email: 'john@test.com' });

    component.onSubmit();

    expect(component.errorMsg).toBe('');
  });

  it('onSubmitTest — should set isLoading to false after success', () => {
    authServiceSpy.forgotPassword.and.returnValue(of('Email sent'));
    component.form.setValue({ email: 'john@test.com' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  // ── onSubmit — error ──────────────────────────────────────

  it('onSubmitTest — should set errorMsg on error', () => {
    authServiceSpy.forgotPassword.and.returnValue(throwError(() => ({ status: 404 })));
    component.form.setValue({ email: 'unknown@test.com' });

    component.onSubmit();

    expect(component.errorMsg).toBe('Aucun compte trouvé avec cet email.');
  });

  it('onSubmitTest — should clear successMsg on error', () => {
    authServiceSpy.forgotPassword.and.returnValue(throwError(() => ({ status: 404 })));
    component.successMsg = 'old success';
    component.form.setValue({ email: 'unknown@test.com' });

    component.onSubmit();

    expect(component.successMsg).toBe('');
  });

  it('onSubmitTest — should set isLoading to false after error', () => {
    authServiceSpy.forgotPassword.and.returnValue(throwError(() => ({ status: 404 })));
    component.form.setValue({ email: 'unknown@test.com' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  // ── initial values ────────────────────────────────────────

  it('initialStateTest — isLoading should be false initially', () => {
    expect(component.isLoading).toBeFalse();
  });

  it('initialStateTest — successMsg should be empty initially', () => {
    expect(component.successMsg).toBe('');
  });

  it('initialStateTest — errorMsg should be empty initially', () => {
    expect(component.errorMsg).toBe('');
  });
});