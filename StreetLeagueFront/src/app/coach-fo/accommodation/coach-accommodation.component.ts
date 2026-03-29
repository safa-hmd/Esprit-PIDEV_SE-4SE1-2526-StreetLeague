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
    this.loadCoachProfile();
    this.loadTournaments();
    this.loadApprovedAccommodations();
    this.loadMyRequests();
  }

  private getHeaders(): { [key: string]: string } {
    const token = localStorage.getItem('TokenUserConnect');
    return { Authorization: `Bearer ${token}` };
  }

  // ===================== LOAD COACH PROFILE =====================
  loadCoachProfile(): void {
    console.log('Loading coach profile...');
    this.http.get<any>(
      `${this.baseUrl}/api/users/me`,
      { headers: this.getHeaders() }
    ).subscribe({
      next: (profile) => {
        console.log('Coach profile loaded:', profile);

        // Backend returns idUser (not id) due to @Column(name="id") on idUser field
        this.coachId = profile.idUser || profile.id || 0;

        // team_id column mapped as teamId in User entity
        this.coachTeamId = profile.teamId || profile.team_id || 0;

        console.log(`Coach ID: ${this.coachId} | Team ID: ${this.coachTeamId}`);

        if (this.coachId > 0) {
          this.loadTeamMembers();
        } else {
          console.warn('Coach ID is 0 — cannot load team members');
        }
      },
      error: (err) => {
        console.error('Profile error:', err.status, err.error);
        this.errorMessage = 'Impossible de charger le profil coach.';
      }
    });
  }

  // ===================== LOAD TEAM MEMBERS =====================
  loadTeamMembers(): void {
    const url = `${this.baseUrl}/api/coach/travel/team-members?coachId=${this.coachId}`;
    console.log('Loading team members from:', url);

    this.http.get<any[]>(url, { headers: this.getHeaders() }).subscribe({
      next: (members) => {
        console.log('Team members raw response:', JSON.stringify(members));

        if (Array.isArray(members) && members.length > 0) {
          // Filter PLAYER role only, exclude the coach
          this.teamMembers = members.filter(u => {
            const role = (u.role || '').toString().toUpperCase();
            const uid = u.idUser || u.id;
            return role === 'PLAYER' && uid !== this.coachId;
          });
          console.log('Players found:', this.teamMembers.length);
        } else {
          console.warn('No members returned from API');
          this.teamMembers = [];
        }
      },
      error: (err) => {
        console.error('Team members error:', err.status, err.error);
      }
    });
  }

  // ===================== LOAD TOURNAMENTS =====================
  loadTournaments(): void {
    const url = `${this.baseUrl}/api/tournaments`;
    console.log('Loading tournaments from:', url);

    this.http.get<any[]>(url, { headers: this.getHeaders() }).subscribe({
      next: (data) => {
        console.log('Tournaments loaded:', data);
        this.tournaments = data || [];
        if (this.tournaments.length > 0) {
          this.selectedTournamentId = this.tournaments[0].id;
          this.filterByTournamentCity();
        }
      },
      error: (err) => console.error('Tournaments error:', err.status, err.error)
    });
  }

  // ===================== LOAD ACCOMMODATIONS =====================
  loadApprovedAccommodations(): void {
    this.travelService.getApprovedAccommodations().subscribe({
      next: (data) => {
        console.log('All accommodations:', data);
        this.approvedAccommodations = data || [];
        this.filterByTournamentCity();
      },
      error: (err) => console.error('Accommodations error:', err)
    });
  }

  // ===================== FILTER BY CITY =====================
  filterByTournamentCity(): void {
    const tournament = this.tournaments.find(
      t => t.id == this.selectedTournamentId
    );

    if (!tournament || !tournament.city) {
      this.filteredAccommodations = [...this.approvedAccommodations];
      return;
    }

    const city = tournament.city.toLowerCase().trim();
    console.log('Filtering accommodations by city:', city);

    this.filteredAccommodations = this.approvedAccommodations.filter(acc =>
      acc.address?.toLowerCase().includes(city)
    );

    console.log('Filtered count:', this.filteredAccommodations.length);
  }

  // ===================== TOURNAMENT CHANGE =====================
  onTournamentChange(event: any): void {
    this.selectedTournamentId = +event.target.value;
    this.selectedAccommodation = null;
    this.selectedMemberIds = [];
    this.filterByTournamentCity();
  }

  // ===================== SELECT ACCOMMODATION =====================
  selectAccommodation(acc: any): void {
    this.selectedAccommodation = acc;
    this.selectedMemberIds = [];
    console.log('Selected accommodation:', acc);
  }

  // ===================== MEMBER SELECTION =====================
  getMemberId(member: any): number {
    return member.idUser || member.id || 0;
  }

  isSelected(member: any): boolean {
    return this.selectedMemberIds.includes(this.getMemberId(member));
  }

  toggleMember(member: any): void {
    const id = this.getMemberId(member);
    const idx = this.selectedMemberIds.indexOf(id);
    if (idx === -1) {
      this.selectedMemberIds.push(id);
    } else {
      this.selectedMemberIds.splice(idx, 1);
    }
    console.log('Selected member IDs:', this.selectedMemberIds);
  }

  // ===================== AMOUNTS =====================
  get individualAmount(): number {
    if (!this.selectedAccommodation) return 0;
    const nights = this.selectedAccommodation.numberOfNights || 0;
    const price = this.selectedAccommodation.pricePerNight || 0;
    return nights * price;
  }

  get totalAmount(): number {
    if (!this.selectedAccommodation) return 0;
    const nights = this.selectedAccommodation.numberOfNights || 0;
    const price = this.selectedAccommodation.pricePerNight || 0;
    const members = this.selectedMemberIds.length;
    console.log(`Total: ${nights} nights × ${price} TND × ${members} members`);
    return nights * price * members;
  }

  // ===================== SUBMIT REQUEST =====================
  submitRequest(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.selectedAccommodation) {
      this.errorMessage = 'Veuillez sélectionner un hébergement.';
      return;
    }
    if (!this.selectedTournamentId) {
      this.errorMessage = 'Veuillez sélectionner un tournoi.';
      return;
    }
    if (!this.coachTeamId) {
      this.errorMessage = 'Team ID manquant. Reconnectez-vous.';
      return;
    }

    console.log('Submitting request:', {
      tournamentId: this.selectedTournamentId,
      teamId: this.coachTeamId,
      accommodationId: this.selectedAccommodation.id,
      memberIds: this.selectedMemberIds
    });

    this.loading = true;
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem('TokenUserConnect'),
      'Content-Type': 'application/json'
    });

    // Step 1: Init logistics
    const initUrl = `${this.baseUrl}/api/logistique/init`
      + `?tournamentId=${this.selectedTournamentId}`
      + `&teamId=${this.coachTeamId}`;

    this.http.post<any>(initUrl, {}, { headers }).subscribe({
      next: (logisticsResp) => {
        console.log('Logistics created:', logisticsResp);
        const logisticsId = logisticsResp.id || logisticsResp.logisticsId;

        // Step 2: Assign members to accommodation
        const assignUrl = `${this.baseUrl}/api/logistique/${logisticsId}`
          + `/hebergement/${this.selectedAccommodation.id}/membres`;

        this.http.post<any>(
          assignUrl,
          this.selectedMemberIds,
          { headers }
        ).subscribe({
          next: () => {
            this.loading = false;
            this.successMessage = 'Demande soumise avec succès ✓';
            this.selectedAccommodation = null;
            this.selectedMemberIds = [];
            this.loadMyRequests();
          },
          error: (err2) => {
            this.loading = false;
            this.errorMessage = 'Erreur assignation: '
              + (err2.error?.message || err2.status);
          }
        });
      },
      error: (err1) => {
        this.loading = false;
        this.errorMessage = 'Erreur init logistique: '
          + (err1.error?.message || err1.status);
      }
    });
  }

  // ===================== MY REQUESTS =====================
  loadMyRequests(): void {
    this.travelService.getMyAccommodationRequests(this.coachId).subscribe({
      next: (data) => {
        this.myRequests = data || [];
        console.log('My requests loaded:', this.myRequests.length);
      },
      error: (err) => console.error('My requests error:', err)
    });
  }

  // ===================== HELPERS =====================
  getFormattedFormula(formula: string): string {
    const labels: { [key: string]: string } = {
      'FULL_BOARD': 'Full Board',
      'HALF_BOARD': 'Half Board',
      'BREAKFAST_INCLUDED': 'Breakfast Included',
      'ROOM_ONLY': 'Room Only',
      'ALL_INCLUSIVE': 'All Inclusive'
    };
    return labels[formula] || formula;
  }

  getStatusClass(status: string): string {
    const classes: any = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected'
    };
    return classes[status] || 'status-pending';
  }
}