// src/app/frontoffice/services/matchmaking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchCandidate } from '../models/match-candidate.model';

@Injectable({ providedIn: 'root' })
export class MatchmakingService {
  private readonly API = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

getCandidates(teamId: number, location: string, top = 5): Observable<MatchCandidate[]> {
  const encodedLocation = encodeURIComponent(location); // "36.8065%2C10.1815"
  return this.http.get<MatchCandidate[]>(
    `${this.API}/matchmaking/candidates?teamId=${teamId}&location=${encodedLocation}&top=${top}`
  );
}

// matchmaking.service.ts
challengeTeam(challengerTeamId: number, opponentTeamId: number, location: string): Observable<any> {
  // Utiliser l'endpoint existant /match/add
  const params = new HttpParams()
    .set('teamAId', challengerTeamId.toString())
    .set('teamBId', opponentTeamId.toString())
    .set('email', this.getCurrentUserEmail()); // Récupère l'email du user connecté
  
  const body = {
    matchDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // Date par défaut +7 jours
    location: location
  };
  
  return this.http.post(`${this.API}/match/add`, body, { params });
}

// Ajoute cette méthode pour récupérer l'email
private getCurrentUserEmail(): string {
  // Récupère depuis ton AuthService ou localStorage
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  return user.email || 'default@test.com';
}
}