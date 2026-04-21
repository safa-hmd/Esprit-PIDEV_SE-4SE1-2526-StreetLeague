import { Component, OnInit } from '@angular/core';
import { TravelService } from '../../services/travel.service';

@Component({
  selector: 'app-transport',
  templateUrl: './transport.component.html',
  styleUrls: ['./transport.component.scss']
})
export class TransportComponent implements OnInit {

  // Form fields - all primitives, no objects
  type: string = 'BUS';
  destination: string = '';
  pricePerSeat: number = 0;
  availableSeats: number = 0;
  departureTime: string = '';
  returnTime: string = '';

  // State
  transportList: any[] = [];
  filteredTransports: any[] = [];
  searchText: string = '';
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private travelService: TravelService) { }

  ngOnInit(): void {
    this.loadTransports();
  }

  loadTransports(): void {
    this.travelService.getTransports().subscribe({
      next: (data) => {
        this.transportList = data;
        this.filteredTransports = data;
      },
      error: (err) => {
        console.error('Load transports error:', err);
      }
    });
  }

  filterTransports(): void {
    if (!this.searchText || this.searchText.trim() === '') {
      this.filteredTransports = this.transportList;
    } else {
      const search = this.searchText.toLowerCase();
      this.filteredTransports = this.transportList.filter(t =>
        t.destination?.toLowerCase().includes(search) ||
        t.type?.toLowerCase().includes(search)
      );
    }
  }

  getTypeIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'BUS': '🚌',
      'TRAIN': '🚆',
      'PLANE': '✈️',
      'VAN': '🚐'
    };
    return icons[type] || '🚗';
  }

  private formatDateTime(dt: string): string {
    if (!dt) return '';
    if (dt.length === 16) return dt + ':00';
    return dt;
  }

  submitForm(): void {
    this.errorMessage = '';
    this.successMessage = '';

    // Force primitive string values
    const typeValue: string = String(this.type);
    const destinationValue: string = String(this.destination);
    const priceValue: number = Number(this.pricePerSeat);
    const seatsValue: number = Number(this.availableSeats);
    const departureValue: string = this.formatDateTime(String(this.departureTime));
    const returnValue: string = this.formatDateTime(String(this.returnTime));

    // Validation
    if (!typeValue || !destinationValue || !priceValue ||
      !seatsValue || !departureValue || !returnValue) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    const payload = {
      type: typeValue,
      destination: destinationValue,
      pricePerSeat: priceValue,
      availableSeats: seatsValue,
      departureTime: departureValue,
      returnTime: returnValue
    };

    // Debug logs - check browser F12 console
    console.log('=== TRANSPORT SUBMIT ===');
    console.log('type:', typeValue, '| typeof:', typeof typeValue);
    console.log('availableSeats:', seatsValue, '| typeof:', typeof seatsValue);
    console.log('FULL PAYLOAD:', JSON.stringify(payload));

    this.loading = true;

    this.travelService.createTransport(payload).subscribe({
      next: (response) => {
        console.log('SUCCESS:', response);
        this.loading = false;
        this.successMessage = 'Transport added successfully!';
        this.resetForm();
        this.loadTransports();
      },
      error: (err) => {
        console.error('ERROR status:', err.status);
        console.error('ERROR body:', err.error);
        this.loading = false;

        if (err.status === 0) {
          this.errorMessage = 'Cannot reach backend. Check if Spring Boot is running on port 8086.';
        } else if (err.status === 401) {
          this.errorMessage = 'Authentication failed. Please log in again.';
        } else if (err.status === 403) {
          this.errorMessage = 'Access denied. Admin role required.';
        } else if (err.status === 400) {
          this.errorMessage = err.error?.message || err.error || 'Invalid data.';
        } else {
          this.errorMessage = err.error?.message || err.error || 'Error: ' + err.status;
        }
      }
    });
  }

  resetForm(): void {
    this.type = 'BUS';
    this.destination = '';
    this.pricePerSeat = 0;
    this.availableSeats = 0;
    this.departureTime = '';
    this.returnTime = '';
  }
}