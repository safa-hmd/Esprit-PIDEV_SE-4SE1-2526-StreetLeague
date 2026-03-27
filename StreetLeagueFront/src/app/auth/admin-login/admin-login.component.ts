import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent {
  isLoading    = false;
  errorMessage = '';

  loginForm = new FormGroup({
    email:    new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isLoading    = true;
    this.errorMessage = '';

    this.authService.login({
      email:    this.loginForm.value.email!,
      password: this.loginForm.value.password!
    }).subscribe({
      next: (response) => {
        this.isLoading = false;

        // ✅ Vérifie que c'est bien un ADMIN
        if (response.role !== 'ROLE_ADMIN') {
          this.authService.logout();
          this.errorMessage = 'Accès refusé. Cette interface est réservée aux administrateurs.';
          return;
        }

        this.router.navigateByUrl('/admin');
      },
      error: () => {
        this.isLoading    = false;
        this.errorMessage = 'Email ou mot de passe incorrect.';
      }
    });
  }
}