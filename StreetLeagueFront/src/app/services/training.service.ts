// src/app/services/training.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrainingRequest, TrainingResponse, TrainingUpdateRequest } from '../models/training.model';

@Injectable({ providedIn: 'root' })
export class TrainingService {

  private base = 'http://localhost:8086/StreetLeague/training';

  constructor(private http: HttpClient) {}

  // ── Auth Headers ─────────────────────────────────────────
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // ── GET /training/showTrainings ───────────────────────────
  getAllTrainings(): Observable<TrainingResponse[]> {
    return this.http.get<TrainingResponse[]>(
      `${this.base}/showTrainings`,
      { headers: this.getHeaders() }
    );
  }

  // ── GET /training/showTrainingById/:id ────────────────────
  getTrainingById(idTraining: number): Observable<TrainingResponse> {
    return this.http.get<TrainingResponse>(
      `${this.base}/showTrainingById/${idTraining}`,
      { headers: this.getHeaders() }
    );
  }

  // ── POST /training/add?teamId=&email= ─────────────────────
  addTraining(dto: TrainingRequest, teamId: number): Observable<TrainingResponse> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.post<TrainingResponse>(
      `${this.base}/add?teamId=${teamId}&email=${email}`,
      dto,
      { headers: this.getHeaders() }
    );
  }

  // ── PUT /training/update?email= ───────────────────────────
  updateTraining(dto: TrainingUpdateRequest): Observable<TrainingResponse> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.put<TrainingResponse>(
      `${this.base}/update?email=${email}`,
      dto,
      { headers: this.getHeaders() }
    );
  }

  // ── DELETE /training/delete/:id?email= ───────────────────
  deleteTraining(idTraining: number): Observable<void> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.delete<void>(
      `${this.base}/delete/${idTraining}?email=${email}`,
      { headers: this.getHeaders() }
    );
  }

  // ── POST /training/:id/join?email= ────────────────────────
  joinTraining(idTraining: number): Observable<TrainingResponse> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.post<TrainingResponse>(
      `${this.base}/${idTraining}/join?email=${email}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // ── DELETE /training/:id/leave?email= ────────────────────
  leaveTraining(idTraining: number): Observable<TrainingResponse> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.delete<TrainingResponse>(
      `${this.base}/${idTraining}/leave?email=${email}`,
      { headers: this.getHeaders() }
    );
  }
}