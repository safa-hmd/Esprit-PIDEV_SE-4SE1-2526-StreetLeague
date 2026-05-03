import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TravelService } from '../../services/travel.service';

@Component({
  selector: 'app-coach-accommodation',
  templateUrl: './coach-accommodation.component.html',
  styleUrls: ['./coach-accommodation.component.css']
})
export class CoachAccommodationComponent implements OnInit {

  // Data
  approvedAccommodations: any[] = [];
  filteredAccommodations: any[] = [];
  selectedAccommodation: any = null;
  teamMembers: any[] = [];
  selectedMemberIds: number[] = [];
  tournaments: any[] = [];
  selectedTournamentId: number = 0;
  myRequests: any[] = [];

  // Coach info
  coachId: number = 0;
  coachTeamId: number = 0;

  // UI state
  loading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  private baseUrl = 'http://localhost:8086/StreetLeague';

  constructor(
    private travelService: TravelService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const coachIdStr = 
      localStorage.getItem('idUser') || 
      localStorage.getItem('userId') || 
      localStorage.getItem('UserIdConnect');

    this.coachId = coachIdStr ? parseInt(coachIdStr) : 0;
    console.log('DEBUG [CoachAccommodation] coachId =', this.coachId, 'type =', typeof this.coachId);
    
    // Fallback if localStorage values are missing
    if (this.coachId === 0) {
      const user = JSON.parse(localStorage.getItem('UserObject') || '{}');
      this.coachId = user.id || user.idUser || 0;
    }

    console.log('=== coachId resolved:', this.coachId);
    
    if (this.coachId === 0) {
        this.errorMessage = 'Session expired. Please login again.';
        return;
    }

    this.loadTeamData();
    this.loadTournaments();
    this.loadApprovedAccommodations();
    this.loadMyRequests();
  }

  loadTeamData(): void {
    this.travelService.getMyTeam(this.coachId).subscribe({
      next: (team: any) => {
        this.coachTeamId = team.id || team.idTeam || 0;
        console.log('Team ID loaded for accommodation:', this.coachTeamId);
        this.loadTeamMembers();
      },
      error: () => this.loadCoachProfile()
    });
  }

  private getHeaders(): { [key: string]: string } {
    const token = localStorage.getItem('TokenUserConnect');
    return { Authorization: `Bearer ${token}` };
  }

  // ===================== LOAD COACH PROFILE =====================
  loadCoachProfile(): void {
    const headers = new HttpHeaders(this.getHeaders());
    this.http.get<any>(`${this.baseUrl}/api/users/me`, { headers }).subscribe({
      next: (profile: any) => {
        this.coachTeamId = profile.teamId || profile.team_id || 0;
        if (this.coachId > 0) {
          this.loadTeamMembers();
        }
      },
      error: (err: any) => console.error('Profile error:', err)
    });
  }

  // ===================== LOAD TEAM MEMBERS =====================
  loadTeamMembers(): void {
    console.log('=== COACH DEBUG [CoachAccommodation] ===');
    console.log('coachId passed to getMyTeamMembers:', this.coachId);
    
    const url = `${this.baseUrl}/api/coach/travel/team-members?coachId=${this.coachId}`;
    this.http.get<any[]>(url, { headers: new HttpHeaders(this.getHeaders()) }).subscribe({
      next: (members: any[]) => {
        console.log('Members received from API:', members);
        if (members && members.length > 0) {
          console.log('First member raw data:', members[0]);
        }
        
        this.teamMembers = (members || []).filter(u => {
          const role = (u.role || '').toString().toUpperCase();
          const uid = u.idUser || u.id;
          const isPlayer = role === 'PLAYER' && uid !== this.coachId;
          console.log(`Checking user ${uid}: role=${role}, isPlayer=${isPlayer}`);
          return isPlayer;
        });
        
        console.log('Final filtered teamMembers:', this.teamMembers);
        console.log('Members count:', this.teamMembers.length);
      },
      error: (err: any) => {
          console.error('Members API error:', err);
          console.error('Status:', err.status);
          console.error('URL called:', err.url);
      }
    });
  }

  // ===================== LOAD TOURNAMENTS =====================
  loadTournaments(): void {
    this.http.get<any[]>(`${this.baseUrl}/api/tournaments`, { headers: new HttpHeaders(this.getHeaders()) }).subscribe({
      next: (data: any[]) => {
        this.tournaments = data || [];
        if (this.tournaments.length > 0) {
          this.selectedTournamentId = this.tournaments[0].id;
          this.filterByTournamentCity();
        }
      }
    });
  }

