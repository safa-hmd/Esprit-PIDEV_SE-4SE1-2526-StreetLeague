import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

// DTO TypeScript aligné sur le Pydantic de FastAPI
export interface RecommendationResponse {
  user_id: number;
  recommendations: number[];
  confidence: number;
}

@Injectable({ providedIn: 'root' })
export class RecommendationService {
  // URL du microservice ML (déjà configuré avec CORS dans app.py)
  private readonly FASTAPI_URL = 'http://localhost:8000/recommend';

  constructor(private http: HttpClient) {}

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