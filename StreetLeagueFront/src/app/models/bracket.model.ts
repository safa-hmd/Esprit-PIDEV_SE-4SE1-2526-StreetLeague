export type BracketType = 'SINGLE_ELIMINATION' | 'ROUND_ROBIN';
export type TournamentMatchStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'BYE';

export interface ParticipantDto {
  id: number;
  name: string;
  type: 'TEAM' | 'PLAYER';
}

export interface MatchSlotDto {
  id: number;
  round: number;
  position: number;
  status: TournamentMatchStatus;
  participantA?: ParticipantDto;
  participantB?: ParticipantDto;
  winner?: ParticipantDto;
  matchDate?: string;
  location?: string;
  nextMatchId?: number;
  nextMatchSlot?: string;
}

export interface BracketResponseDto {
  tournamentId: number;
  tournamentName: string;
  bracketType: BracketType;
  totalRounds: number;
  rounds: { [round: number]: MatchSlotDto[] };
}

export interface SubmitResultDto {
  winnerId: number;
  winnerIsTeam: boolean;
  scoreA?: number;
  scoreB?: number;
}