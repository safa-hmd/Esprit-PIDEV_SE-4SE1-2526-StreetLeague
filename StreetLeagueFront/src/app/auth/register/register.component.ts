import { Component } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

const passwordMatchValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const password        = group.get('password')?.value;
  const confirmPassword = group.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordMismatch: true };
};

type Role = 'PLAYER' | 'COACH' | 'SPONSOR' | 'DELIVERY' | 'ADMIN';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  selectedRole: Role = 'PLAYER';
  isLoading      = false;
  errorMessage   = '';
  successMessage = '';

  strengthWidth = '0%';
  strengthColor = '#eee';
  strengthLabel = '';

  private roleLabels: Record<Role, string> = {
    PLAYER:   'Player',
    COACH:    'Coach',
    SPONSOR:  'Sponsor',
    DELIVERY: 'Delivery',
    ADMIN:    'Admin',
  };

  private emailPlaceholders: Record<Role, string> = {
    PLAYER:   'player@streetleague.com',
    COACH:    'coach@streetleague.com',
    SPONSOR:  'sponsor@streetleague.com',
    DELIVERY: 'delivery@streetleague.com',
    ADMIN:    'admin@streetleague.com',
  };

  get roleLabel():        string { return this.roleLabels[this.selectedRole]; }
  get emailPlaceholder(): string { return this.emailPlaceholders[this.selectedRole]; }

  registerForm = new FormGroup(
    {
      firstName:       new FormControl('', [Validators.required]),
      lastName:        new FormControl('', [Validators.required]),
      email:           new FormControl('', [Validators.required, Validators.email]),
      password:        new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required]),
      terms:           new FormControl(false, [Validators.requiredTrue]),
    },
    { validators: passwordMatchValidator }
  );

  get f() { return this.registerForm.controls; }

  constructor(private authService: AuthService, private router: Router) {}

  selectRole(role: Role) {
    this.selectedRole = role;
    this.errorMessage = '';
  }

  checkStrength(val: string) {
    if (!val) { this.strengthWidth = '0%'; this.strengthColor = '#eee'; this.strengthLabel = ''; return; }
    let score = 0;
    if (val.length >= 8)          score++;
    if (/[A-Z]/.test(val))        score++;
    if (/[0-9]/.test(val))        score++;
    if (/[^A-Za-z0-9]/.test(val)) score++;
    if (score <= 1) { this.strengthWidth = '33%'; this.strengthColor = '#dc2626'; this.strengthLabel = '🔴 Faible'; }
    else if (score <= 3) { this.strengthWidth = '66%'; this.strengthColor = '#f59e0b'; this.strengthLabel = '🟡 Moyen'; }
    else { this.strengthWidth = '100%'; this.strengthColor = '#16a34a'; this.strengthLabel = '🟢 Fort'; }
  }

  onSubmit() {
    if (this.registerForm.invalid) return;
    const fullName = `${this.f['firstName'].value} ${this.f['lastName'].value}`;
    const email    = this.f['email'].value!;
    const password = this.f['password'].value!;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register({ fullName, email, password, role: this.selectedRole }).subscribe({
      next: () => {
        this.isLoading = false;
        const isAdmin = this.selectedRole === 'ADMIN';
        this.successMessage = isAdmin
          ? 'Compte créé ! Redirection vers la connexion administrateur…'
          : 'Compte créé ! Redirection vers la connexion…';
        const target = isAdmin ? '/admin-login' : '/login';
        setTimeout(() => this.router.navigateByUrl(target), 1500);
      },
      error: (error) => {
        this.isLoading    = false;
        const body = error?.error;
        this.errorMessage =
          (typeof body === 'object' && body !== null && 'message' in body && (body as { message?: string }).message) ||
          (typeof body === 'object' && body !== null && 'error' in body && String((body as { error?: unknown }).error)) ||
          'Une erreur est survenue. Veuillez réessayer.';
        console.error(error);
      },
    });
  }
}