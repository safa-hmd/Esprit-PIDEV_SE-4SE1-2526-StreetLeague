export interface Materiel {
  id?: number;
  nom: string;
  description: string;
  prix: number;
  quantiteStock: number;
  imageUrl?: string;
  categorieId: number;
  categorieNom?: string;
}
 
export interface Category {
  id?: number;
  nom: string;
  description?: string;
  nombreMateriels?: number;
}