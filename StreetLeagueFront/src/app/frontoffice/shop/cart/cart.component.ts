import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ShopService } from '../../../services/shop.service';
import { AuthService } from '../../../services/auth.service';
import { PanierResponse } from '../../../models/panier.model';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  panier: PanierResponse | null = null;
  userId: number = 0;
  successMsg = '';
  errorMsg = '';
  loading = false;

  constructor(
    private shopService: ShopService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (!id) {
      this.router.navigate(['/']);
      return;
    }
    this.userId = Number(id);
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    this.shopService.getCart(this.userId).subscribe({
      next: data => { this.panier = data; this.loading = false; },
      error: () => { this.panier = null; this.loading = false; }
    });
  }

  removeItem(ligneId: number): void {
    this.shopService.removeItem(ligneId).subscribe({
      next: () => { this.showSuccess('Article supprimé.'); this.loadCart(); },
      error: () => this.showError('Erreur lors de la suppression.')
    });
  }

  clearCart(): void {
    if (!confirm('Vider tout le panier ?')) return;
    this.shopService.clearCart(this.userId).subscribe({
      next: () => { this.showSuccess('Panier vidé.'); this.loadCart(); },
      error: () => this.showError('Erreur lors de la suppression.')
    });
  }

  checkout(): void {
    this.shopService.checkout(this.userId).subscribe({
      next: (msg) => {
        this.showSuccess(msg);
        setTimeout(() => this.router.navigate(['/client/shop']), 2000);
      },
      error: () => this.showError('Erreur lors de la commande.')
    });
  }

  private showSuccess(msg: string): void {
    this.successMsg = msg; this.errorMsg = '';
    setTimeout(() => this.successMsg = '', 3000);
  }
  private showError(msg: string): void {
    this.errorMsg = msg; this.successMsg = '';
    setTimeout(() => this.errorMsg = '', 3000);
  }
}