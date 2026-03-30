import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';

import { AdminCommunauteModule } from '../components/admin-communaute/admin-communaute.module';
import { AdminEvenementModule } from '../components/admin-evenement/admin-evenement.module';
import { AdminSponsorModule } from '../components/admin-sponsor/admin-sponsor.module';
import { AdminSponsoringEvenementModule } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement.module';
import { AdminContratSponsorModule } from '../components/admin-contrat-sponsor/admin-contrat-sponsor.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HomeComponent } from './home/home.component';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    HomeComponent,
    ListTeamsComponent,
    ListTrainingComponent,
    PlayerProfileComponent,
    TournamentComponent,
    FieldReservationComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    BackofficeRoutingModule,
    AdminCommunauteModule,
    AdminEvenementModule,
    AdminSponsorModule,
    AdminSponsoringEvenementModule,
    AdminContratSponsorModule,
    ReactiveFormsModule,

    FormsModule,

    HttpClientModule,
  ]
})
export class BackofficeModule {  }
