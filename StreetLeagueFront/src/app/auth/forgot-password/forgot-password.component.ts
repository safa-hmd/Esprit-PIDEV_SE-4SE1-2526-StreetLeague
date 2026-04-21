import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  isLoading   = false;
  successMsg  = '';
  errorMsg    = '';

  form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email])
  });

  constructor(private authService: AuthService) {}

  onSubmit() {
    if (this.form.invalid) return;
    this.isLoading  = true;
    this.successMsg = '';
    this.errorMsg   = '';

    this.authService.forgotPassword(this.form.value.email!).subscribe({
      next: () => {
        this.isLoading  = false;
        this.successMsg = 'Un email de réinitialisation a été envoyé à votre adresse.';
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg  = 'Aucun compte trouvé avec cet email.';
      }
    });
  }
}