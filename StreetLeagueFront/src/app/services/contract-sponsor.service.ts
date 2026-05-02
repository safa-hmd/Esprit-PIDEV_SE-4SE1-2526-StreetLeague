import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/api-url';
import { ContractSponsorDTO } from '../models/contract-sponsor-dto';

@Injectable({
  providedIn: 'root'
})
export class ContractSponsorService {
  private readonly apiUrl = `${API_BASE_URL}/api/contrat`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ContractSponsorDTO[]> {
    return this.http.get<ContractSponsorDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<ContractSponsorDTO> {
    return this.http.get<ContractSponsorDTO>(`${this.apiUrl}/${id}`);
  }

  create(contract: ContractSponsorDTO): Observable<ContractSponsorDTO> {
    return this.http.post<ContractSponsorDTO>(this.apiUrl, contract);
  }

  update(id: number, contract: ContractSponsorDTO): Observable<ContractSponsorDTO> {
    return this.http.put<ContractSponsorDTO>(`${this.apiUrl}/${id}`, contract);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateStatus(id: number, statut: string): Observable<ContractSponsorDTO> {
    return this.http.patch<ContractSponsorDTO>(`${this.apiUrl}/${id}/status`, { status });
  }
}

