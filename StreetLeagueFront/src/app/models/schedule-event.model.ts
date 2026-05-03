export interface ScheduleEvent {
  id:                number;
  type:              'TRAINING' | 'MATCH' | 'TOURNAMENT';
  title:             string;
  startTime:         string;     // always ISO string after normalization
  endTime:           string;     // always ISO string after normalization
  description?:      string;
  location?:         string;
  status?:           string;
  color?:            string;
  hasConflict?:      boolean;
  conflictReason?:   string;

  // Team info
  teamName?:         string;
  coachName?:        string;
  opponentTeamName?: string;
  scoreTeamA?:       number;
  scoreTeamB?:       number;

  // AI scoring
  aiScore?:          number;     // 0.0 – 1.0
  recommendation?:   string;    // EXCELLENT | ACCEPTABLE | DECONSEILLE

  // Tournament
  tournamentName?:     string;
  tournamentType?:     string;
  prizePool?:          number;
  sportType?:          string;
  tournamentEndDate?:  string;

  // AI-recommended field for this event
  recommendedFieldId?:       number;
  recommendedFieldName?:     string;
  recommendedFieldLocation?: string;
  recommendedFieldLat?:      number;
  recommendedFieldLng?:      number;
  recommendedFieldDist?:     number;
  recommendedFieldScore?:    number;
  recommendedFieldRec?:      string;
}