import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SponsorCommunauteListComponent } from './communaute-list.component';
import { SponsorCommunauteDetailComponent } from './communaute-detail.component';

const routes: Routes = [
  {
    path: '',
    component: SponsorCommunauteListComponent
  },
  {
    path: ':id',
    component: SponsorCommunauteDetailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CommunauteRoutingModule { }
