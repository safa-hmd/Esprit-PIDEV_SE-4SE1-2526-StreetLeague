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
  isLoadingMembers = true;
  
  constructor(private travelService: TravelService, private router: Router) {
    const state = this.router.getCurrentNavigation()?.extras.state;
    if (state) {
      this.transport = state['data'];
      this.tournamentId = state['tournamentId'];
    }
  }

  ngOnInit() {
    if (!this.transport) {
      this.router.navigate(['/coach/transport']);
      return;
    }
    
    const coachId = localStorage.getItem('UserIdConnect');
    this.travelService.getMyTeamMembers(coachId).subscribe({
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
      }
    });
  }

  toggleMember(id: number) {
    if (this.selectedMemberIds.has(id)) {
      this.selectedMemberIds.delete(id);
    } else {
      this.selectedMemberIds.add(id);
    }
    this.totalPrice = this.selectedMemberIds.size * (this.transport.pricePerSeat || 0);
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

    const coachId = localStorage.getItem('UserIdConnect');
    const requestData = {
      coachId: coachId,
      teamId: this.teamId,
      tournamentId: this.tournamentId, 
      transportId: this.transport.id,
      selectedMemberIds: Array.from(this.selectedMemberIds)
    };

    this.travelService.submitTravelRequest(requestData).subscribe({
      next: (res) => {
        alert('Transport request submitted. Awaiting admin approval.');
        this.router.navigate(['/coach/transport']);
      },
      error: (err) => alert('Error submitting transport request.')
    });
  }

  back() {
    this.router.navigate(['/coach/transport']);
  }
}
