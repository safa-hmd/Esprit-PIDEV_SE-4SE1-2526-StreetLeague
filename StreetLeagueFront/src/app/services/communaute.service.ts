import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommunauteDTO } from '../models/communaute-dto';

@Injectable({
  providedIn: 'root'
})
export class CommunauteService {
  private apiUrl = 'http://localhost:8086/StreetLeague/api/communautes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<CommunauteDTO[]> {
    return this.http.get<CommunauteDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<CommunauteDTO> {
    return this.http.get<CommunauteDTO>(`${this.apiUrl}/${id}`);
  }

  create(dto: CommunauteDTO): Observable<CommunauteDTO> {
    return this.http.post<CommunauteDTO>(this.apiUrl, dto);
  }

  update(id: number, dto: CommunauteDTO): Observable<CommunauteDTO> {
    return this.http.put<CommunauteDTO>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
