import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TravelService } from '../../services/travel.service';

@Component({
  selector: 'app-accommodation-requests',
  templateUrl: './accommodation-requests.component.html',
  styleUrls: ['./accommodation-requests.component.scss']
})
export class AccommodationRequestsComponent implements OnInit {
  accommodationRequests: any[] = [];
  loading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';
  showApproveModal: boolean = false;
  showRejectModal: boolean = false;
  selectedRequest: any = null;
  adminComment: string = '';

  private baseUrl = 'http://localhost:8086/StreetLeague';

  constructor(
    private travelService: TravelService,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.loadAccommodationRequests();
  }

  // ============================================================
  // LOAD REQUESTS
  // ============================================================

  private loadAccommodationRequests(): void {
    this.loading = true;
    this.travelService.getAccommodationRequests().subscribe({
      next: (data) => {
        this.accommodationRequests = data || [];
        this.loading = false;
        console.log('Accommodation requests loaded:', this.accommodationRequests);
      },
      error: (err) => {
        console.error('Error loading requests:', err);
        this.errorMessage = 'Failed to load accommodation requests.';
        this.loading = false;
      }
    });
  }

  // ============================================================
  // MODAL ACTIONS
  // ============================================================

  openApproveModal(request: any): void {
    this.selectedRequest = request;
    this.adminComment = '';
    this.showApproveModal = true;
  }

  openRejectModal(request: any): void {
    this.selectedRequest = request;
    this.adminComment = '';
    this.showRejectModal = true;
  }

  closeModals(): void {
    this.showApproveModal = false;
    this.showRejectModal = false;
    this.selectedRequest = null;
    this.adminComment = '';
  }

  // ============================================================
  // APPROVE/REJECT ACTIONS
  // ============================================================

  confirmApprove(): void {
    if (!this.selectedRequest) return;

    const data = { adminComment: this.adminComment };
    this.loading = true;

    this.travelService.approveAccommodationRequest(
      this.selectedRequest.id, 
      data
    ).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = '✓ Request approved successfully!';
        this.closeModals();
        this.loadAccommodationRequests();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error approving request: ' + (err.error?.message || err.statusText);
        console.error('Approve error:', err);
      }
    });
  }

  confirmReject(): void {
    if (!this.selectedRequest) return;

    if (!this.adminComment.trim()) {
      this.errorMessage = 'Please provide a reason for rejection.';
      return;
    }

    const data = { adminComment: this.adminComment };
    this.loading = true;

    this.travelService.rejectAccommodationRequest(
      this.selectedRequest.id, 
      data
    ).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = '✓ Request rejected successfully!';
        this.closeModals();
        this.loadAccommodationRequests();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error rejecting request: ' + (err.error?.message || err.statusText);
        console.error('Reject error:', err);
      }
    });
  }

  // ============================================================
  // PDF DOWNLOAD
  // ============================================================

  downloadPdf(requestId: number): void {
    this.travelService.downloadAdminRequestPdf(requestId).subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `accommodation-request-${requestId}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('PDF download error:', err);
        this.errorMessage = 'Failed to download PDF.';
      }
    });
  }

  // ============================================================
  // UTILITIES
  // ============================================================

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected'
    };
    return classes[status] || 'status-pending';
  }

  getStatusBadgeText(status: string): string {
    return status || 'PENDING';
  }

  formatDate(dateString: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}
