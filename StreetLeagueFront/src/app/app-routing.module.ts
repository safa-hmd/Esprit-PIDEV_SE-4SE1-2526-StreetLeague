import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [{ path: 'client', loadChildren: () => import('./frontoffice/frontoffice.module').then(m => m.FrontofficeModule) },
  
{ path: 'admin', loadChildren: () => import('./backoffice/backoffice.module').then(m => m.BackofficeModule) },
  
{ path: '', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  
{ path: 'coach', loadChildren: () => import('./coach-fo/coach-fo.module').then(m => m.CoachFOModule) },
{ path: 'delivery', loadChildren: () => import('./delivery-fo/delivery-fo.module').then(m => m.DeliveryFoModule) },
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
