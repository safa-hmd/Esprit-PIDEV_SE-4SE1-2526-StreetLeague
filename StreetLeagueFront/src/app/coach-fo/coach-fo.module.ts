
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
import { CoachAccommodationComponent } from './accommodation/coach-accommodation.component';
import { CoachTransportComponent } from './transport/coach-transport/coach-transport.component';
import { TransportBookComponent } from './transport/transport-book/transport-book.component';
import { PersonalCarComponent } from './transport/personal-car/personal-car.component';
import { MyRequestsComponent } from './transport/my-requests/my-requests.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { ScheduleComponent } from './schedule/schedule.component';


import { NgModule, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { FieldRecommenderComponent } from './field-recommender/field-recommender.component';
import { CoachAnomalyDashboardComponent } from './coach-anomaly-dashboard/coach-anomaly-dashboard.component';

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
    CoachAccommodationComponent,
    CoachTransportComponent,
    TransportBookComponent,
    PersonalCarComponent,
    MyRequestsComponent,

    PlayerProfileComponent,
    ScheduleComponent,
    FieldRecommenderComponent,
    CoachAnomalyDashboardComponent
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
