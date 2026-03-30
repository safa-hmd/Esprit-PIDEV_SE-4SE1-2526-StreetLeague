import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';

// Contrat Sponsor (frontoffice)
import { ContratListComponent } from '../components/contrat-sponsor/contrat-list.component';
import { ContratFormComponent } from '../components/contrat-sponsor/contrat-form.component';
import { ContratEditComponent } from '../components/contrat-sponsor/contrat-edit.component';

// Communauté
import { CommunauteListComponent } from '../components/communaute/communaute-list.component';
import { CommunauteFormComponent } from '../components/communaute/communaute-form.component';
import { CommunauteDetailComponent } from '../components/communaute/communaute-detail.component';
import { CommunauteEditComponent } from '../components/communaute/communaute-edit.component';

// Événement
import { EvenementListComponent } from '../components/evenement/evenement-list.component';
import { EvenementFormComponent } from '../components/evenement/evenement-form.component';
import { EvenementDetailComponent } from '../components/evenement/evenement-detail.component';
import { EvenementEditComponent } from '../components/evenement/evenement-edit.component';

// Sponsor (acteur Sponsor — CRUD complet)
import { SponsorListComponent } from '../components/sponsor/sponsor-list.component';
import { SponsorFormComponent } from '../components/sponsor/sponsor-form.component';
import { SponsorDetailComponent } from '../components/sponsor/sponsor-detail.component';
import { SponsorEditComponent } from '../components/sponsor/sponsor-edit.component';

// Sponsoring Événement (acteur Sponsor — CRUD complet)
import { SponsoringListComponent } from '../components/sponsoring-evenement/sponsoring-list.component';
import { SponsoringFormComponent } from '../components/sponsoring-evenement/sponsoring-form.component';
import { SponsoringDetailComponent } from '../components/sponsoring-evenement/sponsoring-detail.component';
import { SponsoringEditComponent } from '../components/sponsoring-evenement/sponsoring-edit.component';

const routes: Routes = [
  {
    path: '',
    component: FrontofficeComponent,
    children: [
      { path: '', redirectTo: 'communaute', pathMatch: 'full' },

      // Communauté routes
      { path: 'communaute', component: CommunauteListComponent },
      { path: 'communaute/new', component: CommunauteFormComponent },
      { path: 'communaute/:id', component: CommunauteDetailComponent },
      { path: 'communaute/:id/edit', component: CommunauteEditComponent },

      // Événement routes
      { path: 'evenement', component: EvenementListComponent },
      { path: 'evenement/new', component: EvenementFormComponent },
      { path: 'evenement/:id', component: EvenementDetailComponent },
      { path: 'evenement/:id/edit', component: EvenementEditComponent },

      // Sponsor routes (CRUD complet pour acteur Sponsor)
      { path: 'sponsor', component: SponsorListComponent },
      { path: 'sponsor/new', component: SponsorFormComponent },
      { path: 'sponsor/:id', component: SponsorDetailComponent },
      { path: 'sponsor/:id/edit', component: SponsorEditComponent },

      // Sponsoring Événement routes (CRUD complet pour acteur Sponsor)
      { path: 'sponsoring-evenement', component: SponsoringListComponent },
      { path: 'sponsoring-evenement/new', component: SponsoringFormComponent },
      { path: 'sponsoring-evenement/:id', component: SponsoringDetailComponent },
      { path: 'sponsoring-evenement/:id/edit', component: SponsoringEditComponent },

      // Contrat Sponsor routes (frontoffice)
      { path: 'contrat-sponsor', component: ContratListComponent },
      { path: 'contrat-sponsor/new', component: ContratFormComponent },
      { path: 'contrat-sponsor/:id/edit', component: ContratEditComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }
