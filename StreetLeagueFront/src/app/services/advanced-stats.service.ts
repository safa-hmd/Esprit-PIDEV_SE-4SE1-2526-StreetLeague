import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CommunauteStatsDTO {
  communauteNom: string;
  communauteType: string;
  nombreEvenements: number;
  nombreSponsorings: number;
  totalContribution: number;
}

export interface TopCommunauteDTO {
  communauteNom: string;
  communauteType: string;
  nombreEvenements: number;
  nombreSponsorings: number;
  moyenneContribution: number;
  maxContribution: number;
}

export interface EvenementSansSponsoringDTO {
  communauteNom: string;
  evenementTitre: string;
  evenementDate: string;
  evenementDescription: string;
}

export interface ComparaisonSponsorDTO {
  sponsorNom: string;
  sponsorType: string;
  totalContrats: number;
  totalSponsorings: number;
  nombreContrats: number;
  nombreSponsorings: number;
}

export interface DashboardSponsorCommunauteDTO {
  sponsorNom: string;
  sponsorType: string;
  communauteNom: string;
  communauteType: string;
  nombreEvenements: number;
  totalContribution: number;
  moyenneContribution: number;
}

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdvancedStatsService {
  private apiUrl = `${environment.baseUrl}/api`;

  constructor(private http: HttpClient) {}

  // 1. Contribution totale par community
  getContributionTotaleParCommunaute(): Observable<CommunauteStatsDTO[]> {
    return this.http.get<CommunauteStatsDTO[]>(`${this.apiUrl}/sponsoring/test/stats/communaute`);
  }

  // 2. Top communities avec seuil
  getTopCommunautesAvecSponsorings(status?: string, seuilMinimum: number = 1): Observable<TopCommunauteDTO[]> {
    let params = new HttpParams().set('seuilMinimum', seuilMinimum.toString());
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<TopCommunauteDTO[]>(`${this.apiUrl}/sponsoring/test/stats/top-communautes`, { params });
  }

  // 3. Dashboard Sponsor-Community
  getDashboardSponsorParCommunaute(status?: string): Observable<DashboardSponsorCommunauteDTO[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<DashboardSponsorCommunauteDTO[]>(`${this.apiUrl}/sponsoring/test/stats/dashboard-sponsor`, { params });
  }

  // 4. Events sans sponsoring
  getEvenementsSansSponsoring(): Observable<EvenementSansSponsoringDTO[]> {
    return this.http.get<EvenementSansSponsoringDTO[]>(`${this.apiUrl}/evenement/test/stats/sans-sponsoring`);
  }

  // 5. Comparaison contracts vs sponsorings
  getComparaisonContractsVsSponsorings(): Observable<ComparaisonSponsorDTO[]> {
    return this.http.get<ComparaisonSponsorDTO[]>(`${this.apiUrl}/sponsor/test/stats/comparaison-contrats`);
  }
}

