import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { TeamComponent } from './team/team.component';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { HomeComponent } from './home/home.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { MatchHistoryComponent } from './match-history/match-history.component';
import { ScheduleComponent } from './schedule/schedule.component';

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
      { path: 'profile', component: PlayerProfileComponent },
      {path: 'match-history',component: MatchHistoryComponent},
        { path: 'schedule', component: ScheduleComponent }
    ]
  }
]


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }
