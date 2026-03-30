import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrontofficeComponent } from './frontoffice.component';
import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { CartComponent } from './shop/cart/cart.component';

const routes: Routes = [
  {
    path: '',
    component: FrontofficeComponent,
    children: [                                              // ← children ici
      { path: '', redirectTo: 'shop', pathMatch: 'full' },  // ← /client → /client/shop
      { path: 'shop', component: ShopListComponent },
      { path: 'cart', component: CartComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FrontofficeRoutingModule { }