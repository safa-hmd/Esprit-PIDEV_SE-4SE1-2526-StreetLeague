import { environment } from 'src/environments/environment';
// src/app/services/match.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchRequest, MatchResponse, MatchUpdateRequest } from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class MatchService {

  private base = `${environment.baseUrl}/match`;

  constructor(private http: HttpClient) {}

  // ── Auth Headers ─────────────────────────────────────────
private getHeaders(): HttpHeaders {
  const token = localStorage.getItem('TokenUserConnect');
  console.log('Token:', token); // ← vérifier qu'il n'est pas null
  return new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
}

 // POST /match/add?teamAId=1&teamBId=2&email=captain@mail.com
addMatch(match: MatchRequest, teamAId: number, teamBId: number): Observable<MatchResponse> {
  const email = localStorage.getItem('EmailUserConnect');
  return this.http.post<MatchResponse>(
    `${this.base}/add?teamAId=${teamAId}&teamBId=${teamBId}&email=${email}`,
    match,
    { headers: this.getHeaders() }
  );
}

// DELETE /match/delete/1?email=captain@mail.com
deleteMatch(idMatch: number): Observable<void> {
  const email = localStorage.getItem('EmailUserConnect');
  return this.http.delete<void>(
    `${this.base}/delete/${idMatch}?email=${email}`,
    { headers: this.getHeaders() }
  );
}

// PUT /match/update?email=captain@mail.com
updateMatch(match: any): Observable<MatchResponse> {
  const email = localStorage.getItem('EmailUserConnect');
  return this.http.put<MatchResponse>(
    `${this.base}/update?email=${email}`,
    match,
    { headers: this.getHeaders() }
  );
}

// GET /match/showMatchs
getAllMatchs(): Observable<MatchResponse[]> {
  return this.http.get<MatchResponse[]>(
    `${this.base}/showMatchs`,
    { headers: this.getHeaders() }
  );
}

// GET /match/showMatchById/1
getMatchById(id: number): Observable<MatchResponse> {
  return this.http.get<MatchResponse>(
    `${this.base}/showMatchById/${id}`,
    { headers: this.getHeaders() }
  );
}



// AJOUTER dans MatchService — ne pas toucher addMatch() existant
addMatchByEmail(
  match: any,
  teamAId: number,
  teamBId: number,
  email: string
): Observable<any> {
  return this.http.post(
    `${this.base}/add-by-email?teamAId=${teamAId}&teamBId=${teamBId}&email=${email}`,
    match,
    { headers: this.getHeaders() }
  );
}


 // ✅ NOUVELLE MÉTHODE DE RECHERCHE
  searchMatches(teamName: string, statuses: string[]): Observable<MatchResponse[]> {
    let params = new HttpParams()
      .set('teamName', teamName)
      .set('status', statuses.join(','));
    
    return this.http.get<MatchResponse[]>(`${this.base}/search`, { 
      params,
      headers: this.getHeaders() 
    });
  }

respondToMatch(matchId: number, captainId: number, accept: boolean): Observable<any> {
  return this.http.put(
   `${this.base}/${matchId}/respond?captainId=${captainId}&accept=${accept}`,
    {},
    { headers: this.getHeaders() }
  );
}
}
