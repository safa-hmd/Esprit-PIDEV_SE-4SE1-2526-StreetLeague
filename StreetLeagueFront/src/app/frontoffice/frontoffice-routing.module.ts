import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { TeamComponent } from './team/team.component';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { TournamentsComponent } from './tournaments/tournaments.component';

const routes: Routes = [
  {
    path: '',
    component: FrontofficeComponent,   
    children: [                      
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {path: 'home', component: HomeComponent },
      { path: 'team', component: TeamComponent },
      {path: 'training',component:TrainingComponent },
      {path:"detail-match/:id",component:DetailMatchComponent},
      {path:"detail-team/:id", component:DetailTeamComponent},
      { path: 'tournaments', component: TournamentsComponent }
    ]
  }
]


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }
