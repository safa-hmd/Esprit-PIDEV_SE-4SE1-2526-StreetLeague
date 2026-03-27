import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Field, FieldReservation } from '../models/field-reservation.model';

@Injectable({ providedIn: 'root' })
export class FieldReservationService {

  private reservationApi = 'http://localhost:8086/StreetLeague/api/reservations';
  private fieldApi      = 'http://localhost:8086/StreetLeague/api/fields';

  constructor(private http: HttpClient) {}

  // ─── RESERVATIONS ────────────────────────────────────────────────

  getAllReservations(): Observable<FieldReservation[]> {
    return this.http.get<FieldReservation[]>(this.reservationApi);
  }

  getPendingReservations(): Observable<FieldReservation[]> {
    return this.http.get<FieldReservation[]>(`${this.reservationApi}/pending`);
  }

  getReservationById(id: number): Observable<FieldReservation> {
    return this.http.get<FieldReservation>(`${this.reservationApi}/${id}`);
  }

  getReservationsByField(fieldId: number): Observable<FieldReservation[]> {
    return this.http.get<FieldReservation[]>(`${this.reservationApi}/field/${fieldId}`);
  }

  getReservationsByPlayer(playerId: number): Observable<FieldReservation[]> {
    return this.http.get<FieldReservation[]>(`${this.reservationApi}/player/${playerId}`);
  }

  createReservation(dto: FieldReservation): Observable<FieldReservation> {
    return this.http.post<FieldReservation>(this.reservationApi, dto);
  }

  approveReservation(id: number, adminNote?: string): Observable<FieldReservation> {
    return this.http.patch<FieldReservation>(`${this.reservationApi}/${id}/approve`,
      adminNote ? { adminNote } : {}
    );
  }

  rejectReservation(id: number, adminNote?: string): Observable<FieldReservation> {
    return this.http.patch<FieldReservation>(`${this.reservationApi}/${id}/reject`,
      adminNote ? { adminNote } : {}
    );
  }

  cancelReservation(id: number, playerId: number): Observable<FieldReservation> {
    return this.http.patch<FieldReservation>(
      `${this.reservationApi}/${id}/cancel?playerId=${playerId}`, {}
    );
  }

  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.reservationApi}/${id}`);
  }

  // ─── FIELDS ──────────────────────────────────────────────────────

  getAllFields(): Observable<Field[]> {
    return this.http.get<Field[]>(this.fieldApi);
  }

  getAvailableFields(): Observable<Field[]> {
    return this.http.get<Field[]>(`${this.fieldApi}/available`);
  }

  getFieldById(id: number): Observable<Field> {
    return this.http.get<Field>(`${this.fieldApi}/${id}`);
  }

  createField(dto: Field): Observable<Field> {
    return this.http.post<Field>(this.fieldApi, dto);
  }

  updateField(id: number, dto: Field): Observable<Field> {
    return this.http.put<Field>(`${this.fieldApi}/${id}`, dto);
  }

  toggleAvailability(id: number): Observable<Field> {
    return this.http.patch<Field>(`${this.fieldApi}/${id}/toggle`, {});
  }

  deleteField(id: number): Observable<void> {
    return this.http.delete<void>(`${this.fieldApi}/${id}`);
  }
}