import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  isLoading  = false;
  successMsg = '';
  errorMsg   = '';
  token      = '';

  form = new FormGroup({
    newPassword:     new FormControl('', [Validators.required, Validators.minLength(6)]),
    confirmPassword: new FormControl('', [Validators.required])
  }, { validators: this.passwordMatchValidator });

  constructor(
    private route:       ActivatedRoute,
    private router:      Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.errorMsg = 'Lien invalide ou expiré.';
    }
  }

  passwordMatchValidator(group: any) {
    const p1 = group.get('newPassword')?.value;
    const p2 = group.get('confirmPassword')?.value;
    return p1 === p2 ? null : { mismatch: true };
  }

  onSubmit() {
    if (this.form.invalid || !this.token) return;
    this.isLoading = true;
    this.errorMsg  = '';

    this.authService.resetPassword(this.token, this.form.value.newPassword!).subscribe({
      next: () => {
        this.isLoading  = false;
        this.successMsg = 'Mot de passe modifié ! Redirection...';
        setTimeout(() => this.router.navigateByUrl('/login'), 2500);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMsg  = err.status === 400
          ? 'Lien expiré ou invalide. Veuillez recommencer.'
          : 'Une erreur est survenue.';
      }
    });
  }
}