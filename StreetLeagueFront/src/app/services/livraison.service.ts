import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Livraison, Transporteur } from '../models/livraison.model';

@Injectable({ providedIn: 'root' })
export class LivraisonService {
  private base = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  // ── Livraisons ────────────────────────────────────────
  getAllLivraisons(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/livraisons`);
  }

  getLivraisonById(id: number): Observable<Livraison> {
    return this.http.get<Livraison>(`${this.base}/livraisons/${id}`);
  }

  createLivraison(dto: any): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.base}/livraisons`, dto);
  }

  updateStatus(id: number, dto: any): Observable<Livraison> {
    return this.http.put<Livraison>(`${this.base}/livraisons/${id}/status`, dto);
  }

  // ── Transporteurs ─────────────────────────────────────
  getAllTransporteurs(): Observable<Transporteur[]> {
    return this.http.get<Transporteur[]>(`${this.base}/transporteurs`);
  }

  createTransporteur(dto: Transporteur): Observable<Transporteur> {
    return this.http.post<Transporteur>(`${this.base}/transporteurs`, dto);
  }

  updateTransporteur(id: number, dto: Transporteur): Observable<Transporteur> {
    return this.http.put<Transporteur>(`${this.base}/transporteurs/${id}`, dto);
  }

  deleteTransporteur(id: number): Observable<string> {
    return this.http.delete(
      `${this.base}/transporteurs/${id}`,
      { responseType: 'text' }
    );
  }

  // ── Livreurs : GET tous les users puis filtre DELIVERY ─
  getAllDeliveryUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/users`).pipe(
      // Filtre côté Angular : garde uniquement les users avec rôle DELIVERY
      map((users: any[]) => users.filter(u =>
        u.role === 'DELIVERY' || u.role === 'ROLE_DELIVERY'
      )),
      // Si /api/users n'existe pas → retourne liste vide sans planter
      catchError(() => of([]))
    );
  }
}