import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MatchCandidateResponse {
  teamId:                  number;
  teamName:                string;
  sport:                   string;
  eloScore:                number;
  eloFitScore:             number;
  h2hScore:                number;
  matchCompatibilityScore: number;
}

@Injectable({ providedIn: 'root' })
export class MatchmakingService {

  private base = 'http://localhost:8086/StreetLeague/matchmaking';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect');
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  suggestOpponents(teamId: number): Observable<MatchCandidateResponse[]> {
    return this.http.get<MatchCandidateResponse[]>(
      `${this.base}/suggest/${teamId}`,
      { headers: this.getHeaders() }
    );
  }

  updateElo(matchId: number): Observable<void> {
    return this.http.put<void>(
      `${this.base}/elo/${matchId}`,
      {},
      { headers: this.getHeaders() }
    );
  }
}