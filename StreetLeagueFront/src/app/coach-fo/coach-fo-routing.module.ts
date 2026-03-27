import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoachFOComponent } from './coach-fo.component';
import { TeamsComponent } from './teams/teams.component';
import { TrainingsComponent } from './trainings/trainings.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';    
import { DetailMatchComponent } from './detail-match/detail-match.component'; 
import { DetailTrainingComponent } from './detail-training/detail-training.component';
import { HomeComponent } from './home/home.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';

const routes: Routes = [{ path: '', component: CoachFOComponent,   
    children: [                      
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {path: 'home', component: HomeComponent },
      { path: 'teamCoach', component: TeamsComponent },
      {path: 'trainingCoach', component: TrainingsComponent },
      { path: 'detail-team/:id',  component: DetailTeamComponent },  
    { path: 'detail-match/:id', component: DetailMatchComponent },
    { path: 'detail-training/:id', component: DetailTrainingComponent },
    { path: 'profile', component: PlayerProfileComponent },
      
    ]
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CoachFORoutingModule { }
