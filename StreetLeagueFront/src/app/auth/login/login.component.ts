import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

type Role = 'PLAYER' | 'COACH' | 'SPONSOR' | 'DELIVERY';

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
};

private emailPlaceholders: Record<Role, string> = {
  PLAYER:   'player@streetleague.com',
  COACH:    'coach@streetleague.com',
  SPONSOR:  'sponsor@streetleague.com',
  DELIVERY: 'delivery@streetleague.com',
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
 console.log('ROLE RECU DU BACKEND:', response.role);
        // ✅ Sauvegarde sans JSON.stringify
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        localStorage.setItem('RoleUserConnect',  response.role);

        // ✅ Redirection selon le rôle renvoyé par le BACKEND (pas selectedRole)
        this.redirectAfterLogin(response.role);
      },
      error: (error) => {
        this.isLoading = false;
        const body = error?.error as Record<string, unknown> | undefined;
        const err = body?.['error'];
        const msg = body?.['message'];
        this.errorMessage =
          (typeof err === 'string' ? err : null) ||
          (typeof msg === 'string' ? msg : null) ||
          'Email ou password incorrect.';
        console.error(error);
      },
    });
  }

  /** Après login réussi : admin → /admin, coach → /coach, sino espace client */
  private redirectAfterLogin(roleFromBackend: string) {
    const role = this.authService.normalizeRole(roleFromBackend);
    switch (role) {
      case 'ROLE_ADMIN':
        this.router.navigateByUrl('/admin');
        break;
      case 'ROLE_SPONSOR':
        this.router.navigateByUrl('/sponsor');
        break;
      case 'ROLE_COACH':
      case 'ROLE_PLAYER':
      case 'ROLE_DELIVERY':
      default:
        this.router.navigateByUrl('/client');
        break;
    }
  }
  loginWithGoogle(): void {
  this.authService.loginWithGoogle();
}
}