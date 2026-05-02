import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SponsorListComponent } from './sponsor-list.component';
import { SponsorFormComponent } from './sponsor-form.component';
import { SponsorDetailComponent } from './sponsor-detail.component';
import { SponsorEditComponent } from './sponsor-edit.component';

const routes: Routes = [
  { path: '', component: SponsorListComponent },
  { path: 'new', component: SponsorFormComponent },
  { path: ':id', component: SponsorDetailComponent },
  { path: ':id/edit', component: SponsorEditComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SponsorRoutingModule { }
