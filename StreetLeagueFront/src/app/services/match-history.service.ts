// src/app/services/match-history.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchHistoryDto, MatchResponse, MatchStatus } from '../models/match-history.model';

@Injectable({ providedIn: 'root' })
export class MatchHistoryService {

  private base    = 'http://localhost:8086/StreetLeague/matches-history';
  private baseMatch = 'http://localhost:8086/StreetLeague/matches';

  constructor(private http: HttpClient) {}

  // JPQL — historique enrichi (JOIN entre Match, Team A, Team B, Captain A, Captain B)
  getEnrichedHistory(
    status?: MatchStatus,
    from?: string,
    to?: string
  ): Observable<MatchHistoryDto[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    if (from)   params = params.set('from', from);
    if (to)     params = params.set('to', to);
    return this.http.get<MatchHistoryDto[]>(`${this.base}/enriched`, { params });
  }

  // Spring Data Keywords — recherche multi-table (TeamA.name OR TeamB.name OR location)
  searchMatches(
    keyword: string,
    statuses?: MatchStatus[]
  ): Observable<MatchResponse[]> {
    let params = new HttpParams().set('keyword', keyword);
    if (statuses && statuses.length > 0) {
      statuses.forEach(s => params = params.append('status', s));
    }
    return this.http.get<MatchResponse[]>(`${this.baseMatch}/search`, { params });
  }
}