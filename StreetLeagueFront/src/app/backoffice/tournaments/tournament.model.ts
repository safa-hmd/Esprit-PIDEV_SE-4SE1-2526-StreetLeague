// src/app/backoffice/tournaments/tournament.model.ts

export type SportType        = 'FOOTBALL' | 'BASKETBALL' | 'TENNIS' | 'PADEL' | 'VOLLEYBALL' | 'OTHER';
export type TournamentStatus = 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
export type TournamentType   = 'INDIVIDUAL' | 'TEAM';
export type RegistrationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';

export interface TournamentDto {
  id?: number;
  name: string;
  description?: string;
  sportType: SportType;
  tournamentType: TournamentType;
  status?: TournamentStatus;
  startDate: string;             // yyyy-MM-dd
  endDate: string;               // yyyy-MM-dd
  registrationDeadline: string;  // yyyy-MM-dd
  maxParticipants: number;
  fieldId?: number;        // ✅
  fieldName?: string;      // ✅
  fieldLocation?: string;  // ✅
  prizePool?: number;
  registeredCount?: number;
}

export interface TournamentRegistrationDto {
  id?: number;
  tournamentId: number;
  tournamentName?: string;
  playerId?: number;
  playerUsername?: string;
  teamId?: number;
  teamName?: string;
  status?: RegistrationStatus;
  registeredAt?: string;
}

export interface TournamentFilters {
  search: string;
  sport:  SportType | '';
  status: TournamentStatus | '';
}