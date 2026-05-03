import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { NewsComponent } from './news/news.component';
import { HealthComponent } from './health/health.component';



import { HomeComponent } from './home/home.component';
import { TeamComponent } from './team/team.component';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentsComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { CartComponent } from './shop/cart/cart.component';
import { BracketComponent } from './bracket/bracket.component';
import { MatchHistoryComponent } from './match-history/match-history.component';
import { ScheduleComponent } from './schedule/schedule.component';
import { FieldRecommenderComponent } from './field-recommender/field-recommender.component';
import { MatchmakingComponent } from './matchmaking/matchmaking.component';
import { PerformanceStreakComponent } from './performance-streak/performance-streak.component';
import { PerformancePredictionComponent } from './performance-prediction/performance-prediction.component';
import { InjuryRiskComponent } from './injury-risk/injury-risk.component';

const routes: Routes = [
  {
    path: '',
    component: FrontofficeComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'team', component: TeamComponent },
      { path: 'training', component: TrainingComponent },
      { path: 'detail-match/:id', component: DetailMatchComponent },
      { path: 'detail-team/:id', component: DetailTeamComponent },
      { path: 'profile', component: PlayerProfileComponent },
      { path: 'tournaments', component: TournamentsComponent },
      { path: 'field-reservation', component: FieldReservationComponent },
      { path: 'tournaments/:id/bracket', component: BracketComponent },

      { path: 'shop', component: ShopListComponent },
      { path: 'cart', component: CartComponent },
      { path: 'news', component: NewsComponent },
      { path: 'health', component: HealthComponent },
      { path: 'match-history', component: MatchHistoryComponent },
      { path: 'schedule', component: ScheduleComponent },
      { path: 'recommend', component: FieldRecommenderComponent },
      { path: 'matchmaking', component: MatchmakingComponent },
      { path: 'performance-streak', component: PerformanceStreakComponent },
      { path: 'injury-risk', component: InjuryRiskComponent },
      { path: 'performance-prediction', component: PerformancePredictionComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }