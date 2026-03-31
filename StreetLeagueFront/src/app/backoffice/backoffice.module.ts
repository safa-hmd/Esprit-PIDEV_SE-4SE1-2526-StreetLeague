import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { HomeComponent } from './home/home.component';
import { TransportComponent } from './transport/transport.component';
import { AccommodationComponent } from './accommodation/accommodation.component';
import { CoachTravelComponent } from './coach-travel/coach-travel.component';
import { AccommodationRequestsComponent } from './accommodation-requests/accommodation-requests.component';
import { TransportManagementComponent } from './transport-management/transport-management.component';
import { TravelRequestsComponent } from './travel-requests/travel-requests.component';
import { AdminTransportRequestsComponent } from './transport-requests/admin-transport-requests.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { RouterModule } from '@angular/router';
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component';
import { CommandeListComponent } from './commande/commande-list/commande-list.component';
import { AdminEvenementModule } from '../components/admin-evenement/admin-evenement.module';
import { AdminSponsorModule } from '../components/admin-sponsor/admin-sponsor.module';
import { AdminSponsoringEvenementModule } from '../components/admin-sponsoring-evenement/admin-sponsoring-evenement.module';
import { AdminContratSponsorModule } from '../components/admin-contrat-sponsor/admin-contrat-sponsor.module';
import { AdminCommunauteModule } from '../components/admin-communaute/admin-communaute.module';

@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    ListTeamsComponent,
    ListTrainingComponent,
    HomeComponent,
    PlayerProfileComponent,
    TournamentComponent,
    FieldReservationComponent,
    TransportComponent,
    AccommodationComponent,
    CoachTravelComponent,
    AccommodationRequestsComponent,
    TransportManagementComponent,
    TravelRequestsComponent,
    AdminTransportRequestsComponent,


    LivraisonListComponent,
    LivraisonFormComponent,
    TransporteurListComponent,
    MaterielListComponent,
    CommandeListComponent,





  ],
  imports: [
    CommonModule,
    RouterModule,
    BackofficeRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,

  ]
})
export class BackofficeModule { }
