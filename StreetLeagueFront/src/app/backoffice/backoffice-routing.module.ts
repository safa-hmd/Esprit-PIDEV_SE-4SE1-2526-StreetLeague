import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeComponent } from './backoffice.component';
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component'; 
import { CommandeListComponent } from './commande/commande-list/commande-list.component'; 

const routes: Routes = [
  {
    path: '',
    component: BackofficeComponent,
    children: [
      { path: '', redirectTo: 'materiels', pathMatch: 'full' },
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
export class BackofficeRoutingModule {}