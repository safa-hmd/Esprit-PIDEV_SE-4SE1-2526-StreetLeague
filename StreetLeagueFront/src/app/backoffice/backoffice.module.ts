import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { HomeComponent } from './home/home.component';
import { TournamentComponent } from './tournaments/tournaments.component';


@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    ListTeamsComponent,
    ListTrainingComponent,
    HomeComponent,
    TournamentComponent,

  

  ],
  imports: [
    CommonModule,
    BackofficeRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ]
})
export class BackofficeModule { }
