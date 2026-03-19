export interface Team {
  idTeam?: number;
  name: string;
  sport: string;
  description?: string;
  level: string;
  creationDate?: string;
  captainEmail?: string;      
  captainFullName?: string; 
   captainId?: number;  
  playerCount?: number;       
  victories?: number;
  defeats?: number;
  matches?: number;
}