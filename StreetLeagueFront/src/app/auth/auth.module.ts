import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AuthRoutingModule } from './auth-routing.module';
import { AuthComponent } from './auth.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

// ── Composants de feature/match-team-training ──────────────
import { SelectRoleComponent } from './select-role/select-role.component';
import { OAuth2CallbackComponent } from './oauth2-callback/oauth2-callback.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AdminLoginComponent } from './admin-login/admin-login.component';

@NgModule({
  declarations: [
    AuthComponent,
    LoginComponent,
    RegisterComponent,
    // ── feature/match-team-training ───────────────────────
    SelectRoleComponent,
    OAuth2CallbackComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    AdminLoginComponent,
  ],
  imports: [
    CommonModule,
    AuthRoutingModule,
    ReactiveFormsModule,  // ← requis pour [formGroup]
    FormsModule,
  ]
})
export class AuthModule { }