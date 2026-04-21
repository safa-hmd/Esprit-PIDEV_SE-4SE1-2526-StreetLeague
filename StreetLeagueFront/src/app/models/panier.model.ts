export interface AddToCartDTO {
  userId: number;
  materielId: number;
  quantite: number;
}
 
export interface LignePanierResponse {
  ligneId: number;
  materielId: number;
  materielNom: string;
  prix: number;
  quantite: number;
  sousTotal: number;
}
 
export interface PanierResponse {
  panierId: number;
  userId: number;
  lignes: LignePanierResponse[];
  total: number;
}