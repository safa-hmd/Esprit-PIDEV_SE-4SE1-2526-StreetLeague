export interface ContractSponsorDTO {
  id: number;
  sponsorId: number;
  sponsorNom?: string;
  equipeId: number;
  montantTotal: number;
  montant?: number;
  dateDebut: string;
  dateFin: string;
  dateCreation?: string;
  statut: string;
  conditions: string;
  titre?: string;
  description?: string;
  typeContrat?: string;
}
