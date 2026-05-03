import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthComponent } from './auth.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { OAuth2CallbackComponent } from './oauth2-callback/oauth2-callback.component';
import { SelectRoleComponent } from './select-role/select-role.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AdminLoginComponent } from './admin-login/admin-login.component';
import { NoAuthGuard } from '../guards/no-auth.guard';

const routes: Routes = [
  {
    path: '',
    component: AuthComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'login' },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
    ],
  },
  { path: 'oauth2/callback',    component: OAuth2CallbackComponent },
  { path: 'oauth2/select-role', component: SelectRoleComponent },
  {
    path: 'forgot-password',
    canActivate: [NoAuthGuard],
    component: ForgotPasswordComponent
  },
  { path: 'reset-password', component: ResetPasswordComponent },
  {
    path: 'admin-login',
    canActivate: [NoAuthGuard],
    component: AdminLoginComponent
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
