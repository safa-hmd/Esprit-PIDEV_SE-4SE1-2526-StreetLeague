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
import { HomeComponent } from './home/home.component';
import { CommandeListComponent } from './commande/commande-list/commande-list.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';

import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { BracketComponent } from './bracket/bracket.component';

import { DashboardComponent } from './dashboard/dashboard.component';


const routes: Routes = [
  {
    path: '',
    component: BackofficeComponent,
    children: [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

      { path: 'home', component: HomeComponent},

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
      { path: 'trainingAdmin', component: ListTrainingComponent },




      
       { path: 'materiels', component: MaterielListComponent },         
      { path: 'livraisons', component: LivraisonListComponent },
      { path: 'livraisons/new', component: LivraisonFormComponent },
      { path: 'transporteurs', component: TransporteurListComponent },
      { path: 'commandes', component: CommandeListComponent },


      { path: 'profile', component: PlayerProfileComponent },
      { path: 'tournaments/:id/bracket', component: BracketComponent },

    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }
