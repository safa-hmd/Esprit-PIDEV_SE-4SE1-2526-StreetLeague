import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Livraison, StatsAdmin } from '../models/livraison.model';

@Injectable({ providedIn: 'root' })
export class LivraisonService {

  private base = 'http://localhost:8086/StreetLeague/livraisons';
  private baseApi = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  // ── Livraisons CRUD ───────────────────────────────────
  getAllLivraisons(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}`);
  }

  getLivraisonById(id: number): Observable<Livraison> {
    return this.http.get<Livraison>(`${this.base}/${id}`);
  }

  createLivraison(dto: any): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.base}`, dto);
  }

  updateStatus(id: number, dto: any): Observable<Livraison> {
    return this.http.put<Livraison>(`${this.base}/${id}/status`, dto);
  }

  // ── Filtre par statut ─────────────────────────────────
  getLivraisonsByStatut(statut: string): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/statut/${statut}`);
  }

  getLivraisonsByLivreur(livreurId: number): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.base}/livreur/${livreurId}`);
  }

  // ── Tournée optimisée (TSP) ───────────────────────────
  getTournee(livreurId: number): Observable<any> {
    return this.http.get(`${this.base}/tournee/${livreurId}`);
  }

  // ── GPS Tracking livreur ──────────────────────────────
  updateLocation(latitude: number, longitude: number): Observable<string> {
    return this.http.put(
      `${this.baseApi}/livreurs/location`,
      { latitude, longitude },
      { responseType: 'text' }
    );
  }

  updateLivreurStatus(status: string): Observable<string> {
    return this.http.put(
      `${this.baseApi}/livreurs/status?status=${status}`,
      {},
      { responseType: 'text' }
    );
  }

  // ── Admin Dashboard ────────────────────────────────────
  getAdminPending(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.baseApi}/admin/livraisons/pending`);
  }

  getAdminEnCours(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.baseApi}/admin/livraisons/en-cours`);
  }

  getAdminStats(): Observable<StatsAdmin> {
    return this.http.get<StatsAdmin>(`${this.baseApi}/admin/stats`);
  }

  // ── Utilisateurs livreurs ──────────────────────────────
  getAllDeliveryUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseApi}/users`).pipe(
      map((users: any[]) => users.filter(u =>
        u.role === 'DELIVERY' || u.role === 'ROLE_DELIVERY'
      )),
      catchError(() => of([]))
    );
  }
}