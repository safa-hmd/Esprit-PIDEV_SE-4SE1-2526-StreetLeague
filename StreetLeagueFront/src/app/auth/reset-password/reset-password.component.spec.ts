import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { ResetPasswordComponent } from './reset-password.component';
import { AuthService } from 'src/app/services/auth.service';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  // Helper to create the component with a specific token in the URL
  function createComponent(token: string = 'valid-token') {
    TestBed.configureTestingModule({
      declarations: [ResetPasswordComponent],
      imports:      [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router,      useValue: routerSpy      },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: { get: (key: string) => (key === 'token' ? token : null) }
            }
          }
        }
      ]
    });

    fixture   = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['resetPassword']);
    routerSpy      = jasmine.createSpyObj('Router', ['navigateByUrl']);
  });

  afterEach(() => TestBed.resetTestingModule());

  // ── create ────────────────────────────────────────────────

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  // ── ngOnInit ──────────────────────────────────────────────

  it('ngOnInitTest — should read token from queryParamMap', () => {
    createComponent('my-reset-token');
    expect(component.token).toBe('my-reset-token');
  });

  it('ngOnInitTest — should set errorMsg when token is missing', () => {
    createComponent('');
    expect(component.errorMsg).toBe('Lien invalide ou expiré.');
  });

  it('ngOnInitTest — should NOT set errorMsg when token is present', () => {
    createComponent('valid-token');
    expect(component.errorMsg).toBe('');
  });

  // ── initial state ─────────────────────────────────────────

  it('initialStateTest — isLoading should be false initially', () => {
    createComponent();
    expect(component.isLoading).toBeFalse();
  });

  it('initialStateTest — successMsg should be empty initially', () => {
    createComponent();
    expect(component.successMsg).toBe('');
  });

  // ── form validation ───────────────────────────────────────

  it('formTest — form should be invalid when empty', () => {
    createComponent();
    expect(component.form.invalid).toBeTrue();
  });

  it('formTest — newPassword should be required', () => {
    createComponent();
    const ctrl = component.form.get('newPassword');
    ctrl?.setValue('');
    expect(ctrl?.hasError('required')).toBeTrue();
  });

  it('formTest — newPassword should enforce minLength of 6', () => {
    createComponent();
    const ctrl = component.form.get('newPassword');
    ctrl?.setValue('abc');
    expect(ctrl?.hasError('minlength')).toBeTrue();
  });

  it('formTest — confirmPassword should be required', () => {
    createComponent();
    const ctrl = component.form.get('confirmPassword');
    ctrl?.setValue('');
    expect(ctrl?.hasError('required')).toBeTrue();
  });

  it('formTest — should have mismatch error when passwords differ', () => {
    createComponent();
    component.form.setValue({ newPassword: 'Pass123!', confirmPassword: 'Different1!' });
    expect(component.form.hasError('mismatch')).toBeTrue();
  });

  it('formTest — should be valid when passwords match and meet requirements', () => {
    createComponent();
    component.form.setValue({ newPassword: 'Pass123!', confirmPassword: 'Pass123!' });
    expect(component.form.valid).toBeTrue();
  });

  // ── onSubmit — guards ─────────────────────────────────────

  it('onSubmitTest — should not call resetPassword when form is invalid', () => {
    createComponent();
    component.form.setValue({ newPassword: '', confirmPassword: '' });
    component.onSubmit();
    expect(authServiceSpy.resetPassword).not.toHaveBeenCalled();
  });

  it('onSubmitTest — should not call resetPassword when token is missing', () => {
    createComponent('');
    component.form.setValue({ newPassword: 'Pass123!', confirmPassword: 'Pass123!' });
    component.onSubmit();
    expect(authServiceSpy.resetPassword).not.toHaveBeenCalled();
  });

  // ── onSubmit — success ────────────────────────────────────

  it('onSubmitTest — should call resetPassword with token and newPassword', () => {
    createComponent('valid-token');
    authServiceSpy.resetPassword.and.returnValue(of('OK'));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(authServiceSpy.resetPassword).toHaveBeenCalledOnceWith('valid-token', 'NewPass1!');
  });

  it('onSubmitTest — should set successMsg on success', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(of('OK'));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.successMsg).toBe('Mot de passe modifié ! Redirection...');
  });

  it('onSubmitTest — should set isLoading to false after success', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(of('OK'));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should navigate to /login after 2500ms on success', fakeAsync(() => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(of('OK'));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();
    tick(2500);

    expect(routerSpy.navigateByUrl).toHaveBeenCalledOnceWith('/login');
  }));

  it('onSubmitTest — should NOT navigate before 2500ms on success', fakeAsync(() => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(of('OK'));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();
    tick(1000);

    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
    tick(1500); // cleanup
  }));

  // ── onSubmit — error ──────────────────────────────────────

  it('onSubmitTest — should set errorMsg for status 400', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ status: 400 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.errorMsg).toBe('Lien expiré ou invalide. Veuillez recommencer.');
  });

  it('onSubmitTest — should set generic errorMsg for non-400 errors', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ status: 500 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.errorMsg).toBe('Une erreur est survenue.');
  });

  it('onSubmitTest — should set isLoading to false after error', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ status: 500 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should NOT navigate on error', fakeAsync(() => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ status: 400 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();
    tick(2500);

    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  }));
});