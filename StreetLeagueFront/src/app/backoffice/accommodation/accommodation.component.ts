import { Component, OnInit } from '@angular/core';
import { TravelService } from '../../services/travel.service';

@Component({
  selector: 'app-accommodation',
  templateUrl: './accommodation.component.html',
  styleUrls: ['./accommodation.component.scss']
})
export class AccommodationComponent implements OnInit {

  // Form fields
  type: string = 'HOTEL';
  numberOfNights: number = 0;
  pricePerNight: number = 0;
  capacity: number = 0;
  formula: string = 'FULL_BOARD';
  address: string = '';

  // State
  accommodationList: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  
  editMode: boolean = false;
  editId: number | null = null;
  showDeleteConfirm: boolean = false;
  deleteTargetId: number | null = null;
  showRejectModal: boolean = false;
  rejectTargetId: number | null = null;
  rejectComment: string = '';

  accommodationTypes = [
    { value: 'HOTEL', label: 'Hotel' },
    { value: 'HOSTEL', label: 'Hostel' },
    { value: 'APARTMENT', label: 'Apartment' },
    { value: 'SPORTS_CENTER', label: 'Sports Center' }
  ];

  formulas = [
    { value: 'ROOM_ONLY',          label: 'Room Only' },
    { value: 'BREAKFAST_INCLUDED', label: 'Breakfast Included' },
    { value: 'HALF_BOARD',         label: 'Half Board' },
    { value: 'FULL_BOARD',         label: 'Full Board' },
    { value: 'ALL_INCLUSIVE',      label: 'All Inclusive' }
  ];

  constructor(private travelService: TravelService) {}

  ngOnInit(): void {
    this.loadAccommodations();
  }

  loadAccommodations(): void {
    this.travelService.getAllAccommodations().subscribe({
      next: (data) => {
        console.log('DATA FROM API:', data);
        this.accommodationList = data;
      },
      error: (err) => {
        console.error('LOAD ERROR:', err.status, err.error);
        this.errorMessage = 'Failed to load: ' + err.status;
      }
    });
  }

  startEdit(a: any): void {
    this.editMode = true;
    this.editId = a.id;
    this.type = a.type;
    this.numberOfNights = a.numberOfNights;
    this.pricePerNight = a.pricePerNight;
    this.formula = a.formula;
    this.address = a.address || '';
    this.capacity = a.capacity || 0;
    window.scrollTo(0, 0);
  }

  cancelEdit(): void {
    this.editMode = false;
    this.editId = null;
    this.resetForm();
  }

  submitForm(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.type || !this.numberOfNights || 
        !this.pricePerNight || !this.formula || !this.address) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    const payload = {
      type: this.type,
      numberOfNights: Number(this.numberOfNights),
      pricePerNight: Number(this.pricePerNight),
      formula: this.formula,
      address: this.address,
      capacity: Number(this.capacity)
    };

    this.loading = true;
    
    if (this.editMode && this.editId) {
      this.travelService.updateAccommodation(this.editId, payload).subscribe({
        next: (response) => {
          console.log('UPDATED:', response);
          this.loading = false;
          this.successMessage = 'Updated successfully!';
          this.cancelEdit();
          this.loadAccommodations();
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Update failed';
        }
      });
    } else {
      this.travelService.createAccommodation(payload).subscribe({
        next: (response) => {
          console.log('ADDED:', response);
          this.loading = false;
          this.successMessage = 'Accommodation added!';
          this.resetForm();
          this.loadAccommodations();
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Add failed';
        }
      });
    }
  }

  confirmDelete(id: number): void {
    this.showDeleteConfirm = true;
    this.deleteTargetId = id;
  }

  cancelDelete(): void {
    this.showDeleteConfirm = false;
    this.deleteTargetId = null;
  }

  executeDelete(): void {
    if (!this.deleteTargetId) return;
    this.travelService.deleteAccommodation(
      this.deleteTargetId).subscribe({
      next: () => {
        this.showDeleteConfirm = false;
        this.deleteTargetId = null;
        this.successMessage = 'Deleted successfully!';
        this.loadAccommodations();
      },
      error: (err) => {
        this.errorMessage = 'Delete failed: ' + err.status;
      }
    });
  }

  approve(id: number): void {
    this.travelService.approveAccommodation(id).subscribe({
      next: () => {
        this.successMessage = 'Accommodation approved!';
        this.loadAccommodations();
      },
      error: (err) => {
        this.errorMessage = 'Approve failed: ' + err.status;
      }
    });
  }

  reject(id: number): void {
    this.travelService.rejectAccommodation(id).subscribe({
      next: () => {
        this.successMessage = 'Accommodation rejected!';
        this.loadAccommodations();
      },
      error: (err) => {
        this.errorMessage = 'Reject failed: ' + err.status;
      }
    });
  }

  resetForm(): void {
    this.type = 'HOTEL';
    this.numberOfNights = 0;
    this.pricePerNight = 0;
    this.formula = 'FULL_BOARD';
    this.address = '';
    this.capacity = 0;
    this.editMode = false;
    this.editId = null;
  }

  formatFormula(formula: string): string {
    const labels: { [key: string]: string } = {
      'FULL_BOARD': 'Full Board',
      'HALF_BOARD': 'Half Board',
      'BREAKFAST_INCLUDED': 'Breakfast Included',
      'ROOM_ONLY': 'Room Only',
      'ALL_INCLUSIVE': 'All Inclusive'
    };
    return labels[formula] || formula;
  }
}
