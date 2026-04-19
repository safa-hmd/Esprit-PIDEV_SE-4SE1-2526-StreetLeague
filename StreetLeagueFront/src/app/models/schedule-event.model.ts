export interface ScheduleEvent {
  id: number;
  type: 'TRAINING' | 'MATCH';
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  location: string;
  status: string;
  teamName: string;
  coachName?: string;
  opponentTeamName?: string;
  scoreTeamA?: number;
  scoreTeamB?: number;
  hasConflict: boolean;
  conflictReason?: string;
  color: string;
  aiScore?: number;
  recommendation?: string;
}