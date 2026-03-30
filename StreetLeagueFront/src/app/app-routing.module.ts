import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OAuth2CallbackComponent } from './auth/oauth2-callback/oauth2-callback.component';
import { SelectRoleComponent } from './auth/select-role/select-role.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { AdminLoginComponent } from './auth/admin-login/admin-login.component';
import { AdminGuard } from './guards/admin.guard';
import { CoachGuard } from './guards/coach.guard';
import { NoAuthGuard } from './guards/no-auth.guard';
import { PlayerGuard } from './guards/player.guard';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  // ── Front office (joueurs) ──────────────────────────────
  {
    path: 'client',
    canActivate: [PlayerGuard],
    loadChildren: () => import('./frontoffice/frontoffice.module')
      .then(m => m.FrontofficeModule)
  },

  // ── Back office (admin) ─────────────────────────────────
  {
    path: 'admin',
    canActivate: [AdminGuard],
    loadChildren: () => import('./backoffice/backoffice.module')
      .then(m => m.BackofficeModule)
  },

  // ── Coach ───────────────────────────────────────────────
  {
    path: 'coach',
    canActivate: [CoachGuard],
    loadChildren: () => import('./coach-fo/coach-fo.module')
      .then(m => m.CoachFOModule)
  },

  // ── Delivery ────────────────────────────────────────────
  {
    path: 'delivery',
    loadChildren: () => import('./delivery-fo/delivery-fo.module')
      .then(m => m.DeliveryFoModule)
  },

  // ── Auth ────────────────────────────────────────────────
  {
    path: '',
    canActivate: [NoAuthGuard],
    loadChildren: () => import('./auth/auth.module')
      .then(m => m.AuthModule)
  },

  // ── Misc auth routes ────────────────────────────────────
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

  // ── 404 ─────────────────────────────────────────────────
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }