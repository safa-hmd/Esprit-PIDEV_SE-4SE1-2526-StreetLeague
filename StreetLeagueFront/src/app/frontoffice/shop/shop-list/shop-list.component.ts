import { Component, OnInit } from '@angular/core';
import { ShopService } from '../../../services/shop.service';
import { AuthService } from '../../../services/auth.service';
import { RecommendationService } from '../../../services/recommendation.service';
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

  //  Propriétés pour l'IA
  recommendedMateriels: Materiel[] = [];
  loadingRecommendations = false;
  private _pendingRecIds: number[] = [];

  constructor(
    private shopService: ShopService,
    private authService: AuthService,
    private recommendationService: RecommendationService,
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
    this.loadRecommendations(); //  Appel asynchrone à l'API ML
  }

  loadMateriels(): void {
  this.shopService.getAllMateriels().subscribe({
    next: (data) => {
      console.log('📦 Données brutes reçues du backend:', data);
      
      if (!data || data.length === 0) {
        this.showError('Aucun produit trouvé dans la base.');
        return;
      }

      this.materiels = data;
      this.filteredMateriels = data;
      
      // Log des IDs réels de votre DB
      const realIds = data.map(m => (m as any).id ?? (m as any).materielId ?? (m as any).produitId);
      console.log('🔢 IDs réels dans Spring Boot:', realIds);

      this.mapRecommendationsToMateriels();
    },
    error: (err) => {
      console.error('❌ Erreur chargement produits:', err);
      this.showError('Erreur lors du chargement des produits.');
    }
  });
}

  loadCategories(): void {
    this.shopService.getAllCategories().subscribe({
      next: data => this.categories = data,
      error: () => {}
    });
  }

  loadRecommendations(): void {
  this.loadingRecommendations = true;
  const excludeIds: number[] = [];
  console.log('🔍 Appel IA pour userId:', this.userId);

  this.recommendationService.getRecommendations(this.userId, excludeIds, 4).subscribe({
    next: (ids) => {
      console.log('✅ IDs retournés par FastAPI:', ids);
      this._pendingRecIds = ids;
      this.mapRecommendationsToMateriels();
      this.loadingRecommendations = false;
    },
    error: (err) => {
      console.error('❌ Erreur appel IA:', err);
      this.loadingRecommendations = false;
      this.recommendedMateriels = [];
    }
  });
}

  /**
   * Mappe les IDs retournés par FastAPI vers les objets Materiel complets.
   * S'exécute dès que les produits OU les IDs IA sont disponibles.
   */
 private mapRecommendationsToMateriels(): void {
  if (!this.materiels || this.materiels.length === 0) return;

  // 1. Identifier les IDs IA qui correspondent VRAIMENT à vos produits EN STOCK
  const matchedIds = this._pendingRecIds.filter(id =>
    this.materiels.some(m => Number(m.id) === id && m.quantiteStock > 0)
  );

  if (matchedIds.length > 0) {
    // ✅ Cas normal : L'IA a trouvé des correspondances
    this.recommendedMateriels = this.materiels.filter(m =>
      matchedIds.includes(Number(m.id))
    );
    console.log(`🤖 IA → ${this.recommendedMateriels.length} produit(s) personnalisé(s) affichés.`);
  } else {
    // ⚠️ Cas Cold Start / Mismatch : Fallback "Tendances" (Produits populaires/en stock)
    console.log('⚠️ Cold Start détecté (0 overlap IDs) → Activation du fallback UI');
    this.recommendedMateriels = this.materiels
      .filter(m => m.quantiteStock > 0)
      .slice(0, 4); // Affiche les 4 premiers produits disponibles
  }
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