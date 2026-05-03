// src/app/shared/models/match-candidate.model.ts
export interface MatchCandidate {
  teamId: number;
  teamName: string;
  sport: string;
  eloScore: number;
  eloFitScore: number;
  h2hScore: number;
  matchCompatibilityScore: number;
}