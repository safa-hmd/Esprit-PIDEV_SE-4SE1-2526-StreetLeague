import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
import { FooterComponent } from './footer/footer.component';
import { NavbarComponent } from './navbar/navbar.component';
import { TeamComponent } from './team/team.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';


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
