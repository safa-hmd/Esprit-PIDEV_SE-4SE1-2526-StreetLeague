import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/api-url';
import { ContratSponsorDTO } from '../models/contrat-sponsor-dto';

@Injectable({
  providedIn: 'root'
})
export class ContratSponsorService {
  private readonly apiUrl = `${API_BASE_URL}/api/contrat`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ContratSponsorDTO[]> {
    return this.http.get<ContratSponsorDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<ContratSponsorDTO> {
    return this.http.get<ContratSponsorDTO>(`${this.apiUrl}/${id}`);
  }

  create(contrat: ContratSponsorDTO): Observable<ContratSponsorDTO> {
    return this.http.post<ContratSponsorDTO>(this.apiUrl, contrat);
  }

  update(id: number, contrat: ContratSponsorDTO): Observable<ContratSponsorDTO> {
    return this.http.put<ContratSponsorDTO>(`${this.apiUrl}/${id}`, contrat);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
