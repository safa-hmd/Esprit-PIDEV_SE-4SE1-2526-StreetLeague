import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Materiel, Category } from '../models/materiel.model';
import { AddToCartDTO, PanierResponse } from '../models/panier.model';

@Injectable({ providedIn: 'root' })
export class ShopService {
  private base = `${environment.baseUrl}/api`;

  constructor(private http: HttpClient) {}

  // ── Matériels ──────────────────────────────────────
  getAllMateriels(): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(`${this.base}/materiels`);
  }

  getMaterielById(id: number): Observable<Materiel> {
    return this.http.get<Materiel>(`${this.base}/materiels/${id}`);
  }

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.base}/categories`);
  }

  // ── Panier ─────────────────────────────────────────
  addToCart(dto: AddToCartDTO): Observable<string> {
    return this.http.post(              // ← pas de <string> ici
      `${this.base}/panier/add`,
      dto,
      { responseType: 'text' }
    );
  }

  getCart(userId: number): Observable<PanierResponse> {
    return this.http.get<PanierResponse>(`${this.base}/panier/${userId}`);
  }

  removeItem(ligneId: number): Observable<string> {
    return this.http.delete(           // ← pas de <string> ici
      `${this.base}/panier/remove/${ligneId}`,
      { responseType: 'text' }
    );
  }

  clearCart(userId: number): Observable<string> {
    return this.http.delete(           // ← pas de <string> ici
      `${this.base}/panier/clear/${userId}`,
      { responseType: 'text' }
    );
  }

  // ── Commande ───────────────────────────────────────
  checkout(userId: number): Observable<string> {
    return this.http.post(             // ← pas de <string> ici
      `${this.base}/commandes/checkout/${userId}`,
      {},
      { responseType: 'text' }
    );
  }
}
