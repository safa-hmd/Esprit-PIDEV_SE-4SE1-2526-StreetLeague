import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SponsoringListComponent } from './sponsoring-list.component';
import { SponsoringFormComponent } from './sponsoring-form.component';
import { SponsoringDetailComponent } from './sponsoring-detail.component';
import { SponsoringEditComponent } from './sponsoring-edit.component';

const routes: Routes = [
  { path: '', component: SponsoringListComponent },
  { path: 'new', component: SponsoringFormComponent },
  { path: ':id', component: SponsoringDetailComponent },
  { path: ':id/edit', component: SponsoringEditComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SponsoringEvenementRoutingModule { }
