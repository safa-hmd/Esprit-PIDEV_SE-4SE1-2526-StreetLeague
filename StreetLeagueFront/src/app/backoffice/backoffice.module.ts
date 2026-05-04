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
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { TransportComponent } from './transport/transport.component';
import { AccommodationComponent } from './accommodation/accommodation.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { AdminEvenementModule } from './evenement/admin-evenement.module';
import { AdminSponsorModule } from './sponsor/admin-sponsor.module';
import { AdminSponsoringEvenementModule } from './sponsoring-evenement/admin-sponsoring-evenement.module';
import { AdminContractSponsorModule } from './contract-sponsor/admin-contract-sponsor.module';
import { HealthComponent } from './health/health.component';
import { NewsComponent } from './news/news.component';
import { AdminCommunauteModule } from './communaute/admin-communaute.module';
import { HomeComponent } from './home/home.component';
import { CommandeListComponent } from './commande/commande-list/commande-list.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';
import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { BracketComponent } from './bracket/bracket.component';

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
    ListTrainingComponent,
    HomeComponent,
        LivraisonListComponent,
    LivraisonFormComponent,
    TransporteurListComponent,
    MaterielListComponent,
    CommandeListComponent,
    PlayerProfileComponent,
    BracketComponent
  ],

  imports: [                          // ✅ MANQUAIT COMPLÈTEMENT
    CommonModule,                     // ✅ ngClass, date, number, currency, lowercase
    FormsModule,                      // ✅ ngModel
    ReactiveFormsModule,              // ✅ formGroup, formControl
    HttpClientModule,                 // ✅ HttpClient
    RouterModule,                     // ✅ routerLink
    BackofficeRoutingModule,          // ✅ routing

    AdminCommunauteModule,
    AdminEvenementModule,
    AdminSponsorModule,
    AdminSponsoringEvenementModule,
    AdminContractSponsorModule,

  ]
})
export class BackofficeModule { }
