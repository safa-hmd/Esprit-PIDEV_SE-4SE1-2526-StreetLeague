import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DeliveryFoComponent } from './delivery-fo.component';
import { MesLivraisonsComponent } from './mes-livraisons/mes-livraisons.component';
 
const routes: Routes = [
  {
    path: '',
    component: DeliveryFoComponent,
    children: [
      { path: '',               redirectTo: 'mes-livraisons', pathMatch: 'full' },
      { path: 'mes-livraisons', component: MesLivraisonsComponent },
    ]
  }
];
 
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DeliveryFoRoutingModule {}