export interface ContratSponsorDTO {
  id: number;
  sponsorId: number;
  equipeId: number;
  montant: number;
  dateDebut: Date;
  dateFin: Date;
  statut: string;
  conditions: string;
}
