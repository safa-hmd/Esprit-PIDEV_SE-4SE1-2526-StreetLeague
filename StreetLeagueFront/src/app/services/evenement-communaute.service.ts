import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EvenementCommunauteDTO } from '../models/evenement-communaute-dto';

@Injectable({
  providedIn: 'root'
})
export class EvenementCommunauteService {
  private apiUrl = 'http://localhost:8086/StreetLeague/api/evenement-communaute';

  constructor(private http: HttpClient) {}

  getAll(): Observable<EvenementCommunauteDTO[]> {
    return this.http.get<EvenementCommunauteDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<EvenementCommunauteDTO> {
    return this.http.get<EvenementCommunauteDTO>(`${this.apiUrl}/${id}`);
  }

  create(dto: EvenementCommunauteDTO): Observable<EvenementCommunauteDTO> {
    return this.http.post<EvenementCommunauteDTO>(this.apiUrl, dto);
  }

  update(id: number, dto: EvenementCommunauteDTO): Observable<EvenementCommunauteDTO> {
    return this.http.put<EvenementCommunauteDTO>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
