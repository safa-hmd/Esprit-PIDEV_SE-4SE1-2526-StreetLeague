// src/app/models/leaderboard.model.ts
export interface LeaderboardDto {
  teamId: number;
  teamName: string;
  sport: string;
  captainFullName: string;
  captainEmail: string;
  victories: number;
  defeats: number;
  matches: number;
  points: number;
  lastMatchDate: string | null;
  lastOpponentName: string | null;
}