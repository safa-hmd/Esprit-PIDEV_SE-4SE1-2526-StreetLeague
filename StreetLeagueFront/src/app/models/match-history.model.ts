// src/app/models/match-history.model.ts
export type MatchStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED' | 'FINISHED';

export interface MatchHistoryDto {
  idMatch: number;
  matchDate: string;
  location: string;
  status: MatchStatus;
  scoreTeamA: number | null;
  scoreTeamB: number | null;
  teamAName: string;
  teamBName: string;
  captainAName: string;
  captainBName: string;
  sport: string;
  eloTeamA: number;
  eloTeamB: number;
}

export interface MatchResponse {
  idMatch: number;
  matchDate: string;
  location: string;
  status: MatchStatus;
  scoreTeamA: number | null;
  scoreTeamB: number | null;
  teamAName: string;
  teamBName: string;
  createdByEmail: string;
  captainAName: string;
  captainBName: string;
}