  // ===================== LOAD ACCOMMODATIONS =====================
  loadApprovedAccommodations(): void {
    this.travelService.getApprovedAccommodations().subscribe({
      next: (data: any[]) => {
        this.approvedAccommodations = data || [];
        this.filterByTournamentCity();
      }
    });
  }

  // ===================== FILTER BY CITY =====================
  filterByTournamentCity(): void {
    const tournament = this.tournaments.find(t => t.id == this.selectedTournamentId);
    if (!tournament || !tournament.city) {
      this.filteredAccommodations = [...this.approvedAccommodations];
      return;
    }

    const city = tournament.city.toLowerCase().trim();
    this.filteredAccommodations = this.approvedAccommodations.filter(acc =>
      acc.address?.toLowerCase().includes(city)
    );
  }

  onTournamentChange(event: any): void {
    this.selectedTournamentId = +event.target.value;
    this.selectedAccommodation = null;
    this.selectedMemberIds = [];
    this.filterByTournamentCity();
  }

  selectAccommodation(acc: any): void {
    this.selectedAccommodation = acc;
  }

  // ===================== MEMBER SELECTION =====================
  selectAll(): void {
    this.selectedMemberIds = this.teamMembers.map(m => m.idUser || m.id);
  }

  deselectAll(): void {
    this.selectedMemberIds = [];
  }

  onMemberToggle(memberId: number, event: any) {
    if (event.target.checked) {
      if (!this.selectedMemberIds.includes(memberId)) {
        this.selectedMemberIds.push(memberId);
      }
    } else {
      this.selectedMemberIds = this.selectedMemberIds.filter(id => id !== memberId);
    }
  }

  toggleMember(member: any): void {
    const id = member.idUser || member.id;
    const idx = this.selectedMemberIds.indexOf(id);
    if (idx === -1) {
      this.selectedMemberIds.push(id);
    } else {
      this.selectedMemberIds.splice(idx, 1);
    }
  }

  isSelected(member: any): boolean {
    return this.selectedMemberIds.includes(member.idUser || member.id);
  }

  // ===================== AMOUNTS =====================
  get individualAmount(): number {
    if (!this.selectedAccommodation) return 0;
    return (this.selectedAccommodation.numberOfNights || 0) * (this.selectedAccommodation.pricePerNight || 0);
  }

  get totalAmount(): number {
    return this.individualAmount * this.selectedMemberIds.length;
  }

  submitRequest(): void {
    if (!this.selectedAccommodation || this.selectedMemberIds.length === 0) {
        alert("Please select an accommodation and at least one member.");
        return;
    }
    if (!this.selectedTournamentId || this.selectedTournamentId === 0) {
        alert("Please select a tournament first.");
        return;
    }

    this.loading = true;
    const requestData = {
      coachId: this.coachId,
      accommodationId: this.selectedAccommodation.id,
      memberIds: this.selectedMemberIds,
      tournamentId: this.selectedTournamentId,
      totalAmount: this.totalAmount,
      coachName: localStorage.getItem('userName') || 'Coach'
    };

    console.log('Submitting accommodation request:', requestData);

    this.travelService.submitAccommodationRequest(requestData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '✓ Accommodation request submitted for approval!';
        this.selectedAccommodation = null;
        this.selectedMemberIds = [];
        this.loadMyRequests();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err: any) => {
        this.loading = false;
        console.error('Submission error:', err);
        this.errorMessage = err.error?.message || 'Error submitting accommodation request.';
      }
    });
  }

  loadMyRequests(): void {
    this.travelService.getMyAccommodationRequests(this.coachId).subscribe({
      next: (data: any[]) => this.myRequests = data || []
    });
  }

  getFormattedFormula(formula: string): string {
    const labels: any = { 'FULL_BOARD': 'Full Board', 'HALF_BOARD': 'Half Board', 'BREAKFAST_INCLUDED': 'Breakfast Included', 'ROOM_ONLY': 'Room Only', 'ALL_INCLUSIVE': 'All Inclusive' };
    return labels[formula] || formula;
  }

  formatDate(dateString: string): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString();
  }

  downloadPdf(id: number): void {
    this.travelService.downloadAccommodationPdf(id).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `accommodation-voucher-${id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err: any) => {
        console.error('Download error:', err);
        alert('Failed to download the accommodation voucher.');
      }
    });
  }
}