import { Component, OnInit } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';

@Component({
  selector: 'app-travel-requests',
  templateUrl: './travel-requests.component.html',
  styleUrls: ['./travel-requests.component.css']
})
export class TravelRequestsComponent implements OnInit {
  requests: any[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';
  
  // Modal State
  selectedRequest: any = null;
  showApproveModal = false;
  showRejectModal = false;
  adminComment = '';

  constructor(private travelService: TravelService) {}

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.loading = true;
    this.travelService.getAllTravelRequests().subscribe({
      next: (res) => {
        this.requests = res;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = "Failed to load travel requests.";
        this.loading = false;
      }
    });
  }

  openApproveModal(req: any) {
    this.selectedRequest = req;
    this.adminComment = '';
    this.showApproveModal = true;
  }

  openRejectModal(req: any) {
    this.selectedRequest = req;
    this.adminComment = '';
    this.showRejectModal = true;
  }

  closeModals() {
    this.showApproveModal = false;
    this.showRejectModal = false;
    this.selectedRequest = null;
  }

  confirmApprove() {
    if (!this.selectedRequest) return;
    this.loading = true;
    const payload = {
      status: 'APPROVED',
      adminComment: this.adminComment
    };
    
    this.travelService.decideTravelRequest(this.selectedRequest.id, payload).subscribe({
      next: () => {
        this.successMessage = "Request approved successfully.";
        this.loadRequests();
        this.closeModals();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = "Failed to approve request.";
        this.loading = false;
      }
    });
  }

  confirmReject() {
    if (!this.selectedRequest) return;
    this.loading = true;
    const payload = {
      status: 'REJECTED',
      adminComment: this.adminComment
    };

    this.travelService.decideTravelRequest(this.selectedRequest.id, payload).subscribe({
      next: () => {
        this.successMessage = "Request rejected.";
        this.loadRequests();
        this.closeModals();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = "Failed to reject request.";
        this.loading = false;
      }
    });
  }

  downloadPdf(requestId: number) {
    // Note: If there's a specific endpoint for Travel Request PDF, use it.
    // Otherwise, we might need to add one. For now, we'll use a placeholder or check service.
    // Based on BUG 3 requirements, we need a PDF download button.
    alert("PDF Decision generation for Travel Requests (Transport) is being processed.");
  }

  formatDate(date: any) {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString();
  }
}
