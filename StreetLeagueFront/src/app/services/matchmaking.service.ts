// src/app/services/matchmaking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchCandidate } from '../models/match-candidate.model';

@Injectable({ providedIn: 'root' })
export class MatchmakingService {
  private readonly API = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  /**
   * Récupère les adversaires compatibles.
   * @param sport  Nom du sport (ex: "FOOTBALL"), ou "ALL" pour tous, ou null pour sport de l'équipe.
   */
  getCandidates(
    teamId: number,
    location: string,
    top = 50,
    sport?: string | null
  ): Observable<MatchCandidate[]> {
    let params = new HttpParams()
      .set('teamId', teamId.toString())
      .set('location', location)
      .set('top', top.toString());

    if (sport && sport !== 'SAME') {
      params = params.set('sport', sport);
    }

    return this.http.get<MatchCandidate[]>(`${this.API}/matchmaking/candidates`, { params });
  }

  /** Récupère la liste des sports disponibles depuis le backend. */
  getAvailableSports(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API}/matchmaking/sports`);
  }

  challengeTeam(
    challengerTeamId: number,
    opponentTeamId: number,
    location: string,
    email: string
  ): Observable<any> {
    const params = new HttpParams()
      .set('teamAId', challengerTeamId.toString())
      .set('teamBId', opponentTeamId.toString())
      .set('email', email);

    const matchDate = new Date();
    matchDate.setDate(matchDate.getDate() + 7);
    matchDate.setHours(15, 0, 0, 0);
    const pad = (n: number) => String(n).padStart(2, '0');
    const formattedDate =
      `${matchDate.getFullYear()}-` +
      `${pad(matchDate.getMonth() + 1)}-` +
      `${pad(matchDate.getDate())}T` +
      `${pad(matchDate.getHours())}:` +
      `${pad(matchDate.getMinutes())}:00`;

    return this.http.post(
      `http://localhost:8086/StreetLeague/match/add`,
      { matchDate: formattedDate, location },
      { params }
    );
  }
}