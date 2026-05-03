import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/api-url';
import { SponsoringEvenementDTO } from '../models/sponsoring-evenement-dto';

@Injectable({
  providedIn: 'root'
})
export class SponsoringEvenementService {
  private readonly apiUrl = `${API_BASE_URL}/api/sponsoring`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<SponsoringEvenementDTO[]> {
    return this.http.get<SponsoringEvenementDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<SponsoringEvenementDTO> {
    return this.http.get<SponsoringEvenementDTO>(`${this.apiUrl}/${id}`);
  }

  create(sponsoring: SponsoringEvenementDTO): Observable<SponsoringEvenementDTO> {
    return this.http.post<SponsoringEvenementDTO>(this.apiUrl, sponsoring);
  }

  update(id: number, sponsoring: SponsoringEvenementDTO): Observable<SponsoringEvenementDTO> {
    return this.http.put<SponsoringEvenementDTO>(`${this.apiUrl}/${id}`, sponsoring);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateStatus(id: number, statut: string): Observable<SponsoringEvenementDTO> {
    return this.http.patch<SponsoringEvenementDTO>(`${this.apiUrl}/${id}/status`, { status });
  }
}

