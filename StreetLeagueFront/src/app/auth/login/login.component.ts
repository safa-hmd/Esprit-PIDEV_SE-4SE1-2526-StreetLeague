import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

type Role = 'PLAYER' |  'COACH' | 'SPONSOR' | 'DELIVERY';

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

        // ✅ Sauvegarde sans JSON.stringify
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        localStorage.setItem('RoleUserConnect',  response.role);

        // ✅ Redirection selon le rôle renvoyé par le BACKEND (pas selectedRole)
        this.redirectByRole(response.role);
      },
      error: (error) => {
        this.isLoading    = false;
        this.errorMessage = 'Email ou mot de passe incorrect.';
        console.error(error);
      },
    });
  }

  private redirectByRole(role: string) {
    switch (role) {
      case 'ROLE_COACH':
        this.router.navigateByUrl('/coach');   
        break;
      case 'SPONSOR':
        this.router.navigateByUrl('/client');   
        break;
      case 'DELIVERY':
        this.router.navigateByUrl('/client');   
        break;
      case 'PLAYER':
      default:
        this.router.navigateByUrl('/client');
        break;
    }
  }
  loginWithGoogle(): void {
  this.authService.loginWithGoogle();
}
}