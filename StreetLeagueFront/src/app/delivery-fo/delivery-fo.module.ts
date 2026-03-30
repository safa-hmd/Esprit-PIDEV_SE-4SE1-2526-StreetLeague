import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
 
import { DeliveryFoRoutingModule } from './delivery-fo-routing.module';
import { DeliveryFoComponent } from './delivery-fo.component';
import { MesLivraisonsComponent } from './mes-livraisons/mes-livraisons.component';
import { FilterByStatutPipe } from './filter-by-statut.pipe';
 
@NgModule({
  declarations: [
    DeliveryFoComponent,
    MesLivraisonsComponent,
    FilterByStatutPipe,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    DeliveryFoRoutingModule,
  ]
})
export class DeliveryFoModule { }