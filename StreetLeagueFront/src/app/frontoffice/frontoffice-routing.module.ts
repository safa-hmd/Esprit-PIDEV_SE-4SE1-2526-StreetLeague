import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { NewsComponent } from './news/news.component';
import { HealthComponent } from './health/health.component';

import { ContratListComponent } from '../components/contrat-sponsor/contrat-list.component';
import { ContratFormComponent } from '../components/contrat-sponsor/contrat-form.component';
import { ContratEditComponent } from '../components/contrat-sponsor/contrat-edit.component';

import { CommunauteListComponent } from '../components/communaute/communaute-list.component';
import { CommunauteFormComponent } from '../components/communaute/communaute-form.component';
import { CommunauteDetailComponent } from '../components/communaute/communaute-detail.component';
import { CommunauteEditComponent } from '../components/communaute/communaute-edit.component';

import { EvenementListComponent } from '../components/evenement/evenement-list.component';
import { EvenementFormComponent } from '../components/evenement/evenement-form.component';
import { EvenementDetailComponent } from '../components/evenement/evenement-detail.component';
import { EvenementEditComponent } from '../components/evenement/evenement-edit.component';

import { SponsorListComponent } from '../components/sponsor/sponsor-list.component';
import { SponsorFormComponent } from '../components/sponsor/sponsor-form.component';
import { SponsorDetailComponent } from '../components/sponsor/sponsor-detail.component';
import { SponsorEditComponent } from '../components/sponsor/sponsor-edit.component';

import { SponsoringListComponent } from '../components/sponsoring-evenement/sponsoring-list.component';
import { SponsoringFormComponent } from '../components/sponsoring-evenement/sponsoring-form.component';
import { SponsoringDetailComponent } from '../components/sponsoring-evenement/sponsoring-detail.component';
import { SponsoringEditComponent } from '../components/sponsoring-evenement/sponsoring-edit.component';

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
      { path: 'communaute', component: CommunauteListComponent },
      { path: 'communaute/new', component: CommunauteFormComponent },
      { path: 'communaute/:id', component: CommunauteDetailComponent },
      { path: 'communaute/:id/edit', component: CommunauteEditComponent },
      { path: 'evenement', component: EvenementListComponent },
      { path: 'evenement/new', component: EvenementFormComponent },
      { path: 'evenement/:id', component: EvenementDetailComponent },
      { path: 'evenement/:id/edit', component: EvenementEditComponent },
      { path: 'sponsor', component: SponsorListComponent },
      { path: 'sponsor/new', component: SponsorFormComponent },
      { path: 'sponsor/:id', component: SponsorDetailComponent },
      { path: 'sponsor/:id/edit', component: SponsorEditComponent },
      { path: 'sponsoring-evenement', component: SponsoringListComponent },
      { path: 'sponsoring-evenement/new', component: SponsoringFormComponent },
      { path: 'sponsoring-evenement/:id', component: SponsoringDetailComponent },
      { path: 'sponsoring-evenement/:id/edit', component: SponsoringEditComponent },
      { path: 'contrat-sponsor', component: ContratListComponent },
      { path: 'contrat-sponsor/new', component: ContratFormComponent },
      { path: 'contrat-sponsor/:id/edit', component: ContratEditComponent },
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