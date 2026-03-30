import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component';
import { CommandeListComponent } from './commande/commande-list/commande-list.component';

// ── feature/match-team-training ────────────────────────────
// Décommente selon les composants disponibles dans ta branche :
// import { TrainingListComponent } from './training/training-list/training-list.component';
// import { TeamListComponent } from './team/team-list/team-list.component';
// import { MatchListComponent } from './match/match-list/match-list.component';

// ── feature/tournament-fieldreservation ────────────────────
// import { TournamentListComponent } from './tournament/tournament-list/tournament-list.component';
// import { FieldListComponent } from './field/field-list/field-list.component';

const routes: Routes = [
  {
    path: '',
    component: BackofficeComponent,
    children: [
      { path: '', redirectTo: 'materiels', pathMatch: 'full' },

      // ── Ton module (StreetLeague) ──────────────────────
      { path: 'materiels',      component: MaterielListComponent },
      { path: 'livraisons',     component: LivraisonListComponent },
      { path: 'livraisons/new', component: LivraisonFormComponent },
      { path: 'transporteurs',  component: TransporteurListComponent },
      { path: 'commandes',      component: CommandeListComponent },

      // ── feature/match-team-training ───────────────────
      // Décommente quand les composants sont disponibles :
      // { path: 'training',    component: TrainingListComponent },
      // { path: 'teams',       component: TeamListComponent },
      // { path: 'matches',     component: MatchListComponent },
      // { path: 'health',      component: HealthAdminComponent },
      // { path: 'news',        component: NewsAdminComponent },
      // { path: 'community',   component: CommunityAdminComponent },
      // { path: 'sponsors',    component: SponsorsAdminComponent },

      // ── feature/tournament-fieldreservation ───────────
      // Décommente quand les composants sont disponibles :
      // { path: 'tournaments',   component: TournamentListComponent },
      // { path: 'fields',        component: FieldListComponent },
      // { path: 'accommodation', component: AccommodationAdminComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }