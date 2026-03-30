import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
 
import { FrontofficeRoutingModule } from './frontoffice-routing.module';
import { FrontofficeComponent } from './frontoffice.component';
 
import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { CartComponent } from './shop/cart/cart.component';
 
// Si tu as déjà un navbar et footer composant, importe-les ici aussi
 import { NavbarComponent } from './navbar/navbar.component';
 import { FooterComponent } from './footer/footer.component';
 
@NgModule({
  declarations: [
    FrontofficeComponent,
    ShopListComponent,
    CartComponent,
     NavbarComponent,
     FooterComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    FrontofficeRoutingModule,
  ]
})
export class FrontofficeModule {}
 