import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from 'src/environments/api-url';
import { CommunauteDTO } from '../models/communaute-dto';

@Injectable({
  providedIn: 'root'
})
export class CommunauteService {
  private readonly apiUrl = `${API_BASE_URL}/api/communaute`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<CommunauteDTO[]> {
    return this.http.get<CommunauteDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<CommunauteDTO> {
    return this.http.get<CommunauteDTO>(`${this.apiUrl}/${id}`);
  }

  create(communaute: CommunauteDTO): Observable<CommunauteDTO> {
    return this.http.post<CommunauteDTO>(this.apiUrl, communaute);
  }

  update(id: number, communaute: CommunauteDTO): Observable<CommunauteDTO> {
    return this.http.put<CommunauteDTO>(`${this.apiUrl}/${id}`, communaute);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
