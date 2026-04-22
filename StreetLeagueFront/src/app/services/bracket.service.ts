import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BracketResponseDto, BracketType, MatchSlotDto, SubmitResultDto } from '../models/bracket.model';

@Injectable({ providedIn: 'root' })
export class BracketService {

  private readonly API = 'http://localhost:8086/StreetLeague/api/brackets';

  constructor(private http: HttpClient) {}

  generateBracket(tournamentId: number, type: BracketType): Observable<BracketResponseDto> {
    return this.http.post<BracketResponseDto>(
      `${this.API}/${tournamentId}/generate?type=${type}`, {}
    );
  }

  getBracket(tournamentId: number): Observable<BracketResponseDto> {
    return this.http.get<BracketResponseDto>(`${this.API}/${tournamentId}`);
  }

  submitResult(matchId: number, dto: SubmitResultDto): Observable<MatchSlotDto> {
    return this.http.patch<MatchSlotDto>(`${this.API}/matches/${matchId}/result`, dto);
  }

  getRound(tournamentId: number, round: number): Observable<MatchSlotDto[]> {
    return this.http.get<MatchSlotDto[]>(`${this.API}/${tournamentId}/round/${round}`);
  }
}