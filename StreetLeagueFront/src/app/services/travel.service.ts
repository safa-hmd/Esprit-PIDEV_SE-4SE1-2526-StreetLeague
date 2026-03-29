import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TravelService {
  private baseUrl = 'http://localhost:8086/StreetLeague';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('TokenUserConnect') 
      || localStorage.getItem('token') 
      || '';
      
    console.log('Token found:', token ? 'YES' : 'NO');
    
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? 'Bearer ' + token : ''
    });
  }

  getTransports(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/admin/travel/transport`,
      { headers: this.getHeaders() }
    );
  }

  createTransport(data: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/api/admin/travel/transport`,
      data,
      { headers: this.getHeaders() }
    );
  }

  getAllAccommodations(): Observable<any[]> {
    console.log('Calling getAllAccommodations...');
    return this.http.get<any[]>(
      'http://localhost:8086/StreetLeague/api/admin/travel/accommodation',
      { headers: this.getHeaders() }
    );
  }

  createAccommodation(data: any): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/api/admin/travel/accommodation`,
      data,
      { headers: this.getHeaders() }
    );
  }

  updateAccommodation(id: number, data: any): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/api/admin/travel/accommodation/${id}`,
      data, 
      { headers: this.getHeaders() }
    );
  }
  
  deleteAccommodation(id: number): Observable<any> {
    return this.http.delete(
      `${this.baseUrl}/api/admin/travel/accommodation/${id}`,
      { headers: this.getHeaders() }
    );
  }
  
  approveAccommodation(id: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/api/admin/travel/accommodation/${id}/approve`,
      {}, 
      { headers: this.getHeaders() }
    );
  }
  
  rejectAccommodation(id: number): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/api/admin/travel/accommodation/${id}/reject`,
      {}, 
      { headers: this.getHeaders() }
    );
  }
  
  getApprovedAccommodations(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/coach/travel/accommodations`,
      { headers: this.getHeaders() }
    );
  }
  
  getMyTeamMembers(coachId: any): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/coach/travel/team-members?coachId=${coachId}`,
      { headers: this.getHeaders() }
    );
  }
  
  submitAccommodationRequest(data: any): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/api/coach/travel/accommodation-request`,
      data, 
      { headers: this.getHeaders() }
    );
  }
  
  getMyAccommodationRequests(coachId: any): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/coach/travel/accommodation-requests/my?coachId=${coachId}`,
      { headers: this.getHeaders() }
    );
  }

  // ============================================================
  // PDF DOWNLOAD METHODS
  // ============================================================

  downloadRequestPdf(requestId: number): Observable<Blob> {
    return this.http.get(
      `${this.baseUrl}/api/coach/travel/accommodation-requests/${requestId}/pdf`,
      { 
        headers: this.getHeaders(),
        responseType: 'blob'
      }
    );
  }

  downloadAdminRequestPdf(requestId: number): Observable<Blob> {
    return this.http.get(
      `${this.baseUrl}/api/admin/travel/accommodation-requests/${requestId}/pdf`,
      {
        headers: this.getHeaders(),
        responseType: 'blob'
      }
    );
  }

  // ============================================================
  // ADMIN METHODS  
  // ============================================================

  getAccommodationRequests(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/admin/travel/accommodation-requests`,
      { headers: this.getHeaders() }
    );
  }

  approveAccommodationRequest(id: number, data: any): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/api/admin/travel/accommodation-requests/${id}/approve`,
      data,
      { headers: this.getHeaders() }
    );
  }

  rejectAccommodationRequest(id: number, data: any): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/api/admin/travel/accommodation-requests/${id}/reject`,
      data,
      { headers: this.getHeaders() }
    );
  }
}
