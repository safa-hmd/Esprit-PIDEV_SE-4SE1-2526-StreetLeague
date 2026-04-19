import { CommonModule } from '@angular/common';

import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
import { FooterComponent } from './footer/footer.component';
import { TeamComponent } from './team/team.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { NavbarComponent } from './navbar/navbar.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';

import { MatchHistoryComponent } from './match-history/match-history.component';
import { ScheduleComponent } from './schedule/schedule.component';

import { NgModule, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

registerLocaleData(localeFr);  // ← ajoute avant @NgModule

@NgModule({
  declarations: [
    FrontofficeComponent,
    FooterComponent,
    TeamComponent,
    TrainingComponent,
    DetailMatchComponent,
    HomeComponent,
    DetailTeamComponent,
    NavbarComponent,
    PlayerProfileComponent,
    MatchHistoryComponent,
    ScheduleComponent,
  ],
  imports: [
    CommonModule,
    FrontofficeRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule, 
    CommonModule,
  ]
  ,
      providers: [
         { provide: LOCALE_ID, useValue: 'fr' }  
      ]
})
export class FrontofficeModule { }
