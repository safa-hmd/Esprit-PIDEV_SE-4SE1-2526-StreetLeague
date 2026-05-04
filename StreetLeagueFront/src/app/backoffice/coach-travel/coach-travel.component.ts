import { environment } from 'src/environments/environment';
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-coach-travel',
  templateUrl: './coach-travel.component.html',
  styleUrls: ['./coach-travel.component.scss']
})
export class CoachTravelComponent implements OnInit {
  transports: any[] = [];
  accommodations: any[] = [];
  selectedTransportId: number | null = null;
  selectedAccommodationId: number | null = null;

  tournamentId: number = 1;
  teamId: number = 1;
  sameCity: boolean = false;

  errorMessage: string = '';
  successMessage: string = '';
  myRequests: any[] = [];

  private baseUrl = `${environment.baseUrl}/api/coach/travel`;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.sameCity = false;

    this.loadTransports();
    this.loadAccommodations();
    this.loadMyRequests();
  }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    });
  }

  loadTransports(): void {
    this.http.get<any[]>(`${this.baseUrl}/transports/tournament/${this.tournamentId}`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => this.transports = data,
        error: (err) => console.error('Error loading transports', err)
      });
  }

  loadAccommodations(): void {
    this.http.get<any[]>(`${this.baseUrl}/accommodations/tournament/${this.tournamentId}`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => this.accommodations = data,
        error: (err) => console.error('Error loading accommodations', err)
      });
  }

  loadMyRequests(): void {
    this.http.get<any[]>(`${this.baseUrl}/requests/team/${this.teamId}`, { headers: this.getHeaders() })
      .subscribe({
        next: (data) => this.myRequests = data,
        error: (err) => console.error('Error loading requests', err)
      });
  }

  onTransportSelect(id: number): void {
    if (this.selectedTransportId === id) {
      this.selectedTransportId = null;
    } else {
      this.selectedTransportId = id;
    }
  }

  onAccommodationSelect(id: number): void {
    if (this.selectedAccommodationId === id) {
      this.selectedAccommodationId = null;
    } else {
      this.selectedAccommodationId = id;
    }
  }

  submitRequest(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.sameCity && !this.selectedTransportId) {
      this.errorMessage = 'Please select a transport option.';
      return;
    }

    if (!this.selectedAccommodationId) {
      this.errorMessage = 'Please select an accommodation option.';
      return;
    }

    const payload = {
      teamId: this.teamId,
      tournamentId: this.tournamentId,
      transportId: this.sameCity ? null : this.selectedTransportId,
      accommodationId: this.selectedAccommodationId,
      comment: 'Requested via Coach Dashboard'
    };

    this.http.post<any>(`${this.baseUrl}/requests`, payload, { headers: this.getHeaders() })
      .subscribe({
        next: () => {
          this.successMessage = 'Travel request submitted successfully!';
          this.selectedTransportId = null;
          this.selectedAccommodationId = null;
          this.loadMyRequests();
        },
        error: (err) => {
          this.errorMessage = err?.error?.message || 'Failed to submit travel request.';
        }
      });
  }
  getTotalAmount(): number {
    const accommodation = this.accommodations?.find(
      a => a.id === this.selectedAccommodationId
    );
    const nights = accommodation?.numberOfNights || 0;
    const pricePerNight = accommodation?.pricePerNight || 0;
    const members = (this as any).memberIds?.length || 1;
    return nights * pricePerNight * members;
  }
}

