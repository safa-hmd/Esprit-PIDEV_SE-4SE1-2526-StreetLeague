// src/app/services/tournament.service.ts
import { Injectable }             from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

import {
  TournamentDto,
  TournamentRegistrationDto,
  TournamentFilters,
  SportType,
  TournamentStatus
} from '../backoffice/tournaments/tournament.model';

@Injectable({ providedIn: 'root' })
export class TournamentService {

  private readonly API  = 'http://localhost:8086/StreetLeague/api/tournaments';
  private readonly REG  = 'http://localhost:8086/StreetLeague/api/registrations';

  private filtersSubject = new BehaviorSubject<TournamentFilters>({ search: '', sport: '', status: '' });
  filters$ = this.filtersSubject.asObservable();

  private toastSubject = new BehaviorSubject<string | null>(null);
  toast$ = this.toastSubject.asObservable();

  constructor(private http: HttpClient) {}

  // ── TOURNAMENTS ───────────────────────────────────────────────────────────

  getAll(): Observable<TournamentDto[]> {
    return this.http.get<TournamentDto[]>(this.API);
  }

  getById(id: number): Observable<TournamentDto> {
    return this.http.get<TournamentDto>(`${this.API}/${id}`);
  }

  create(dto: TournamentDto): Observable<TournamentDto> {
    return this.http.post<TournamentDto>(this.API, dto);
  }

  update(id: number, dto: TournamentDto): Observable<TournamentDto> {
    return this.http.put<TournamentDto>(`${this.API}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  cancel(id: number): Observable<TournamentDto> {
    return this.http.patch<TournamentDto>(`${this.API}/${id}/cancel`, {});
  }

  // ── REGISTRATIONS ─────────────────────────────────────────────────────────

  registerPlayer(tournamentId: number, playerId: number): Observable<TournamentRegistrationDto> {
    const params = new HttpParams()
      .set('tournamentId', tournamentId)
      .set('playerId', playerId);
    return this.http.post<TournamentRegistrationDto>(`${this.REG}/player`, null, { params });
  }

  registerTeam(tournamentId: number, teamId: number): Observable<TournamentRegistrationDto> {
    const params = new HttpParams()
      .set('tournamentId', tournamentId)
      .set('teamId', teamId);
    return this.http.post<TournamentRegistrationDto>(`${this.REG}/team`, null, { params });
  }

  cancelRegistration(id: number): Observable<TournamentRegistrationDto> {
    return this.http.patch<TournamentRegistrationDto>(`${this.REG}/${id}/cancel`, {});
  }

  getRegistrationsByTournament(id: number): Observable<TournamentRegistrationDto[]> {
    return this.http.get<TournamentRegistrationDto[]>(`${this.REG}/tournament/${id}`);
  }
    // ✅ AJOUT 1 — récupérer les inscriptions d'un joueur
  getRegistrationsByPlayer(playerId: number): Observable<TournamentRegistrationDto[]> {
    return this.http.get<TournamentRegistrationDto[]>(`${this.REG}/player/${playerId}`);
  }

  // ✅ AJOUT 2 — récupérer les inscriptions d'une équipe
  getRegistrationsByTeam(teamId: number): Observable<TournamentRegistrationDto[]> {
    return this.http.get<TournamentRegistrationDto[]>(`${this.REG}/team/${teamId}`);
  }
  /*// ✅ AJOUT 3 — récupérer une registration par id (utilisé dans getById)
  getRegistrationById(id: number): Observable<TournamentRegistrationDto> {
    return this.http.get<TournamentRegistrationDto>(`${this.REG}/${id}`);
  } */

  //  Admin accept a pending registration
  acceptRegistration(id: number): Observable<TournamentRegistrationDto> {
    return this.http.patch<TournamentRegistrationDto>(`${this.REG}/${id}/accept`, {});
  }

  // Admin reject a pending registration
  rejectRegistration(id: number): Observable<TournamentRegistrationDto> {
    return this.http.patch<TournamentRegistrationDto>(`${this.REG}/${id}/reject`, {});
  }

  // ── FILTERS ───────────────────────────────────────────────────────────────

  updateFilters(partial: Partial<TournamentFilters>): void {
    this.filtersSubject.next({ ...this.filtersSubject.getValue(), ...partial });
  }

  getFilters(): TournamentFilters {
    return this.filtersSubject.getValue();
  }

  applyFilters(list: TournamentDto[], f: TournamentFilters): TournamentDto[] {
    return list.filter(t => {
      const matchSearch = !f.search ||
        t.name.toLowerCase().includes(f.search.toLowerCase()) ||
        (t.location ?? '').toLowerCase().includes(f.search.toLowerCase());
      const matchSport  = !f.sport  || t.sportType === f.sport;
      const matchStatus = !f.status || t.status    === f.status;
      return matchSearch && matchSport && matchStatus;
    });
  }

  // ── TOAST ─────────────────────────────────────────────────────────────────

  showToast(msg: string): void {
    this.toastSubject.next(msg);
    setTimeout(() => this.toastSubject.next(null), 3500);
  }

  // ── UI HELPERS ────────────────────────────────────────────────────────────

  getProgress(t: TournamentDto): number {
    return t.maxParticipants
      ? Math.round(((t.registeredCount ?? 0) / t.maxParticipants) * 100)
      : 0;
  }

  sportBadge(s: SportType): string {
    const map: Record<SportType, string> = {
      FOOTBALL:   'badge-gray',
      BASKETBALL: 'badge-blue',
      TENNIS:     'badge-green',
      PADEL:      'badge-orange',
      VOLLEYBALL: 'badge-orange',
      OTHER:      'badge-gray'
    };
    return map[s] ?? 'badge-gray';
  }

  statusBadge(s: TournamentStatus): string {
    const map: Record<TournamentStatus, string> = {
      UPCOMING:  'badge-green',
      ONGOING:   'badge-orange',
      COMPLETED: 'badge-gray',
      CANCELLED: 'badge-red'
    };
    return map[s] ?? 'badge-gray';
  }

  statusLabel(s: TournamentStatus): string {
    const map: Record<TournamentStatus, string> = {
      UPCOMING:  'Open',
      ONGOING:   'In Progress',
      COMPLETED: 'Completed',
      CANCELLED: 'Cancelled'
    };
    return map[s] ?? s;
  }

  formatDate(t: TournamentDto): string {
    if (!t.endDate || t.startDate === t.endDate) return t.startDate;
    return `${t.startDate} → ${t.endDate}`;
  }
}