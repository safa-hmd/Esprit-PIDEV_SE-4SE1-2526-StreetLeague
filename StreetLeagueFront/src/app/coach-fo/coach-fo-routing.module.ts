import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoachFOComponent } from './coach-fo.component';
import { TrainingsComponent } from './trainings/trainings.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { CoachAccommodationComponent } from './accommodation/coach-accommodation.component';
import { CoachTransportComponent } from './transport/coach-transport/coach-transport.component';
import { TransportBookComponent } from './transport/transport-book/transport-book.component';
import { PersonalCarComponent } from './transport/personal-car/personal-car.component';
import { MyRequestsComponent } from './transport/my-requests/my-requests.component';
import { ScheduleComponent } from './schedule/schedule.component';
import { FieldRecommenderComponent } from './field-recommender/field-recommender.component';
import { CoachAnomalyDashboardComponent } from './coach-anomaly-dashboard/coach-anomaly-dashboard.component';

const routes: Routes = [{
  path: '',
  component: CoachFOComponent,
  children: [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'home', component: HomeComponent },
    { path: 'trainingCoach', component: TrainingsComponent },
    { path: 'detail-team/:id', component: DetailTeamComponent },
    { path: 'detail-match/:id', component: DetailMatchComponent },
    { path: 'lodging', component: CoachAccommodationComponent },
    { path: 'transport', component: CoachTransportComponent },
    { path: 'transport/book', component: TransportBookComponent },
    { path: 'transport/personal-car', component: PersonalCarComponent },
    { path: 'transport/my-requests', component: MyRequestsComponent },
    { path: 'schedule', component: ScheduleComponent },
    { path: 'recommend', component: FieldRecommenderComponent },
    { path: 'anomalies', component: CoachAnomalyDashboardComponent }
  ]
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CoachFORoutingModule { }
