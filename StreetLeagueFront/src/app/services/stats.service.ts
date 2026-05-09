import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CaJourDTO {
  date:        string;
  ca:          number;
  nbCommandes: number;
}

export interface TopProduitDTO {
  materielId:    number;
  nom:           string;
  quantiteVendue: number;
  caGenere:      number;
  stockActuel:   number;
  pctDuTotal:    number;
}

export interface LivreurPerfDTO {
  livreurId:              number;
  nom:                    string;
  totalLivraisons:        number;
  livraisonsReussies:     number;
  tauxReussite:           number;
  tempsLivraisonMoyen:    number;
  scoreAffectationMoyen:  number;
  niveau:                 'excellent' | 'bon' | 'faible';
}

export interface DashboardStatsDTO {
  caTotalTND:                number;
  nbCommandes:               number;
  nbLivreursActifs:          number;
  nbProduitsStockCritique:   number;
  caEvolutionPct:            number;
  evolutionCA:               CaJourDTO[];
  livraisonsPreparees:       number;
  livraisonsAssignees:       number;
  livraisonsExpediees:       number;
  livraisonsLivrees:         number;
  livraisonsEchecs:          number;
  topProduits:               TopProduitDTO[];
  performanceLivreurs:       LivreurPerfDTO[];
}

@Injectable({ providedIn: 'root' })
export class StatsService {

  private readonly API = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}
getDashboard(jours: number): Observable<DashboardStatsDTO> {
  return this.http.get<DashboardStatsDTO>(`${this.API}/stats/dashboard?jours=${jours}`);
}
  }
