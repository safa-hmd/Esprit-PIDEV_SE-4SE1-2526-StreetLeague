import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, catchError, from, map, of, switchMap } from 'rxjs';

export interface FieldRecommendation {
  fieldId:        number;
  fieldName:      string;
  fieldLocation:  string;
  fieldCapacity:  number;
  pricePerHour:   number;
  fieldAvailable: boolean;
  aiScore:        number;          // 0.0 – 1.0
  recommendation: 'EXCELLENT' | 'ACCEPTABLE' | 'DECONSEILLE';
  distanceKm:     number;
  weather:        string;
  weatherScore:   number;
  bestSlots?:     SlotDto[];
}

export interface SlotDto {
  dayName:   string;
  hour:      number;
  label:     string;
  score:     number;   // 0.0 – 1.0
  rec:       string;
  startTime: string;
  endTime:   string;

}
interface RecommendationResponse {
  recommendations: number[];
}

@Injectable({ providedIn: 'root' })
export class RecommendationService {


  // ✅ Single source of truth for API base URL
  private readonly BASE = 'http://localhost:8086/StreetLeague/api/recommend';

  constructor(private http: HttpClient) {}

  // ── Auth header ─────────────────────────────────────────────────
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect');
    return token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();
  }

  // ── Resolve current userId from localStorage / JWT ───────────────
  getCurrentUserId(): number {
    const stored = localStorage.getItem('UserIdConnect');
    if (stored && stored !== 'undefined' && stored !== 'null') {
      const n = Number(stored);
      if (!isNaN(n) && n > 0) return n;
    }
    const token = localStorage.getItem('TokenUserConnect');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return Number(payload.id || payload.userId || payload.user_id || 0);
      } catch (_) {}
    }
    return 0;
  }

  // ── Get GPS position as Promise — resolves null if unavailable ──
  private getPositionOrNull(): Promise<{ lat: number; lng: number } | null> {
    return new Promise(resolve => {
      if (!navigator.geolocation) { resolve(null); return; }
      navigator.geolocation.getCurrentPosition(
        pos => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
        ()  => resolve(null),   // GPS denied → null (not an error)
        { timeout: 8000, maximumAge: 60000, enableHighAccuracy: false }
      );
    });
  }

  // ── Field recommendations (GPS optional — falls back gracefully) ─
  getFieldRecommendations(): Observable<FieldRecommendation[]> {
    const userId = this.getCurrentUserId();
    if (!userId) {
      // Return empty array instead of throwing — UI handles empty state
      return of([]);
    }

    return from(this.getPositionOrNull()).pipe(
      switchMap(gps => {
        let params = new HttpParams();
        if (gps) {
          params = params.set('lat', gps.lat.toString()).set('lng', gps.lng.toString());
        }
        return this.http.get<FieldRecommendation[]>(
          `${this.BASE}/fields/${userId}`,
          { headers: this.getHeaders(), params }
        );
      })
    );
  }

  // ── Slot recommendations for a selected field ───────────────────
  getSlotRecommendations(
    fieldId: number,
    lat: number,
    lng: number
  ): Observable<SlotDto[]> {
    const userId = this.getCurrentUserId();
    let params = new HttpParams();
    if (lat && lng) {
      params = params.set('lat', lat.toString()).set('lng', lng.toString());
    }
    return this.http.get<SlotDto[]>(
      `${this.BASE}/slot/${userId}/${fieldId}`,
      { headers: this.getHeaders(), params }
    );
  }

  // URL du microservice ML (déjà configuré avec CORS dans app.py)
  private readonly FASTAPI_URL = 'http://localhost:8000/recommend';



  /**
   * Appelle l'API ML et retourne la liste des IDs produits recommandés.
   * Fallback silencieux si l'API est indisponible (conforme au PDF: "Consommation robuste").
   */
  getRecommendations(userId: number, excludeIds: number[] = [], topK: number = 4): Observable<number[]> {
    const payload = { user_id: userId, top_k: topK, exclude_ids: excludeIds };
    
    return this.http.post<RecommendationResponse>(this.FASTAPI_URL, payload).pipe(
      map(res => res.recommendations),
      catchError(err => {
        console.warn('⚠️ ML API unreachable → Fallback empty recommendations', err);
        return of([]); // Retourne [] au lieu de bloquer l'UI
      })
    );
  }

}