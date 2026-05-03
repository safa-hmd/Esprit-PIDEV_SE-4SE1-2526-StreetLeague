export interface MatchRequest {
  teamAId?: number;
  teamBId?: number;
  location?: string;
  matchDate?: string;
}

export interface MatchResponse {
  idMatch: number;
  teamAId?: number;
  teamAName?: string;
  captainAName?: string;
  captainAEmail?: string;
  teamBId?: number;
  teamBName?: string;
  captainBName?: string;
  captainBEmail?: string;
  location?: string;
  matchDate?: string;
  scoreTeamA?: number;
  scoreTeamB?: number;
  status?: string;
}

export interface MatchUpdateRequest {
  location?: string;
  matchDate?: string;
  scoreTeamA?: number;
  scoreTeamB?: number;
  status?: string;
}
