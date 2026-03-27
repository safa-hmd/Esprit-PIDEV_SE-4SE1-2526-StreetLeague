import { NgModule } from '@angular/core';
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
  ],
  imports: [
    CommonModule,
    FrontofficeRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule, 
    CommonModule,
  ]
})
export class FrontofficeModule { }
