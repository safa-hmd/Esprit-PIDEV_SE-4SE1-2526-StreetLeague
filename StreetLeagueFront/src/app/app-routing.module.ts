import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OAuth2CallbackComponent } from './auth/oauth2-callback/oauth2-callback.component';
import { SelectRoleComponent } from './auth/select-role/select-role.component';

const routes: Routes = [{ path: 'client', loadChildren: () => import('./frontoffice/frontoffice.module').then(m => m.FrontofficeModule) },
  
{ path: 'admin', loadChildren: () => import('./backoffice/backoffice.module').then(m => m.BackofficeModule) },
  
{ path: '', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  
{ path: 'coach', loadChildren: () => import('./coach-fo/coach-fo.module').then(m => m.CoachFOModule) }
  ,
  { path: 'oauth2/callback', component: OAuth2CallbackComponent },
  { path: 'oauth2/select-role', component: SelectRoleComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
