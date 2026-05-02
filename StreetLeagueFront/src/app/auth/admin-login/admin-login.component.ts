import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css'],
  // standalone: true,
  //  imports: [ReactiveFormsModule] 
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
<<<<<<< HEAD
      error: (error) => {
        this.isLoading = false;
        const body = error?.error as Record<string, unknown> | undefined;
        const err = body?.['error'];
        const msg = body?.['message'];
        this.errorMessage =
          (typeof err === 'string' ? err : null) ||
          (typeof msg === 'string' ? msg : null) ||
          'Email ou password incorrect.';
=======
      error: () => {
        this.isLoading    = false;
        this.errorMessage = 'Email ou mot de passe incorrect.';
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
      }
    });
  }
}