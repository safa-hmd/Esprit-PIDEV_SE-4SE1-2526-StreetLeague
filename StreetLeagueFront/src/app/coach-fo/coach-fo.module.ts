import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CoachFORoutingModule } from './coach-fo-routing.module';
import { CoachFOComponent } from './coach-fo.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { FormControl, FormsModule } from '@angular/forms';
import { TeamsComponent } from './teams/teams.component';
import { TrainingsComponent } from './trainings/trainings.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { DetailTrainingComponent } from './detail-training/detail-training.component';
import { HomeComponent } from './home/home.component';


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
    HomeComponent
  ],
  imports: [
    CommonModule,
    CoachFORoutingModule,
    FormsModule
  ]
})
export class CoachFOModule { }
