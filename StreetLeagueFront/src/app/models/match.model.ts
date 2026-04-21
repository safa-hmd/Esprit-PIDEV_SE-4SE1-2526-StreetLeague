// src/app/models/match.model.ts

export interface Match {
  idMatch?: number;
  matchDate?: string;           // ISO date string ex: "2025-06-15T18:00:00"
  location?: string;
  status?: 'SCHEDULED' | 'ONGOING' | 'FINISHED' | 'CANCELLED';
  scoreTeamA?: number;
  scoreTeamB?: number;
  teamA?: {
    id: number;
    name: string;
  };
  teamB?: {
    id: number;
    name: string;
  };
  captain?: {
    idUser: number;
    fullName: string;
    email: string;
  };
}

// ── Request DTOs ────────────────────────────────────────────

export interface MatchRequest {
  matchDate: string;
  location: string;
  status?: string;
  scoreTeamA?: number;
  scoreTeamB?: number;
}

export interface MatchUpdateRequest {
  idMatch: number;
  date?: string;
  location?: string;
  status?: string;
  scoreTeamA?: number;
  scoreTeamB?: number;
}

// ── Response DTO 

export interface MatchResponse {
  idMatch: number;
  matchDate: string;
  location: string;
  status: string;
  scoreTeamA: number;
  scoreTeamB: number;
  teamAName: string;
  teamBName: string;
  captainAName?: string;    
  captainBName?: string;
  captainName: string;
  captainAEmail?: string;   // ← ajouter
  captainBEmail?: string; 
}