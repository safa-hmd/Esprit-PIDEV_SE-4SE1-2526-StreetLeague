import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';

// Admin Communauté
import { AdminCommunauteListComponent } from '../components/admin-communaute/admin-communaute-list.component';
import { AdminCommunauteFormComponent } from '../components/admin-communaute/admin-communaute-form.component';
import { AdminCommunauteEditComponent } from '../components/admin-communaute/admin-communaute-edit.component';

// Admin Événement — CRUD complet admin
import { AdminEvenementListComponent } from '../components/admin-evenement/admin-evenement-list.component';
import { AdminEvenementFormComponent } from '../components/admin-evenement/admin-evenement-form.component';
import { AdminEvenementEditComponent } from '../components/admin-evenement/admin-evenement-edit.component';

// Admin Sponsor — CRUD complet
import { AdminSponsorListComponent } from '../components/admin-sponsor/admin-sponsor-list.component';
import { AdminSponsorDetailComponent } from '../components/admin-sponsor/admin-sponsor-detail.component';
import { AdminSponsorFormComponent } from '../components/admin-sponsor/admin-sponsor-form.component';
import { AdminSponsorEditComponent } from '../components/admin-sponsor/admin-sponsor-edit.component';

// Admin Sponsoring Événement — CRUD complet
import { AdminSponsoringEvenementListComponent } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement-list.component';
import { AdminSponsoringEvenementDetailComponent } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement-detail.component';
import { AdminSponsoringEvenementFormComponent } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement-form.component';
import { AdminSponsoringEvenementEditComponent } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement-edit.component';

// Admin Contrat Sponsor (inchangé)
import { AdminContratSponsorListComponent } from '../components/admin-contrat-sponsor/admin-contrat-sponsor-list.component';
import { AdminContratSponsorFormComponent } from '../components/admin-contrat-sponsor/admin-contrat-sponsor-form.component';
import { AdminContratSponsorEditComponent } from '../components/admin-contrat-sponsor/admin-contrat-sponsor-edit.component';
import { HomeComponent } from './home/home.component';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';

const routes: Routes = [
  {
    path: '',
    component: BackofficeComponent,
    children: [
     



           { path: '', redirectTo: 'home', pathMatch: 'full' },
      {path: 'home', component: HomeComponent},
      { path: 'teamAdmin', component: ListTeamsComponent},
      {path: 'trainingAdmin', component: ListTrainingComponent},
      { path: 'profile', component: PlayerProfileComponent },
      { path: 'tournaments', component: TournamentComponent },
      { path: 'field-reservation', component: FieldReservationComponent },

 { path: 'communaute', component:AdminCommunauteFormComponent },
      // Communauté — CRUD complet admin
      { path: 'communaute', component: AdminCommunauteListComponent },
      { path: 'communaute/new', component: AdminCommunauteFormComponent },
      { path: 'communaute/:id/edit', component: AdminCommunauteEditComponent },

      // Événement — CRUD complet admin
      { path: 'evenement', component: AdminEvenementListComponent },
      { path: 'evenement/new', component: AdminEvenementFormComponent },
      { path: 'evenement/:id/edit', component: AdminEvenementEditComponent },

      // Sponsor — CRUD complet admin
      { path: 'sponsor', component: AdminSponsorListComponent },
      { path: 'sponsor/new', component: AdminSponsorFormComponent },
      { path: 'sponsor/:id', component: AdminSponsorDetailComponent },
      { path: 'sponsor/:id/edit', component: AdminSponsorEditComponent },

      // Sponsoring Événement — CRUD complet admin
      { path: 'sponsoring-evenement', component: AdminSponsoringEvenementListComponent },
      { path: 'sponsoring-evenement/new', component: AdminSponsoringEvenementFormComponent },
      { path: 'sponsoring-evenement/:id', component: AdminSponsoringEvenementDetailComponent },
      { path: 'sponsoring-evenement/:id/edit', component: AdminSponsoringEvenementEditComponent },

      // Contrat Sponsor — CRUD complet admin (inchangé)
      { path: 'contrat-sponsor', component: AdminContratSponsorListComponent },
      { path: 'contrat-sponsor/new', component: AdminContratSponsorFormComponent },
      { path: 'contrat-sponsor/:id/edit', component: AdminContratSponsorEditComponent },

  
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }

