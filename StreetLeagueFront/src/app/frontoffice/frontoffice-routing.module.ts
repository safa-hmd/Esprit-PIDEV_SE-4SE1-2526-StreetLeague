import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { CartComponent } from './shop/cart/cart.component';

// ── Composants de feature/match-team-training ──────────────
// Décommente et importe selon les composants existants dans ta branche
// import { TrainingListComponent } from './training/training-list/training-list.component';
// import { TeamListComponent } from './team/team-list/team-list.component';
// import { MatchListComponent } from './match/match-list/match-list.component';

// ── Composants de feature/tournament-fieldreservation ──────
// import { TournamentListComponent } from './tournament/tournament-list/tournament-list.component';
// import { FieldListComponent } from './field/field-list/field-list.component';

const routes: Routes = [
  {
    path: '',
    component: FrontofficeComponent,
    children: [
      { path: '', redirectTo: 'shop', pathMatch: 'full' },

      // ── Ton module (StreetLeague) ──────────────────────
      { path: 'shop',  component: ShopListComponent },
      { path: 'cart',  component: CartComponent },

      // ── feature/match-team-training ───────────────────
      // Décommente quand les composants sont disponibles :
      // { path: 'training',  component: TrainingListComponent },
      // { path: 'teams',     component: TeamListComponent },
      // { path: 'matches',   component: MatchListComponent },
      // { path: 'community', component: CommunityComponent },
      // { path: 'health',    component: HealthComponent },
      // { path: 'news',      component: NewsComponent },

      // ── feature/tournament-fieldreservation ───────────
      // Décommente quand les composants sont disponibles :
      // { path: 'tournaments', component: TournamentListComponent },
      // { path: 'fields',      component: FieldListComponent },
      // { path: 'lodging',     component: LodgingComponent },
      // { path: 'transport',   component: TransportComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }