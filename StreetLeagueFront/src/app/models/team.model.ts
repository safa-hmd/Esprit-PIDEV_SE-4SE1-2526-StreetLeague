export interface Team {
  // DB fields (from teams table)
  idTeam?: number;
  id?: number;
  city?: string;
  name: string;
  level?: string;
  sport?: string;
  captainId?: number;
  creationDate?: string;
  description?: string;

  // Computed/joined fields used by components
  captainFullName?: string;
  captainEmail?: string;
  playerCount?: number;
  victories?: number;
  defeats?: number;
  matches?: number;
   playerEmails?:    string[]; 
}