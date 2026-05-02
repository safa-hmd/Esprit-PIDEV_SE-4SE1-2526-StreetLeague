import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminGuard } from './guards/admin.guard';
<<<<<<< HEAD
import { AuthGuard } from './guards/auth.guard';
=======
import { CoachGuard } from './guards/coach.guard';
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
import { NoAuthGuard } from './guards/no-auth.guard';
import { SponsorGuard } from './guards/sponsor.guard';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  // ── Front office (joueurs) ──────────────────────────────
  {
<<<<<<< HEAD
=======
    path: 'client',
    canActivate: [PlayerGuard],
    loadChildren: () => import('./frontoffice/frontoffice.module')
      .then(m => m.FrontofficeModule)
  },

  // ── Back office (admin) ─────────────────────────────────
  {
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
    path: 'admin',
    canActivate: [AdminGuard],
    loadChildren: () => import('./backoffice/backoffice.module')
      .then(m => m.BackofficeModule)
  },

  // ── Coach ───────────────────────────────────────────────
  {
<<<<<<< HEAD
    path: 'sponsor',
    canActivate: [SponsorGuard],
    loadChildren: () => import('./sponsor-fo/sponsor-fo.module')
      .then(m => m.SponsorFOModule)
=======
    path: 'coach',
    canActivate: [CoachGuard],
    loadChildren: () => import('./coach-fo/coach-fo.module')
      .then(m => m.CoachFOModule)
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
  },

  // ── Delivery ────────────────────────────────────────────
  {
<<<<<<< HEAD
    path: 'client',
    canActivate: [AuthGuard],
    loadChildren: () => import('./frontoffice/frontoffice.module')
      .then(m => m.FrontofficeModule)
  },
  {
    path: '',
    canActivate: [NoAuthGuard],
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
=======
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
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }