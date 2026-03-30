import { Component, OnInit } from '@angular/core';
import { TravelService } from 'src/app/services/travel.service';
import { AuthService } from 'src/app/services/auth.service';
import { TeamService } from 'src/app/services/team.service';
import { Router } from '@angular/router';
import { Team } from 'src/app/models/team.model';
import { Tournament } from 'src/app/models/tournament.model';

@Component({
  selector: 'app-coach-transport',
  templateUrl: './coach-transport.component.html',
  styleUrls: ['./coach-transport.component.css']
})
export class CoachTransportComponent implements OnInit {
  eligible: boolean = false;
  reason: string = '';
  transports: any[] = [];
  filteredTransports: any[] = [];
  tournaments: Tournament[] = [];
  selectedTournamentId: number | null = null;
  selectedTournament: Tournament | null = null;
  isLoading = true;
  coachId: string | null = null;
  teamCity: string = '';
  teamId: number | null = null;
  isSameCity: boolean = false;

  // New UI selection states
  selectedTransport: any = null;
  teamMembers: any[] = [];
  selectedMemberIds: number[] = [];
  isLoadingMembers = false;
  totalPrice: number = 0;

  // Personal Car Form
  personalCar = {
    destination: '',
    availableSeats: 0,
    pricePerSeat: 0,
    departureTime: '',
    returnTime: '',
    type: 'CAR'
  };
  pcMessage: string = '';
  pcError: string = '';

  // My Requests
  myRequests: any[] = [];

  constructor(
    private travelService: TravelService,
    private authService: AuthService,
    private teamService: TeamService,
    private router: Router
  ) {}

  ngOnInit() {
    const coachIdStr = 
      localStorage.getItem('idUser') || 
      localStorage.getItem('userId') || 
      localStorage.getItem('UserIdConnect');

    this.coachId = coachIdStr ? coachIdStr : null;
    console.log('DEBUG [CoachTransport] coachId =', this.coachId, 'type =', typeof this.coachId);
    
    // Fallback if localStorage values are missing
    if (!this.coachId || this.coachId === '0') {
      const user = JSON.parse(localStorage.getItem('UserObject') || '{}');
      this.coachId = String(user.id || user.idUser || '');
    }

    console.log('=== coachId resolved:', this.coachId);
    
    if (!this.coachId || this.coachId === '0' || this.coachId === '') {
        alert('Session expired. Please login again.');
        return;
    }

    this.loadTournaments();
    this.loadTeamData();
    this.loadMyRequests();
    this.loadTeamMembers();
  }

  loadTeamData() {
    if (!this.coachId) return;
    this.travelService.getMyTeam(this.coachId).subscribe({
      next: (team: any) => {
          this.teamCity = team.city || '';
          this.teamId = team.id || team.idTeam || null;
          console.log('Team data loaded via dedicated API:', team);
      },
      error: () => {
        console.warn('Dedicated team API failed, falling back to legacy service...');
        this.teamService.getMyTeams(Number(this.coachId)).subscribe({
          next: (teams: Team[]) => {
            if (teams && teams.length > 0) {
              this.teamCity = teams[0].city || '';
              this.teamId = teams[0].idTeam ?? teams[0].id ?? null;
            }
          },
          error: (err: any) => console.error('Error loading team data', err)
        });
      }
    });
  }

  loadTeamMembers(): void {
    console.log('=== COACH DEBUG [CoachTransport] ===');
    console.log('coachId passed to getMyTeamMembers:', this.coachId);
    
    this.isLoadingMembers = true;
    this.travelService.getMyTeamMembers(this.coachId!).subscribe({
      next: (members: any[]) => {
        console.log('Members received from API:', members);
        if (members) {
          console.log('First member raw data:', members[0]);
        }
        console.log('Members count:', members ? members.length : 0);
        
        this.teamMembers = (members || []).filter(u => {
            const role = (u.role || '').toString().toUpperCase();
            const uid = u.idUser || u.id;
            const isPlayer = role === 'PLAYER';
            console.log(`Checking user ${uid}: role=${role}, isPlayer=${isPlayer}`);
            return isPlayer;
        });
        
        console.log('Final filtered teamMembers:', this.teamMembers);
        this.isLoadingMembers = false;
      },
      error: (err: any) => {
          console.error('Members API error:', err);
          console.error('Status:', err.status);
          console.error('URL called:', err.url);
          this.isLoadingMembers = false;
      }
    });
  }

