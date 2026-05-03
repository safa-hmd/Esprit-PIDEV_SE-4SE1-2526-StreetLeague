export interface SponsorDTO {
  id: number;
  nom: string;
  type: string;
  contactEmail: string;
  telephone: string;
  address: string;
  status?: 'PENDING' | 'APPROUVÉ' | 'REJETÉ';
  dateCreation?: string;
}

