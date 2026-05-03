export interface Team {
  // DB fields (from teams table)
  idTeam?: number;
  id?: number;
  city?: string;
  name: string;
  level?: string;
  sport?: string;

  creationDate?: string;

  description?: string;



  captainEmail?: string;      
  captainFullName?: string; 
  captainRole?: string;
   captainId?: number;  
  playerCount?: number;       

  victories?: number;
  defeats?: number;
  matches?: number;
   playerEmails?:    string[]; 
}