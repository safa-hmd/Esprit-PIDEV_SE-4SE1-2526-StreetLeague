import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

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
    MyRequestsComponent
  ],
  imports: [
    CommonModule,
    CoachFORoutingModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class CoachFOModule { }
