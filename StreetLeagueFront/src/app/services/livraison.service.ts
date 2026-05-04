import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Livraison, StatsAdmin } from '../models/livraison.model';

@Injectable({ providedIn: 'root' })
export class LivraisonService {

  private base = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  // ── Livraisons CRUD ───────────────────────────────────
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

  // ── Filtre par statut ─────────────────────────────────
  getLivraisonsByStatut(statut: string): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/livraisons/statut/${statut}`);
  }

  getLivraisonsByLivreur(livreurId: number): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/livraisons/livreur/${livreurId}`);
  }

  // ── PARTIE 3 : GPS Tracking livreur ──────────────────
  updateLocation(latitude: number, longitude: number): Observable<string> {
    return this.http.put(
      `${this.base}/livreurs/location`,
      { latitude, longitude },
      { responseType: 'text' }
    );
  }

  updateLivreurStatus(status: string): Observable<string> {
    return this.http.put(
      `${this.base}/livreurs/status?status=${status}`,
      {},
      { responseType: 'text' }
    );
  }

  // ── PARTIE 8 : Admin Dashboard ────────────────────────
  getAdminPending(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/admin/livraisons/pending`);
  }

  getAdminEnCours(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/admin/livraisons/en-cours`);
  }

  getAdminStats(): Observable<StatsAdmin> {
    return this.http.get<StatsAdmin>(`${this.base}/admin/stats`);
  }

  // ── Livreurs (users DELIVERY) ─────────────────────────
  getAllDeliveryUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/users`).pipe(
      map((users: any[]) => users.filter(u =>
        u.role === 'DELIVERY' || u.role === 'ROLE_DELIVERY'
      )),
      catchError(() => of([]))
    );
  }

getTournee(livreurId: number): Observable<any> {
  return this.http.get(`${this.base}/livraisons/tournee/${livreurId}`);
}

}