  loadTournaments() {
    this.travelService.getTournaments().subscribe({
      next: (res: any[]) => {
        this.tournaments = res;
        this.isLoading = false;
        if (this.tournaments.length > 0) {
            this.selectedTournamentId = Number(this.tournaments[0].id);
            this.onTournamentChange();
        }
      },
      error: (err: any) => {
        console.error('Error loading tournaments', err);
        this.isLoading = false;
      }
    });
  }

  loadMyRequests() {
    if (!this.coachId) return;
    this.travelService.getMyTravelRequests(this.coachId).subscribe({
      next: (res: any[]) => {
          console.log("\n\n=== MY REQUESTS FROM BACKEND ===", res);
          this.myRequests = res;
      },
      error: (err: any) => console.error('Error loading my requests', err)
    });
  }

  onTournamentChange() {
    this.selectedTransport = null;
    this.selectedMemberIds = [];

    if (!this.selectedTournamentId) {
      this.selectedTournament = null;
      this.isSameCity = false;
      this.filteredTransports = [];
      return;
    }

    this.selectedTournament = this.tournaments.find(t => t.id == this.selectedTournamentId) || null;
    
    if (this.selectedTournament && this.teamCity) {
      this.isSameCity = this.teamCity.toLowerCase().trim() === this.selectedTournament.city.toLowerCase().trim();
    }

    if (this.isSameCity) {
      this.filteredTransports = [];
    } else {
      this.checkEligibility();
    }
  }

  checkEligibility() {
    this.isLoading = true;
    this.travelService.checkEligibilityAndGetTransports(this.coachId!, this.selectedTournamentId!).subscribe({
      next: (res: any) => {
        this.eligible = res.eligible;
        this.reason = res.reason;
        this.transports = res.transports || [];
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error(err);
        this.eligible = true;
        this.loadAllTransportsFallback();
      }
    });
  }

