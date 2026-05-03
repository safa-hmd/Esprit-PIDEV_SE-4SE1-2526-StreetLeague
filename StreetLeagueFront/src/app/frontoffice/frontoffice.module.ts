import { LOCALE_ID, NgModule } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';

import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { HomeComponent } from './home/home.component';

import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { CartComponent } from './shop/cart/cart.component';
import { TeamComponent } from './team/team.component';
import { TrainingComponent } from './training/training.component';
import { DetailMatchComponent } from './detail-match/detail-match.component';
import { DetailTeamComponent } from './detail-team/detail-team.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentsComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { NewsComponent } from './news/news.component';
import { HealthComponent } from './health/health.component';
import { BracketComponent } from './bracket/bracket.component';
import { MatchHistoryComponent } from './match-history/match-history.component';
import { ScheduleComponent } from './schedule/schedule.component';
import { FieldRecommenderComponent } from './field-recommender/field-recommender.component';
import { MatchmakingComponent } from './matchmaking/matchmaking.component';
import { PerformanceStreakComponent } from './performance-streak/performance-streak.component';
import { InjuryRiskComponent } from './injury-risk/injury-risk.component';
import { PerformancePredictionComponent } from './performance-prediction/performance-prediction.component';

import { CommunauteModule } from './communaute/communaute.module';
import { EvenementModule } from './evenement/evenement.module';

registerLocaleData(localeFr);

@NgModule({
  declarations: [
    FrontofficeComponent,
    ShopListComponent,
    CartComponent,
    NavbarComponent,
    FooterComponent,
    HomeComponent,
    TeamComponent,
    TrainingComponent,
    DetailMatchComponent,
    DetailTeamComponent,
    PlayerProfileComponent,
    TournamentsComponent,
    FieldReservationComponent,
    NewsComponent,
    HealthComponent,
    BracketComponent,
    MatchHistoryComponent,
    ScheduleComponent,
    FieldRecommenderComponent,
    MatchmakingComponent,
    PerformanceStreakComponent,
    InjuryRiskComponent,
    PerformancePredictionComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    FrontofficeRoutingModule,
    CommunauteModule,
    EvenementModule,
    RouterModule
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'fr' }
  ]
})
export class FrontofficeModule {}