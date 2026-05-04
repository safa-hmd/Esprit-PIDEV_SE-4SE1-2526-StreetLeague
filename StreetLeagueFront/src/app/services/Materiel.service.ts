import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Materiel, Category } from '../models/materiel.model';

@Injectable({ providedIn: 'root' })
export class MaterielService {
  private base = `${environment.baseUrl}/api`;

  constructor(private http: HttpClient) {}

  // ── Matériels ──────────────────────────────────────
  getAll(): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(`${this.base}/materiels`);
  }

  getById(id: number): Observable<Materiel> {
    return this.http.get<Materiel>(`${this.base}/materiels/${id}`);
  }

  create(dto: Materiel): Observable<Materiel> {
    return this.http.post<Materiel>(`${this.base}/materiels`, dto);
  }

  update(id: number, dto: Materiel): Observable<Materiel> {
    return this.http.put<Materiel>(`${this.base}/materiels/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/materiels/${id}`);
  }

  // ── Catégories ─────────────────────────────────────
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.base}/categories`);
  }

  createCategory(dto: Category): Observable<Category> {
    return this.http.post<Category>(`${this.base}/categories`, dto);
  }

  updateCategory(id: number, dto: Category): Observable<Category> {
    return this.http.put<Category>(`${this.base}/categories/${id}`, dto);
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/categories/${id}`);
  }
}
