import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { AdvancedDashboardComponent } from './dashboard/advanced-dashboard.component';

import { AdminCommunauteModule } from './communaute/admin-communaute.module';
import { AdminEvenementModule } from './evenement/admin-evenement.module';
import { AdminSponsorModule } from './sponsor/admin-sponsor.module';
import { AdminSponsoringEvenementModule } from './sponsoring-evenement/admin-sponsoring-evenement.module';
import { AdminContractSponsorModule } from './contract-sponsor/admin-contract-sponsor.module';

import { HealthComponent } from './health/health.component';
import { TransportComponent } from './transport/transport.component';
import { AccommodationComponent } from './accommodation/accommodation.component';

import { NewsComponent } from './news/news.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';

@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    AdvancedDashboardComponent,
    HealthComponent,
    TransportComponent,
    AccommodationComponent,
    NewsComponent,
    FieldReservationComponent,
    TournamentComponent,
    ListTeamsComponent,
    ListTrainingComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule,
    BackofficeRoutingModule,
    AdminCommunauteModule,
    AdminEvenementModule,
    AdminSponsorModule,
    AdminSponsoringEvenementModule,
    AdminContractSponsorModule,
  ]
})
export class BackofficeModule { }
