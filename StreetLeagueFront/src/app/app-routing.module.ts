import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OAuth2CallbackComponent } from './auth/oauth2-callback/oauth2-callback.component';
import { SelectRoleComponent } from './auth/select-role/select-role.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { AdminLoginComponent } from './auth/admin-login/admin-login.component';
import { AdminGuard } from './guards/admin.guard';
import { AuthGuard } from './guards/auth.guard';
import { CoachGuard } from './guards/coach.guard';
import { NoAuthGuard } from './guards/no-auth.guard';
import { PlayerGuard } from './guards/player.guard';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  {
    path: 'client',
    canActivate: [PlayerGuard],      // ← bloque si non connecté
    loadChildren: () => import('./frontoffice/frontoffice.module')
      .then(m => m.FrontofficeModule)
  },
  {
    path: 'admin',
    canActivate: [AdminGuard],       // ← bloque si non ADMIN
    loadChildren: () => import('./backoffice/backoffice.module')
      .then(m => m.BackofficeModule)
  },
  {
    path: 'coach',
    canActivate: [CoachGuard],       // ← bloque si non COACH
    loadChildren: () => import('./coach-fo/coach-fo.module')
      .then(m => m.CoachFOModule)
  },
  {
    path: '',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  { path: 'oauth2/callback',    component: OAuth2CallbackComponent },
  { path: 'oauth2/select-role', component: SelectRoleComponent },
  {
    path: 'forgot-password',
    canActivate: [NoAuthGuard],      // ← optionnel
    component: ForgotPasswordComponent
  },
  { path: 'reset-password', component: ResetPasswordComponent },
  {
    path: 'admin-login',
    canActivate: [NoAuthGuard],      // ← bloque si déjà connecté
    component: AdminLoginComponent
  },

    { path: '**',                 component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }