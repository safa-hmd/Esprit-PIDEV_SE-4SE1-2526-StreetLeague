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

@Injectable({
  providedIn: 'root'
})
export class AdvancedStatsService {
  private readonly base = 'http://localhost:8086/StreetLeague/api';

  constructor(private http: HttpClient) {}

  getContributionTotaleParCommunaute(): Observable<CommunauteStatsDTO[]> {
    return this.http.get<CommunauteStatsDTO[]>(`${this.base}/sponsoring/test/stats/communaute`);
  }

  getTopCommunautesAvecSponsorings(status?: string, seuilMinimum: number = 1): Observable<TopCommunauteDTO[]> {
    let params = new HttpParams().set('seuilMinimum', seuilMinimum.toString());
    if (status) params = params.set('status', status);
    return this.http.get<TopCommunauteDTO[]>(`${this.base}/sponsoring/test/stats/top-communautes`, { params });
  }

  getDashboardSponsorParCommunaute(status?: string): Observable<DashboardSponsorCommunauteDTO[]> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<DashboardSponsorCommunauteDTO[]>(`${this.base}/sponsoring/test/stats/dashboard-sponsor`, { params });
  }

  getEvenementsSansSponsoring(): Observable<EvenementSansSponsoringDTO[]> {
    return this.http.get<EvenementSansSponsoringDTO[]>(`${this.base}/evenement/test/stats/sans-sponsoring`);
  }

  getComparaisonContractsVsSponsorings(): Observable<ComparaisonSponsorDTO[]> {
    return this.http.get<ComparaisonSponsorDTO[]>(`${this.base}/sponsor/test/stats/comparaison-contrats`);
  }
}