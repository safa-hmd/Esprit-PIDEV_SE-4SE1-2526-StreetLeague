import { ContratListComponent } from '../components/contrat-sponsor/contrat-list.component';
import { ContratFormComponent } from '../components/contrat-sponsor/contrat-form.component';
import { ContratEditComponent } from '../components/contrat-sponsor/contrat-edit.component';

import { CommunauteListComponent } from '../components/communaute/communaute-list.component';
import { CommunauteFormComponent } from '../components/communaute/communaute-form.component';
import { CommunauteDetailComponent } from '../components/communaute/communaute-detail.component';
import { CommunauteEditComponent } from '../components/communaute/communaute-edit.component';

import { EvenementListComponent } from '../components/evenement/evenement-list.component';
import { EvenementFormComponent } from '../components/evenement/evenement-form.component';
import { EvenementDetailComponent } from '../components/evenement/evenement-detail.component';
import { EvenementEditComponent } from '../components/evenement/evenement-edit.component';

import { SponsorListComponent } from '../components/sponsor/sponsor-list.component';
import { SponsorFormComponent } from '../components/sponsor/sponsor-form.component';
import { SponsorDetailComponent } from '../components/sponsor/sponsor-detail.component';
import { SponsorEditComponent } from '../components/sponsor/sponsor-edit.component';

import { SponsoringListComponent } from '../components/sponsoring-evenement/sponsoring-list.component';
import { SponsoringFormComponent } from '../components/sponsoring-evenement/sponsoring-form.component';
import { SponsoringDetailComponent } from '../components/sponsoring-evenement/sponsoring-detail.component';
import { SponsoringEditComponent } from '../components/sponsoring-evenement/sponsoring-edit.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { RouterModule } from '@angular/router';
 
import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
 
import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { CartComponent } from './shop/cart/cart.component';
 
// Si tu as déjà un navbar et footer composant, importe-les ici aussi
 import { NavbarComponent } from './navbar/navbar.component';
 import { FooterComponent } from './footer/footer.component';
import { TeamComponent } from './team/team.component';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentsComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { HttpClientModule } from '@angular/common/http';

import { NewsComponent } from './news/news.component';
import { HealthComponent } from './health/health.component';
import { BracketComponent } from './bracket/bracket.component';
 
@NgModule({
  declarations: [
    FrontofficeComponent,
    ShopListComponent,
    CartComponent,
     NavbarComponent,
     FooterComponent,
    TeamComponent,
    TrainingComponent,
    DetailMatchComponent,
    HomeComponent,
    DetailTeamComponent,
    PlayerProfileComponent,
    TournamentsComponent,
    FieldReservationComponent,



      CommunauteListComponent,
  CommunauteFormComponent,
  CommunauteDetailComponent,
  CommunauteEditComponent,

  EvenementListComponent,
  EvenementFormComponent,
  EvenementDetailComponent,
  EvenementEditComponent,

  SponsorListComponent,
  SponsorFormComponent,
  SponsorDetailComponent,
  SponsorEditComponent,

  SponsoringListComponent,
  SponsoringFormComponent,
  SponsoringDetailComponent,
  SponsoringEditComponent,

  ContratListComponent,
  ContratFormComponent,
  ContratEditComponent,

     NewsComponent,
    HealthComponent,
    BracketComponent,
  ],
  imports: [
      CommonModule,
    FrontofficeRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
  ]
})
export class FrontofficeModule {}
 

