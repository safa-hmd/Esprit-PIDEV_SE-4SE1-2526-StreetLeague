import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { FormsModule } from '@angular/forms';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { HomeComponent } from './home/home.component';



@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    ListTeamsComponent,
    ListTrainingComponent,
    HomeComponent

  

  ],
  imports: [
    CommonModule,
    BackofficeRoutingModule,
    FormsModule
  ]
})
export class BackofficeModule { }
