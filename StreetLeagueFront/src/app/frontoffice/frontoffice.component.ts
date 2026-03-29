import { Component } from '@angular/core';

@Component({
  selector: 'app-frontoffice',
  templateUrl: './frontoffice.component.html',
  styleUrls: ['./frontoffice.component.css']
})
export class FrontofficeComponent {

}


/*import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ShopService } from '../services/shop.service';

@Component({
  selector: 'app-frontoffice',
  templateUrl: './frontoffice.component.html',
  styleUrls: ['./frontoffice.component.css']
})
export class FrontofficeComponent implements OnInit {
  cartCount = 0;
  userId = 0;

  constructor(
    private authService: AuthService,
    private shopService: ShopService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (id) {
      this.userId = id;
      this.refreshCart();
    }
  }

  refreshCart(): void {
    this.shopService.getCart(this.userId).subscribe({
      next: panier => this.cartCount = panier?.lignes?.length || 0,
      error: () => this.cartCount = 0
    });
  }

  goToCart(): void {
    this.router.navigate(['/client/cart']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}*/