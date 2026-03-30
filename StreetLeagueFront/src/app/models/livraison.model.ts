export type LivraisonStatus = 'PREPAREE' | 'EXPEDIEE' | 'EN_COURS' | 'LIVREE' | 'ECHEC';
export type CommandeStatus = 'EN_ATTENTE' | 'VALIDEE' | 'ANNULEE' | 'LIVREE' | 'PREPAREE';

// L'objet User retourné en nested par le backend
export interface LivreurRef {
  id: number;
  fullName: string;
  email: string;
  role: string;
}

export interface Livraison {
  id?: number;
  commandeId: number;
  transporteurId: number;

  // Backend retourne soit livreurId (flat) soit livreur (nested object)
  livreurId?: number;
  livreur?: LivreurRef;   // ← nested object retourné par Spring

  adresse: string;
  fraisLivraison: number;
  statut: LivraisonStatus;
}

export interface Transporteur {
  id?: number;
  nomSociete: string;
  telephone?: string;
  email?: string;
}

export interface Commande {
  id?: number;
  montantTotal: number;
  statut: CommandeStatus;
}