import { CommonModule, DatePipe } from '@angular/common';

import { CoachFORoutingModule } from './coach-fo-routing.module';
import { CoachFOComponent } from './coach-fo.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TeamsComponent } from './teams/teams.component';
import { TrainingsComponent } from './trainings/trainings.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { DetailTrainingComponent } from './detail-training/detail-training.component';
import { HomeComponent } from './home/home.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { ScheduleComponent } from './schedule/schedule.component';

import { NgModule, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

registerLocaleData(localeFr);  // ← ajoute avant @NgModule

@NgModule({
  declarations: [
    CoachFOComponent,
    NavbarComponent,
    FooterComponent,
    TeamsComponent,
    TrainingsComponent,
    DetailTeamComponent,
    DetailMatchComponent,
    DetailTrainingComponent,
    HomeComponent,
    PlayerProfileComponent,
    ScheduleComponent
  ],
  imports: [
    CommonModule,
    CoachFORoutingModule,
    FormsModule,
    ReactiveFormsModule
  ],
    providers: [DatePipe,
       { provide: LOCALE_ID, useValue: 'fr' }  
    ]
})
export class CoachFOModule { }
