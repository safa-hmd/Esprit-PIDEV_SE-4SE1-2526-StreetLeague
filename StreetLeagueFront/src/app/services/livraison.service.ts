import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Livraison, Transporteur } from '../models/livraison.model';
import { DeliveryUser } from '../backoffice/livraison/livraison-form/livraison-form.component';

@Injectable({ providedIn: 'root' })
export class LivraisonService {
  private base = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  // ── Livraisons ──────────────────────────────────────────
  getAllLivraisons(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/livraisons`);
  }
  getLivraisonById(id: number): Observable<Livraison> {
    return this.http.get<Livraison>(`${this.base}/livraisons/${id}`);
  }
  createLivraison(dto: Livraison): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.base}/livraisons`, dto);
  }
  updateStatus(id: number, dto: Partial<Livraison>): Observable<Livraison> {
    return this.http.put<Livraison>(`${this.base}/livraisons/${id}/status`, dto);
  }

  // ── Transporteurs ───────────────────────────────────────
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
    return this.http.delete<string>(`${this.base}/transporteurs/${id}`);
  }

  // ── Users DELIVERY ──────────────────────────────────────
  // Récupère tous les users avec le rôle DELIVERY
  getAllDeliveryUsers(): Observable<DeliveryUser[]> {
    return this.http.get<DeliveryUser[]>(`${this.base}/users`)
      .pipe(
        map(users => users.filter(u => u.role === 'DELIVERY'))
      );
  }
}