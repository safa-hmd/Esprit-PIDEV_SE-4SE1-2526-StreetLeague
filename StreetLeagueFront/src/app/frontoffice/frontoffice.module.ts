import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
import { FooterComponent } from './footer/footer.component';
import { NavbarComponent } from './navbar/navbar.component';

import { CommunauteModule } from '../components/communaute/communaute.module';
import { EvenementModule } from '../components/evenement/evenement.module';
import { SponsorModule } from '../components/sponsor/sponsor.module';
import { SponsoringEvenementModule } from '../components/sponsoring-evenement/sponsoring-evenement.module';
import { ContratSponsorModule } from '../components/contrat-sponsor/contrat-sponsor.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TeamComponent } from './team/team.component';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentsComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';

@NgModule({
  declarations: [
    FrontofficeComponent,
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
  ],
  imports: [
    CommonModule,
    RouterModule,
    FrontofficeRoutingModule,
    CommunauteModule,
    EvenementModule,
    SponsorModule,
    SponsoringEvenementModule,
    ContratSponsorModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule, 
  
  ]
})
export class FrontofficeModule { }
