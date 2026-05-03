import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { ResetPasswordComponent } from './reset-password.component';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResetPasswordComponent],
      imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: { get: (key: string) => 'test-token' }
            },
            queryParams: of({ token: 'test-token' }),
            queryParamMap: of({ get: (key: string) => 'test-token' })
          }
        }
      ]
    });
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
<<<<<<< HEAD

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

    expect(component.successMsg).toBe('Password modifié ! Redirection...');
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
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ statut: 400 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.errorMsg).toBe('Lien expiré ou invalide. Please recommencer.');
  });

  it('onSubmitTest — should set generic errorMsg for no-400 errors', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ statut: 500 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.errorMsg).toBe('Une erreur est survenue.');
  });

  it('onSubmitTest — should set isLoading to false after error', () => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ statut: 500 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();

    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should NOT navigate on error', fakeAsync(() => {
    createComponent();
    authServiceSpy.resetPassword.and.returnValue(throwError(() => ({ statut: 400 })));
    component.form.setValue({ newPassword: 'NewPass1!', confirmPassword: 'NewPass1!' });

    component.onSubmit();
    tick(2500);

    expect(routerSpy.navigateByUrl).not.toHaveBeenCalled();
  }));
=======
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
});
