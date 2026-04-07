import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Livraison, Transporteur } from '../models/livraison.model';

@Injectable({ providedIn: 'root' })
export class LivraisonService {
  private base = 'http://localhost:8086/StreetLeague/livraisons';

  constructor(private http: HttpClient) {}

  // ── Livraisons ────────────────────────────────────────
  getAllLivraisons(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}`); // ✅ correct
  }

  getLivraisonById(id: number): Observable<Livraison> {
    return this.http.get<Livraison>(`${this.base}/${id}`); // ✅ supprime /livraisons/
  }

  createLivraison(dto: any): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.base}`, dto); // ✅ supprime /livraisons
  }

  updateStatus(id: number, dto: any): Observable<Livraison> {
    return this.http.put<Livraison>(`${this.base}/${id}/status`, dto); // ✅ supprime /livraisons/
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

  getAllDeliveryUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/users`).pipe(
      map((users: any[]) => users.filter(u =>
        u.role === 'DELIVERY' || u.role === 'ROLE_DELIVERY'
      )),
      catchError(() => of([]))
    );
  }
}