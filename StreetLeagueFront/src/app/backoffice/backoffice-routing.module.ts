import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';
import { ListTeamsComponent } from './list-teams/list-teams.component';
import { ListTrainingComponent } from './list-training/list-training.component';
import { HomeComponent } from './home/home.component';
import { TransportComponent } from './transport/transport.component';
import { AccommodationComponent } from './accommodation/accommodation.component';
import { CoachTravelComponent } from './coach-travel/coach-travel.component';
import { AccommodationRequestsComponent } from './accommodation-requests/accommodation-requests.component';


import { TransportManagementComponent } from './transport-management/transport-management.component';
import { TravelRequestsComponent } from './travel-requests/travel-requests.component';
import { AdminTransportRequestsComponent } from './transport-requests/admin-transport-requests.component';
import { PlayerProfileComponent } from './player-profile/player-profile.component';
import { TournamentComponent } from './tournaments/tournaments.component';
import { FieldReservationComponent } from './field-reservation/field-reservation.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component';
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';
import { CommandeListComponent } from './commande/commande-list/commande-list.component';

const routes: Routes = [{ path: '', component: BackofficeComponent,   
    children: [                      
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent},
      { path: 'teamAdmin', component: ListTeamsComponent},
      { path: 'trainingAdmin', component: ListTrainingComponent},
            { path: 'profile', component: PlayerProfileComponent },
      { path: 'tournaments', component: TournamentComponent },
      { path: 'field-reservation', component: FieldReservationComponent },

      { path: 'transport', component: TransportManagementComponent },
      { path: 'travel/requests', component: TravelRequestsComponent },
      { path: 'accommodation', component: AccommodationComponent },
      { path: 'accommodation-requests', component: AccommodationRequestsComponent },
      { path: 'transport-requests', component: AdminTransportRequestsComponent },
      { path: 'coach-travel', component: CoachTravelComponent },





       { path: 'materiels', component: MaterielListComponent },         
      { path: 'livraisons', component: LivraisonListComponent },
      { path: 'livraisons/new', component: LivraisonFormComponent },
      { path: 'transporteurs', component: TransporteurListComponent },
      { path: 'commandes', component: CommandeListComponent },



      


    
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BackofficeRoutingModule { }
