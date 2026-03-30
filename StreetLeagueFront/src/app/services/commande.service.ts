import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CommandeStatus = 'EN_ATTENTE' | 'VALIDEE' | 'ANNULEE' | 'LIVREE' | 'PREPAREE';

export interface Commande {
  id: number;
  montantTotal: number;
  statut: CommandeStatus;
}

@Injectable({ providedIn: 'root' })
export class CommandeService {
  private base = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Commande[]> {
    return this.http.get<Commande[]>(`${this.base}/commandes`);
  }

  getById(id: number): Observable<Commande> {
    return this.http.get<Commande>(`${this.base}/commandes/${id}`);
  }

  updateStatus(id: number, statut: CommandeStatus): Observable<Commande> {
    return this.http.put<Commande>(
      `${this.base}/commandes/${id}/status?statut=${statut}`, {}
    );
  }
}