import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SponsorEvenementListComponent } from './evenement-list.component';
import { SponsorEvenementDetailComponent } from './evenement-detail.component';

const routes: Routes = [
  {
    path: '',
    component: SponsorEvenementListComponent
  },
  {
    path: ':id',
    component: SponsorEvenementDetailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EvenementRoutingModule { }
