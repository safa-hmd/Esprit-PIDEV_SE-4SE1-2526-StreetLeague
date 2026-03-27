import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { HomeComponent } from './home/home.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';


const routes: Routes = [{ path: '', component: BackofficeComponent,   
    children: [                      
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {path: 'home', component: HomeComponent},
      { path: 'teamAdmin', component: ListTeamsComponent},
      {path: 'trainingAdmin', component: ListTrainingComponent},
      { path: 'tournaments', component: TournamentComponent },
      { path: 'field-reservation', component: FieldReservationComponent }
      
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }
