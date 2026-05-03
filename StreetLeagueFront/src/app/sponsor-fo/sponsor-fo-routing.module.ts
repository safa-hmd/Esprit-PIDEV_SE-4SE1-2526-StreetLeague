import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SponsorHomeComponent } from './home/home.component';

const routes: Routes = [
  {
    path: '',
    component: SponsorHomeComponent
  },
  {
    path: 'sponsor',
    loadChildren: () => import('./sponsor/sponsor.module')
      .then(m => m.SponsorModule)
  },
  {
    path: 'contract-sponsor',
    loadChildren: () => import('./contract-sponsor/contract-sponsor.module')
      .then(m => m.ContractSponsorModule)
  },
  {
    path: 'sponsoring-evenement',
    loadChildren: () => import('./sponsoring-evenement/sponsoring-evenement.module')
      .then(m => m.SponsoringEvenementModule)
  },
  {
    path: 'evenement',
    loadChildren: () => import('./evenement/evenement.module')
      .then(m => m.EvenementModule)
  },
  {
    path: 'communaute',
    loadChildren: () => import('./communaute/communaute.module')
      .then(m => m.CommunityModule)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SponsorFORoutingModule { }
