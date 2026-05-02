import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminGuard } from './guards/admin.guard';
import { AuthGuard } from './guards/auth.guard';
import { NoAuthGuard } from './guards/no-auth.guard';
import { SponsorGuard } from './guards/sponsor.guard';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  {
    path: 'admin',
    canActivate: [AdminGuard],
    loadChildren: () => import('./backoffice/backoffice.module')
      .then(m => m.BackofficeModule)
  },
  {
    path: 'sponsor',
    canActivate: [SponsorGuard],
    loadChildren: () => import('./sponsor-fo/sponsor-fo.module')
      .then(m => m.SponsorFOModule)
  },
  {
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
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }