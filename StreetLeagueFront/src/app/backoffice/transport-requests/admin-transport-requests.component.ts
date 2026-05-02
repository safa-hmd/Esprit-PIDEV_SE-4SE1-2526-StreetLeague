import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TravelService } from '../../services/travel.service';

@Component({
  selector: 'app-admin-transport-requests',
  templateUrl: './admin-transport-requests.component.html',
  styleUrls: ['./admin-transport-requests.component.scss']
})
export class AdminTransportRequestsComponent implements OnInit {
  loading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  // Private Car
  transportRequests: any[] = [];
  showApproveModal: boolean = false;
  showRejectModal: boolean = false;
  selectedRequest: any = null;
  adminComment: string = '';

  // Travel Requests
  travelRequests: any[] = [];
  showTravelApproveModal: boolean = false;
  showTravelRejectModal: boolean = false;
  selectedTravelRequest: any = null;
  travelAdminComment: string = '';

  // KPI Getters
  get pendingCount(): number {
    const p1 = (this.transportRequests || []).filter(t => t.status === 'PENDING').length;
    const p2 = (this.travelRequests || []).filter(t => t.status === 'PENDING').length;
    return p1 + p2;
  }

  get vehiclesReadyCount(): number {
    const r1 = (this.transportRequests || []).filter(t => t.status === 'APPROVED' || t.status === 'CONFIRMED').length;
    const r2 = (this.travelRequests || []).filter(t => t.status === 'APPROVED' || t.status === 'CONFIRMED').length;
    return r1 + r2;
  }

  get teamsInTransitCount(): number {
    return (this.travelRequests || []).filter(t => t.status === 'APPROVED' || t.status === 'CONFIRMED').length;
  }

  get weeklyBudget(): number {
    let sum = 0;
    (this.transportRequests || []).forEach(t => { sum += (t.pricePerSeat || 0); });
    (this.travelRequests || []).forEach(t => { sum += (t.totalAmount || 0); });
    return sum;
  }

  private baseUrl = 'http://localhost:8086/StreetLeague';

  constructor(
    private travelService: TravelService,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.loadTransportRequests();
    this.loadTravelRequests();
  }

  loadTransportRequests(): void {
    this.loading = true;
    this.travelService.getTransports().subscribe({
      next: (data) => {
        // Filter only PRIVATE_CAR transports
        this.transportRequests = (data || []).filter((t: any) => t.type === 'PRIVATE_CAR');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading transports:', err);
        this.errorMessage = 'Failed to load private car transport requests.';
        this.loading = false;
      }
    });
  }

  loadTravelRequests() {
    this.loading = true;
    this.travelService.getAllTravelRequests().subscribe({
      next: (res) => {
        this.travelRequests = res;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = "Failed to load travel transport requests.";
        this.loading = false;
      }
    });
  }

  // MODAL ACTIONS - PRIVATE CAR
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

  confirmApprove(): void {
    if (!this.selectedRequest) return;
    this.loading = true;
    this.travelService.approveTransport(this.selectedRequest.id).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '✓ Private Car request approved successfully!';
        this.closeModals();
        this.loadTransportRequests();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error approving private car request.';
      }
    });
  }

  confirmReject(): void {
    if (!this.selectedRequest) return;
    if (!this.adminComment.trim()) {
      this.errorMessage = 'Please provide a reason for rejection.';
      return;
    }
    this.loading = true;
    this.travelService.rejectTransport(this.selectedRequest.id).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '✓ Private Car request rejected successfully!';
        this.closeModals();
        this.loadTransportRequests();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error rejecting private car request.';
      }
    });
  }

  downloadPdf(id: number): void {
    this.travelService.downloadTransportPdf(id).subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `private-car-request-${id}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.errorMessage = 'Failed to download PDF.';
      }
    });
  }

  // MODAL ACTIONS - TRAVEL REQUESTS (BUS/VAN)
  openTravelApproveModal(req: any) {
    this.selectedTravelRequest = req;
    this.travelAdminComment = '';
    this.showTravelApproveModal = true;
  }

  openTravelRejectModal(req: any) {
    this.selectedTravelRequest = req;
    this.travelAdminComment = '';
    this.showTravelRejectModal = true;
  }

  closeTravelModals() {
    this.showTravelApproveModal = false;
    this.showTravelRejectModal = false;
    this.selectedTravelRequest = null;
    this.travelAdminComment = '';
  }

  confirmTravelApprove() {
    if (!this.selectedTravelRequest) return;
    this.loading = true;
    const payload = {
      status: 'APPROVED',
      adminComment: this.travelAdminComment
    };
    
    this.travelService.decideTravelRequest(this.selectedTravelRequest.id, payload).subscribe({
      next: () => {
        this.successMessage = "Travel Request approved successfully.";
        this.loadTravelRequests();
        this.closeTravelModals();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = "Failed to approve travel request.";
        this.loading = false;
      }
    });
  }

  confirmTravelReject() {
    if (!this.selectedTravelRequest) return;
    this.loading = true;
    const payload = {
      status: 'REJECTED',
      adminComment: this.travelAdminComment
    };

    this.travelService.decideTravelRequest(this.selectedTravelRequest.id, payload).subscribe({
      next: () => {
        this.successMessage = "Travel Request rejected.";
        this.loadTravelRequests();
        this.closeTravelModals();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = "Failed to reject travel request.";
        this.loading = false;
      }
    });
  }

  downloadTravelPdf(requestId: number) {
    this.travelService.downloadTravelRequestPdf(requestId).subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `team-transport-voucher-${requestId}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Error downloading travel PDF:', err);
        this.errorMessage = 'Failed to download Team Transport PDF.';
      }
    });
  }


  formatDate(dateString: string): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString();
  }
}

