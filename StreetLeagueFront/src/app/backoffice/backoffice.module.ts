import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';           // ← obligatoire pour [(ngModel)]
import { RouterModule } from '@angular/router';
 
import { BackofficeRoutingModule } from './backoffice-routing.module';
import { BackofficeComponent } from './backoffice.component';
 
import { LivraisonListComponent } from './livraison/livraison-list/livraison-list.component';
import { LivraisonFormComponent } from './livraison/livraison-form/livraison-form.component';
import { TransporteurListComponent } from './transporteur/transporteur-list/transporteur-list.component';
import { MenuComponent } from './menu/menu.component';
import { HeaderComponent } from './header/header.component';
import { MaterielListComponent } from './materiel/materiel-list/materiel-list.component';
import { CommandeListComponent } from './commande/commande-list/commande-list.component';
import { DashboardComponent } from './dashboard/dashboard.component';
 
@NgModule({
  declarations: [
    BackofficeComponent,
    LivraisonListComponent,
    LivraisonFormComponent,
    TransporteurListComponent,
    MenuComponent,
    HeaderComponent,
    MaterielListComponent,
    CommandeListComponent,
    DashboardComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,           // ← pour [(ngModel)] et (ngModel)
    RouterModule,          // ← pour routerLink dans les templates
    BackofficeRoutingModule,
  ]
})
export class BackofficeModule {}