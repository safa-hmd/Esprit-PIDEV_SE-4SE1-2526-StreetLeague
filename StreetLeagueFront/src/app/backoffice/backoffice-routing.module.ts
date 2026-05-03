import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';
import { AdvancedDashboardComponent } from './dashboard/advanced-dashboard.component';

import { HealthComponent } from './health/health.component';
import { TransportComponent } from './transport/transport.component';
import { AccommodationComponent } from './accommodation/accommodation.component';

import { NewsComponent } from './news/news.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';

// Admin Community
import { AdminCommunauteListComponent } from './communaute/admin-communaute-list.component';
import { AdminCommunauteFormComponent } from './communaute/admin-communaute-form.component';
import { AdminCommunauteEditComponent } from './communaute/admin-communaute-edit.component';

// Admin Event — CRUD complet admin
import { AdminEvenementListComponent } from './evenement/admin-evenement-list.component';
import { AdminEvenementFormComponent } from './evenement/admin-evenement-form.component';
import { AdminEvenementEditComponent } from './evenement/admin-evenement-edit.component';

// Admin Sponsor — CRUD complet
import { AdminSponsorListComponent } from './sponsor/admin-sponsor-list.component';
import { AdminSponsorDetailComponent } from './sponsor/admin-sponsor-detail.component';
import { AdminSponsorFormComponent } from './sponsor/admin-sponsor-form.component';
import { AdminSponsorEditComponent } from './sponsor/admin-sponsor-edit.component';

// Admin Sponsoring Event — CRUD complet
import { AdminSponsoringEvenementListComponent } from './sponsoring-evenement/admin-sponsoring-evenement-list.component';
import { AdminSponsoringEvenementDetailComponent } from './sponsoring-evenement/admin-sponsoring-evenement-detail.component';
import { AdminSponsoringEvenementFormComponent } from './sponsoring-evenement/admin-sponsoring-evenement-form.component';
import { AdminSponsoringEvenementEditComponent } from './sponsoring-evenement/admin-sponsoring-evenement-edit.component';

// Admin Contract Sponsor (inchangé)
import { AdminContractSponsorListComponent } from './contract-sponsor/admin-contract-sponsor-list.component';
import { AdminContractSponsorFormComponent } from './contract-sponsor/admin-contract-sponsor-form.component';
import { AdminContractSponsorEditComponent } from './contract-sponsor/admin-contract-sponsor-edit.component';

const routes: Routes = [
  {
    path: '',
    component: BackofficeComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdvancedDashboardComponent },

      // Community — CRUD complet admin
      { path: 'communaute', component: AdminCommunauteListComponent },
      { path: 'communaute/new', component: AdminCommunauteFormComponent },
      { path: 'communaute/:id/edit', component: AdminCommunauteEditComponent },

      // Event — CRUD complet admin
      { path: 'evenement', component: AdminEvenementListComponent },
      { path: 'evenement/new', component: AdminEvenementFormComponent },
      { path: 'evenement/:id/edit', component: AdminEvenementEditComponent },


      // Sponsoring Event — CRUD complet admin
      { path: 'sponsoring-evenement', component: AdminSponsoringEvenementListComponent },
      { path: 'sponsoring-evenement/new', component: AdminSponsoringEvenementFormComponent },
      { path: 'sponsoring-evenement/:id', component: AdminSponsoringEvenementDetailComponent },
      { path: 'sponsoring-evenement/:id/edit', component: AdminSponsoringEvenementEditComponent },

      // Contract Sponsor — CRUD complet admin (inchangé)
      { path: 'contract-sponsor', component: AdminContractSponsorListComponent },
      { path: 'contract-sponsor/new', component: AdminContractSponsorFormComponent },
      { path: 'contract-sponsor/:id/edit', component: AdminContractSponsorEditComponent },

      // Sponsor — CRUD complet admin
      { path: 'sponsor', component: AdminSponsorListComponent },
      { path: 'sponsor/new', component: AdminSponsorFormComponent },
      { path: 'sponsor/:id', component: AdminSponsorDetailComponent },
      { path: 'sponsor/:id/edit', component: AdminSponsorEditComponent },

      // Services
      { path: 'health', component: HealthComponent },
      { path: 'transport', component: TransportComponent },
      { path: 'accommodation', component: AccommodationComponent },

      // Other features
      { path: 'news', component: NewsComponent },
      { path: 'field-reservation', component: FieldReservationComponent },
      { path: 'tournaments', component: TournamentComponent },
      { path: 'teamAdmin', component: ListTeamsComponent },
      { path: 'trainingAdmin', component: ListTrainingComponent }
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }
