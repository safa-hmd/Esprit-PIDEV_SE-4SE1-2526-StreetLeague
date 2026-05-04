import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  FinancialSummary,
  RevenueByField,
  RevenueBySport,
  RevenueByMonth,
  TopPlayer
} from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private api = `${environment.baseUrl}/api/dashboard`;

  constructor(private http: HttpClient) {}

  getSummary(): Observable<FinancialSummary> {
    return this.http.get<FinancialSummary>(`${this.api}/summary`);
  }

  getRevenueByField(): Observable<RevenueByField[]> {
    return this.http.get<RevenueByField[]>(`${this.api}/revenue/fields`);
  }

  getRevenueBySport(): Observable<RevenueBySport[]> {
    return this.http.get<RevenueBySport[]>(`${this.api}/revenue/sports`);
  }

  getRevenueByMonth(): Observable<RevenueByMonth[]> {
    return this.http.get<RevenueByMonth[]>(`${this.api}/revenue/months`);
  }

  getTopPlayers(): Observable<TopPlayer[]> {
    return this.http.get<TopPlayer[]>(`${this.api}/top-players`);
  }
}
