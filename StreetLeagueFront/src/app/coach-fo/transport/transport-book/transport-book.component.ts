import { Component, OnInit } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-transport-book',
  templateUrl: './transport-book.component.html',
  styleUrls: ['./transport-book.component.css']
})
export class TransportBookComponent implements OnInit {
  transport: any;
  tournamentId: number | null = null;
  teamMembers: any[] = [];
  selectedMemberIds: Set<number> = new Set<number>();
  totalPrice: number = 0;
  teamId: number = 0;
  coachId: string | null = null;
  isLoadingMembers = true;
  successMessage: string = '';
  errorMessage: string = '';
  
  constructor(private travelService: TravelService, private router: Router) {
    const nav = this.router.getCurrentNavigation();
    // Robust state retrieval from multiple sources
    const state = nav?.extras?.state as any || history.state || window.history.state || {};
    
    console.log('Transport Booking State:', state);

    if (state && state.data) {
      this.transport = state.data;
      this.tournamentId = state.tournamentId;
    }
  }

  ngOnInit() {
    if (!this.transport) {
      console.warn('No transport data found in state.');
      this.errorMessage = "No transport selected. Please go back and select one.";
      return;
    }
    
    this.coachId = localStorage.getItem('UserIdConnect');
    this.loadTeamMembers();
  }

  loadTeamMembers() {
    if (!this.coachId) return;
    this.isLoadingMembers = true;
    this.travelService.getMyTeamMembers(this.coachId).subscribe({
      next: (members) => {
        this.teamMembers = members;
        if (members.length > 0) {
           this.teamId = members[0].teamId || 0;
        }
        this.isLoadingMembers = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoadingMembers = false;
        this.errorMessage = "Failed to load team members.";
      }
    });
  }

  toggleMember(id: number) {
    if (this.selectedMemberIds.has(id)) {
      this.selectedMemberIds.delete(id);
    } else {
      this.selectedMemberIds.add(id);
    }
    this.calculateTotal();
  }

  calculateTotal() {
    this.totalPrice = this.selectedMemberIds.size * (this.transport?.pricePerSeat || 0);
  }

  submitRequest() {
    if (this.selectedMemberIds.size === 0) {
        alert("Please select at least one member.");
        return;
    }

    if (this.selectedMemberIds.size > this.transport.availableSeats) {
        alert("Not enough available seats on this transport!");
        return;
    }

    const requestData = {
      coachId: this.coachId,
      teamId: this.teamId,
      tournamentId: this.tournamentId, 
      transportId: this.transport.id,
      selectedMemberIds: Array.from(this.selectedMemberIds)
    };

    this.travelService.submitTravelRequest(requestData).subscribe({
      next: (res) => {
        this.successMessage = 'Transport request submitted! Awaiting admin approval.';
        setTimeout(() => this.router.navigate(['/coach/transport']), 2000);
      },
      error: (err) => alert('Error submitting transport request.')
    });
  }

  back() {
    this.router.navigate(['/coach/transport']);
  }

  formatPrice(val: number | undefined): string {
    if (val == null) return '0.00 TND';
    return val.toFixed(2) + ' TND';
  }

  formatDate(val: string | undefined): string {
    if (!val) return '-';
    return new Date(val).toLocaleString();
  }
}
