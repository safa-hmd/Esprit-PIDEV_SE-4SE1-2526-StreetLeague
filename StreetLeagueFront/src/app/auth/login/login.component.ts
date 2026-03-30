import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

type Role = 'PLAYER' | 'COACH' | 'SPONSOR' | 'DELIVERY' | 'ADMIN';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  selectedRole: Role = 'PLAYER';
  isLoading    = false;
  errorMessage = '';

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

  loginForm = new FormGroup({
    email:    new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  selectRole(role: Role) {
    this.selectedRole = role;
    this.errorMessage = '';
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    const email    = this.loginForm.value.email!;
    const password = this.loginForm.value.password!;

    this.isLoading    = true;
    this.errorMessage = '';

    this.authService.login({ email, password }).subscribe({
      next: (response) => {
        this.isLoading = false;

        // Le tap() d'AuthService.login a déjà enregistré token / email / rôle (rôle peut venir du JWT)
        const rawRole =
          (response.role && String(response.role).trim()) ||
          this.authService.readRoleFromJwt(response.token) ||
          '';
        this.redirectAfterLogin(rawRole);
      },
      error: (error) => {
        this.isLoading = false;
        const body = error?.error as Record<string, unknown> | undefined;
        const err = body?.['error'];
        const msg = body?.['message'];
        this.errorMessage =
          (typeof err === 'string' ? err : null) ||
          (typeof msg === 'string' ? msg : null) ||
          'Email ou mot de passe incorrect.';
        console.error(error);
      },
    });
  }

  /** Après login réussi : admin → /admin, coach → /coach, sinon espace client */
  private redirectAfterLogin(roleFromBackend: string) {
    const role = this.authService.normalizeRole(roleFromBackend);
    if (role === 'ROLE_ADMIN') {
      this.router.navigateByUrl('/admin');
      return;
    }
    if (role === 'ROLE_COACH') {
      this.router.navigateByUrl('/coach');
      return;
    }
    this.router.navigateByUrl('/client');
  }
  loginWithGoogle(): void {
  this.authService.loginWithGoogle();
}
}