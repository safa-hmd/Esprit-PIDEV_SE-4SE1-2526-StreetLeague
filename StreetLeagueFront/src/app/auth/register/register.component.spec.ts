import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from 'src/app/services/auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
  RouterTestingModule.withRoutes([
    { path: 'login',  component: RegisterComponent },
    { path: 'client', component: RegisterComponent },
  ]),
  ReactiveFormsModule
],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    router    = TestBed.inject(Router);
    fixture   = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ── Init ──────────────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have PLAYER as default role', () => {
    expect(component.selectedRole).toBe('PLAYER');
  });

  it('registerForm should be invalid when empty', () => {
    expect(component.registerForm.invalid).toBeTrue();
  });

  // ── selectRole ────────────────────────────────────────────

  it('selectRoleTest — should change selectedRole', () => {
    component.selectRole('COACH');
    expect(component.selectedRole).toBe('COACH');
  });

  it('selectRoleTest — should clear errorMessage', () => {
    component.errorMessage = 'some error';
    component.selectRole('ADMIN');
    expect(component.errorMessage).toBe('');
  });

  it('selectRoleTest — should update roleLabel', () => {
    component.selectRole('SPONSOR');
    expect(component.roleLabel).toBe('Sponsor');
  });

  // ── checkStrength ─────────────────────────────────────────

  it('checkStrengthTest — empty string resets strength', () => {
    component.checkStrength('');
    expect(component.strengthWidth).toBe('0%');
    expect(component.strengthLabel).toBe('');
  });

  it('checkStrengthTest — weak password', () => {
    component.checkStrength('abc');
    expect(component.strengthWidth).toBe('33%');
    expect(component.strengthColor).toBe('#dc2626');
    expect(component.strengthLabel).toBe('🔴 Faible');
  });

  it('checkStrengthTest — medium password', () => {
    component.checkStrength('Abcdef1');
    expect(component.strengthWidth).toBe('66%');
    expect(component.strengthColor).toBe('#f59e0b');
    expect(component.strengthLabel).toBe('🟡 Moyen');
  });

  it('checkStrengthTest — strong password', () => {
    component.checkStrength('Abcdef1!');
    expect(component.strengthWidth).toBe('100%');
    expect(component.strengthColor).toBe('#16a34a');
    expect(component.strengthLabel).toBe('🟢 Fort');
  });

  // ── Form validation ───────────────────────────────────────

  it('registerFormTest — should be invalid with empty fields', () => {
    expect(component.registerForm.invalid).toBeTrue();
  });

  it('registerFormTest — should be invalid with bad email', () => {
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'notanemail', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: true
    });
    expect(component.registerForm.invalid).toBeTrue();
  });

  it('registerFormTest — should be invalid when passwords do not match', () => {
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: 'Pass123!',
      confirmPassword: 'Different1!', terms: true
    });
    expect(component.registerForm.hasError('passwordMismatch')).toBeTrue();
  });

  it('registerFormTest — should be invalid when terms not accepted', () => {
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: false
    });
    expect(component.registerForm.invalid).toBeTrue();
  });

  it('registerFormTest — should be valid with correct data', () => {
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: true
    });
    expect(component.registerForm.valid).toBeTrue();
  });

  it('registerFormTest — should be invalid with short password', () => {
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: '123',
      confirmPassword: '123', terms: true
    });
    expect(component.registerForm.invalid).toBeTrue();
  });

  // ── onSubmit ──────────────────────────────────────────────

  it('onSubmitTest — should not call register if form invalid', () => {
    component.onSubmit();
    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('onSubmitTest — should call register with correct values', () => {
    authServiceSpy.register.and.returnValue(of({} as any));
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: true
    });
    component.onSubmit();
    expect(authServiceSpy.register).toHaveBeenCalledWith({
      fullName: 'John Doe',
      email:    'john@test.com',
      password: 'Pass123!',
      role:     'PLAYER'
    });
  });

  it('onSubmitTest — should set successMessage on success', () => {
    authServiceSpy.register.and.returnValue(of({} as any));
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: true
    });
    component.onSubmit();
    expect(component.successMessage).toContain('Compte créé');
    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should set errorMessage on register failure', () => {
    authServiceSpy.register.and.returnValue(
      throwError(() => ({ error: { message: 'Email already exists' } }))
    );
    component.registerForm.setValue({
      firstName: 'John', lastName: 'Doe',
      email: 'john@test.com', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: true
    });
    component.onSubmit();
    expect(component.errorMessage).toBe('Email already exists');
    expect(component.isLoading).toBeFalse();
  });

  it('onSubmitTest — should use selected role when registering', () => {
    authServiceSpy.register.and.returnValue(of({} as any));
    component.selectRole('COACH');
    component.registerForm.setValue({
      firstName: 'Sara', lastName: 'Ali',
      email: 'sara@test.com', password: 'Pass123!',
      confirmPassword: 'Pass123!', terms: true
    });
    component.onSubmit();
    expect(authServiceSpy.register).toHaveBeenCalledWith(
      jasmine.objectContaining({ role: 'COACH' })
    );
  });
});