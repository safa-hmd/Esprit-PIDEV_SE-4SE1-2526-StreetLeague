// src/app/services/team.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Team } from '../models/team.model';
import { LeaderboardDto } from '../models/leaderboard.model';

@Injectable({ providedIn: 'root' })
export class TeamService {

  private base = 'http://localhost:8086/StreetLeague/team';

  constructor(private http: HttpClient) {}

    private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

getAllTeams(): Observable<Team[]> {
  return this.http.get<Team[]>(`${this.base}/showTeams`, { headers: this.getHeaders() });
}

// Après addTeam → le backend retourne directement le TeamResponse avec captainFullName
addTeam(team: Team): Observable<Team> {
  const email = localStorage.getItem('EmailUserConnect');
  return this.http.post<Team>(
    `${this.base}/add?email=${email}`,
    team,
    { headers: this.getHeaders() }
  );
}

getMyTeams(captainId: number): Observable<Team[]> {
  return this.http.get<Team[]>(`${this.base}/my-teams?captainId=${captainId}`, { headers: this.getHeaders() });
}
  // GET /team/showTeamById/1
  getTeamById(id: number): Observable<Team> {
    return this.http.get<Team>(`${this.base}/showTeamById/${id}`, { headers: this.getHeaders() });
  }

deleteTeam(idTeam: number, email: string): Observable<void> {
  const token = localStorage.getItem('TokenUserConnect');
  const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  return this.http.delete<void>(
    `${this.base}/delete/${idTeam}?email=${email}`,
    { headers }
  );
}

updateTeam(team: Team, email: string): Observable<Team> {
  return this.http.put<Team>(
    `${this.base}/update/${team.idTeam}?email=${email}`,
    team,
    { headers: this.getHeaders() }
  );
}

joinTeam(idTeam: number, email: string): Observable<void> {
  return this.http.post<void>(
    `${this.base}/${idTeam}/join?email=${email}`,  // ← URL corrigée
    {}, { headers: this.getHeaders() }
  );
}

// team.service.ts
leaveTeam(idTeam: number, email: string): Observable<void> {
  return this.http.delete<void>(
    `${this.base}/${idTeam}/leave?email=${email}`,
    { headers: this.getHeaders() }
  );
}

// Ajouter dans team.service.ts
getTeamsByCoach(): Observable<Team[]> {
  const email = localStorage.getItem('EmailUserConnect');
  return this.http.get<Team[]>(
    `${this.base}/myTeams?email=${email}`,
    { headers: this.getHeaders() }
  );
}

// Ajouter update stats dans team.service.ts
// team.service.ts  ← dans cette fonction existante
updateTeamStats(idTeam: number, victories: number, defeats: number, matches: number): Observable<Team> {
  const email = localStorage.getItem('EmailUserConnect');  // ligne ~85 de ton fichier
  
  if (!email) {
    console.error('❌ No email found in localStorage!');
  }
  
  const body = { victories, defeats, matches, email };
  console.log('📤 Sending stats update:', body); // ← tu verras le body dans la console
  
  return this.http.put<Team>(
    `${this.base}/updateStats/${idTeam}`,
    body,
    { headers: this.getHeaders() }
  );
}

getLeaderboard(sport: string): Observable<LeaderboardDto[]> {
  return this.http.get<LeaderboardDto[]>(
    `${this.base}/leaderboard?sport=${sport}`,
    { headers: this.getHeaders() }
  );
}
}