import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, from, switchMap, throwError } from 'rxjs';

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

  // ── Get GPS position as Promise ──────────────────────────────────
  private getPosition(): Promise<GeolocationPosition> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation not supported'));
        return;
      }
      navigator.geolocation.getCurrentPosition(resolve, reject, {
        timeout: 8000,
        maximumAge: 60000,
        enableHighAccuracy: false
      });
    });
  }

  // ── Field recommendations (uses live GPS) ───────────────────────
  getFieldRecommendations(): Observable<FieldRecommendation[]> {
    const userId = this.getCurrentUserId();
    if (!userId) {
      return throwError(() => new Error('userId introuvable — vérifie ta connexion'));
    }

    return from(this.getPosition()).pipe(
      switchMap(pos =>
        this.http.get<FieldRecommendation[]>(
          `${this.BASE}/fields/${userId}?lat=${pos.coords.latitude}&lng=${pos.coords.longitude}`,
          { headers: this.getHeaders() }
        )
      )
    );
  }

  // ── Slot recommendations for a selected field ───────────────────
  getSlotRecommendations(
    fieldId: number,
    lat: number,
    lng: number
  ): Observable<SlotDto[]> {
    const userId = this.getCurrentUserId();
    return this.http.get<SlotDto[]>(
      `${this.BASE}/slot/${userId}/${fieldId}?lat=${lat}&lng=${lng}`,
      { headers: this.getHeaders() }
    );
  }
}