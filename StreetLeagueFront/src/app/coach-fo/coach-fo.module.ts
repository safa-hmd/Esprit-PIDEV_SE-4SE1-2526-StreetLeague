import { NgModule, LOCALE_ID } from '@angular/core';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import localeFr from '@angular/common/locales/fr';

import { CoachFORoutingModule } from './coach-fo-routing.module';
import { CoachFOComponent } from './coach-fo.component';
import { NavbarComponent } from './navbar/navbar.component';
import { HomeComponent } from './home/home.component';
import { CoachAccommodationComponent } from './accommodation/coach-accommodation.component';
import { CoachTransportComponent } from './transport/coach-transport/coach-transport.component';
import { TransportBookComponent } from './transport/transport-book/transport-book.component';
import { PersonalCarComponent } from './transport/personal-car/personal-car.component';
import { MyRequestsComponent } from './transport/my-requests/my-requests.component';
import { ScheduleComponent } from './schedule/schedule.component';
import { FieldRecommenderComponent } from './field-recommender/field-recommender.component';
import { CoachAnomalyDashboardComponent } from './coach-anomaly-dashboard/coach-anomaly-dashboard.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { TrainingsComponent } from './trainings/trainings.component';
import { TeamsComponent } from './teams/teams.component';
import { DetailTrainingComponent } from './detail-training/detail-training.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';

registerLocaleData(localeFr);

@NgModule({
  declarations: [
    CoachFOComponent,
    NavbarComponent,
    HomeComponent,
    CoachAccommodationComponent,
    CoachTransportComponent,
    TransportBookComponent,
    PersonalCarComponent,
    MyRequestsComponent,
    ScheduleComponent,
    FieldRecommenderComponent,
    CoachAnomalyDashboardComponent,
    DetailMatchComponent,
    DetailTeamComponent,
    TrainingsComponent,
    TeamsComponent,
    DetailTrainingComponent,
    PlayerProfileComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CoachFORoutingModule,
  ],
  providers: [
    DatePipe,
    { provide: LOCALE_ID, useValue: 'fr' }
  ]
})
export class CoachFOModule { }
