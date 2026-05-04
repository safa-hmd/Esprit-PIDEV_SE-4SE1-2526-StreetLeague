export type LivraisonStatus =
  | 'PREPAREE'
  | 'ASSIGNEE'
  | 'EXPEDIEE'
  | 'OUT_FOR_DELIVERY'
  | 'LIVREE'
  | 'ECHEC';

export type Priorite = 'LOW' | 'NORMAL' | 'HIGH';
export type LivreurStatus = 'DISPONIBLE' | 'OCCUPE' | 'OFFLINE';
export type CommandeStatus = 'EN_ATTENTE' | 'VALIDEE' | 'ANNULEE' | 'LIVREE' | 'PREPAREE';

// ✅ DTO aplati retourné par le backend — plus d'objet nested
export interface Livraison {
  id?: number;

  // Commande
  commandeId?: number;

  // Livreur aplati (plus d'objet livreur nested)
  livreurId?: number;
  livreurNom?: string;
  livreurEmail?: string;

  // Adresse & GPS
  adresse: string;
  latitudeClient?: number;
  longitudeClient?: number;

  // Dates
  dateCreation?: string;
  dateAffectation?: string;
  dateLivraison?: string;

  // Logistique
  fraisLivraison: number;
  distance?: number;
  priorite?: Priorite;
  scoreAffectation?: number;

  // Statut
  statut: LivraisonStatus;
  nbTentatives?: number;
  motifEchec?: string;
}

export interface StatsAdmin {
  nbLivraisonsEnCours: number;
  nbLivreursDisponibles: number;
  livreurLePlusActif: string;
  tauxLivraisonsReussies: number;
}

export interface Commande {
  id?: number;
  montantTotal: number;
  statut: CommandeStatus;
  latitudeClient?: number;
  longitudeClient?: number;
  adresseLivraison?: string;
}