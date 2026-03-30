import { Component, OnInit } from '@angular/core';
import { ShopService } from '../../../services/shop.service';
import { AuthService } from '../../../services/auth.service';
import { Materiel, Category } from '../../../models/materiel.model';
import { Router } from '@angular/router';
@Component({
  selector: 'app-shop-list',
  templateUrl: './shop-list.component.html',
  styleUrls: ['./shop-list.component.css']
})
export class ShopListComponent implements OnInit {
  materiels: Materiel[] = [];
  filteredMateriels: Materiel[] = [];
  categories: Category[] = [];
  selectedCategorieId: number | null = null;
  cartCount = 0;
  userId: number = 0;
  successMsg = '';
  errorMsg = '';

  constructor(
    private shopService: ShopService,
    private authService: AuthService,
    private router: Router 
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (!id) {
      this.showError('Utilisateur non connecté.');
      return;
    }
    this.userId = Number(id);
    this.loadMateriels();
    this.loadCategories();
  }

  loadMateriels(): void {
    this.shopService.getAllMateriels().subscribe({
      next: data => {
        this.materiels = data;
        this.filteredMateriels = data;
      },
      error: () => this.showError('Erreur lors du chargement des produits.')
    });
  }

  loadCategories(): void {
    this.shopService.getAllCategories().subscribe({
      next: data => this.categories = data,
      error: () => {}
    });
  }

  filterByCategory(categorieId: number | null): void {
    this.selectedCategorieId = categorieId;
    this.filteredMateriels = categorieId
      ? this.materiels.filter(m => m.categorieId === categorieId)
      : [...this.materiels];
  }

  addToCart(materiel: Materiel): void {
    this.shopService.addToCart({
      userId: this.userId,
      materielId: materiel.id!,
      quantite: 1
    }).subscribe({
      next: () => {
        this.cartCount++;
        this.showSuccess(`${materiel.nom} ajouté au panier !`);
      },
      error: () => this.showError('Erreur lors de l\'ajout au panier.')
    });
  }

  checkout(): void {
    this.shopService.checkout(this.userId).subscribe({
      next: (msg) => this.showSuccess(msg),
      error: () => this.showError('Erreur lors de la commande.')
    });
  }

  goToCart(): void {
  this.router.navigate(['/client/cart']);
}

  private showSuccess(msg: string): void {
    this.successMsg = msg;
    this.errorMsg = '';
    setTimeout(() => this.successMsg = '', 3000);
  }

  private showError(msg: string): void {
    this.errorMsg = msg;
    this.successMsg = '';
    setTimeout(() => this.errorMsg = '', 3000);
  }
}