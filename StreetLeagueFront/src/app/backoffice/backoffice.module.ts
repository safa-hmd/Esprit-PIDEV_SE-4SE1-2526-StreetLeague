import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { HomeComponent } from './home/home.component';
import { TransportComponent } from './transport/transport.component';
import { AccommodationComponent } from './accommodation/accommodation.component';
import { CoachTravelComponent } from './coach-travel/coach-travel.component';
import { AccommodationRequestsComponent } from './accommodation-requests/accommodation-requests.component';



@NgModule({
  declarations: [
    BackofficeComponent,
    HeaderComponent,
    MenuComponent,
    ListTeamsComponent,
    ListTrainingComponent,
    HomeComponent,
    TransportComponent,
    AccommodationComponent,
    CoachTravelComponent,
    AccommodationRequestsComponent

  

  ],
  imports: [
    CommonModule,
    BackofficeRoutingModule,
    FormsModule,
    HttpClientModule
  ]
})
export class BackofficeModule { }