  loadAllTransportsFallback() {
    this.travelService.getTransports().subscribe({
      next: (res: any[]) => {
        this.transports = res;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  applyFilter() {
    if (!this.selectedTournament) return;
    const dest = this.selectedTournament.city.toLowerCase();
    this.filteredTransports = this.transports.filter(t => 
       t.destination.toLowerCase() === dest
    );
  }

  selectTransport(t: any) {
    if (t.availableSeats === 0) return;
    this.selectedTransport = t;
    this.selectedMemberIds = [];
    this.calculateTotal();
  }

  onMemberToggle(memberId: number, event: any) {
    if (event.target.checked) {
      if (!this.selectedMemberIds.includes(memberId)) {
        this.selectedMemberIds.push(memberId);
      }
    } else {
      this.selectedMemberIds = this.selectedMemberIds.filter(id => id !== memberId);
    }
    this.calculateTotal();
  }

  toggleMember(id: number) {
    const idx = this.selectedMemberIds.indexOf(id);
    if (idx === -1) {
      this.selectedMemberIds.push(id);
    } else {
      this.selectedMemberIds.splice(idx, 1);
    }
    this.calculateTotal();
  }

  selectAllMembers() {
    this.selectedMemberIds = this.teamMembers.map(m => m.idUser || m.id);
    this.calculateTotal();
  }

  isSelected(m: any): boolean {
    const id = m.idUser || m.id;
    return this.selectedMemberIds.includes(id);
  }

  calculateTotal() {
    if (!this.selectedTransport) {
      this.totalPrice = 0;
      return;
    }
    this.totalPrice = this.selectedMemberIds.length * (this.selectedTransport.pricePerSeat || 0);
  }

  confirmBooking() {
    if (!this.selectedTournamentId) {
        alert('Please select a tournament first');
        return;
    }
    if (!this.selectedTransport || this.selectedMemberIds.length === 0) return;

    if (this.selectedMemberIds.length > this.selectedTransport.availableSeats) {
        alert("Not enough seats available!");
        return;
    }

    const requestData = {
      coachId: this.coachId,
      teamId: this.teamId,
      tournamentId: this.selectedTournamentId,
      transportId: this.selectedTransport.id,
      selectedMemberIds: this.selectedMemberIds
    };

    this.travelService.submitTravelRequest(requestData).subscribe({
      next: () => {
        this.pcMessage = "✓ Transport booking request submitted successfully!";
        this.selectedTransport = null;
        this.selectedMemberIds = [];
        this.loadMyRequests();
        setTimeout(() => this.pcMessage = '', 3000);
      },
      error: (err: any) => {
        console.error('Submission error:', err);
        const serverMsg = err.error?.message || 'Error submitting transport booking.';
        alert(serverMsg);
      }
    });
  }

  submitPersonalCarForm() {
    this.pcMessage = '';
    this.pcError = '';

    // Validation 1: Capacity (max 5 avec chauffeur)
    if (this.personalCar.availableSeats > 5) {
      this.pcError = 'Error: Maximum payload capacity is 5 seats (including driver).';
      return;
    }

    // Validation 2: Departure Date (at least 5 days from today)
    const now = new Date();
    const minDepartureDate = new Date();
    minDepartureDate.setDate(now.getDate() + 5);
    const depDate = new Date(this.personalCar.departureTime);

    if (depDate < minDepartureDate) {
      this.pcError = 'Error: Initial Departure must be at least 5 days in the future.';
      return;
    }

    // Validation 3: Return Date (after departure)
    const retDate = new Date(this.personalCar.returnTime);
    if (retDate <= depDate) {
      this.pcError = 'Error: Standby Return must be strictly after Initial Departure.';
      return;
    }

    const data = {
      ...this.personalCar,
      coachId: this.coachId,
      teamId: this.teamId,
      type: 'PRIVATE_CAR'
    };

    this.travelService.submitPersonalCar(data).subscribe({
      next: () => {
        this.pcMessage = "✅ Personal vehicle submitted! Awaiting admin approval.";
        this.personalCar = { destination: '', availableSeats: 0, pricePerSeat: 0, departureTime: '', returnTime: '', type: 'CAR' };
        this.loadMyRequests();
        setTimeout(() => this.pcMessage = '', 8000);
      },
      error: () => this.pcError = 'Error during submission. Please check all fields.'
    });
  }

  goToLodging() {
    this.router.navigate(['/coach/accommodation']);
  }

  getTransportIcon(type: string): string {
    switch (type?.toUpperCase()) {
      case 'BUS': return 'fas fa-bus';
      case 'TRAIN': return 'fas fa-train';
      case 'VAN': return 'fas fa-shuttle-van';
      case 'FLIGHT': return 'fas fa-plane';
      case 'PRIVATE_CAR': return 'fas fa-car';
      case 'CAR': return 'fas fa-car';
      default: return 'fas fa-bus';
    }
  }

  formatDate(d: any) {
    if (!d) return '-';
    return new Date(d).toLocaleDateString();
  }

  downloadPdf(requestId: number): void {
    // We try to download as a TravelRequest (Team Transport) first
    this.travelService.downloadTravelRequestPdf(requestId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `transport-voucher-${requestId}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err: any) => {
        console.error('Download error:', err);
        alert('Failed to download the transport voucher.');
      }
    });
  }
}
