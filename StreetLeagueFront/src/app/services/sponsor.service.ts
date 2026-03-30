import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/api-url';
import { SponsorDTO } from '../models/sponsor-dto';

@Injectable({
  providedIn: 'root'
})
export class SponsorService {
  private readonly apiUrl = `${API_BASE_URL}/api/sponsor`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<SponsorDTO[]> {
    return this.http.get<SponsorDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<SponsorDTO> {
    return this.http.get<SponsorDTO>(`${this.apiUrl}/${id}`);
  }

  create(sponsor: SponsorDTO): Observable<SponsorDTO> {
    return this.http.post<SponsorDTO>(this.apiUrl, sponsor);
  }

  update(id: number, sponsor: SponsorDTO): Observable<SponsorDTO> {
    return this.http.put<SponsorDTO>(`${this.apiUrl}/${id}`, sponsor);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
