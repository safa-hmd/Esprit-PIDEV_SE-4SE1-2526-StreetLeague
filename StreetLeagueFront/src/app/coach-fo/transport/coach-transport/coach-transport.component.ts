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

  // Personal Car Form
  personalCar = {
    destination: '',
    availableSeats: 0,
    pricePerSeat: 0,
    departureTime: '',
    returnTime: ''
  };
  pcMessage: string = '';

  // My Requests
  myRequests: any[] = [];

  constructor(
    private travelService: TravelService,
    private authService: AuthService,
    private teamService: TeamService,
    private router: Router
  ) {}

  ngOnInit() {
    this.coachId = localStorage.getItem('UserIdConnect'); 
    this.loadTournaments();
    this.loadTeamData();
    this.loadMyRequests();
  }

  loadTeamData() {
    if (!this.coachId) return;
    this.teamService.getMyTeams(Number(this.coachId)).subscribe({
      next: (teams: Team[]) => {
        if (teams && teams.length > 0) {
          this.teamCity = teams[0].city || '';
          this.teamId = teams[0].idTeam ?? teams[0].id ?? null;
        }
      },
      error: (err) => console.error('Error loading team data', err)
    });
  }

  loadTournaments() {
    this.travelService.getTournaments().subscribe({
      next: (res) => {
        this.tournaments = res;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading tournaments', err);
        this.isLoading = false;
      }
    });
  }

  loadMyRequests() {
    if (!this.coachId) return;
    this.travelService.getMyTravelRequests(this.coachId).subscribe({
      next: (res) => this.myRequests = res,
      error: (err) => console.error('Error loading my requests', err)
    });
  }

  onTournamentChange() {
    if (!this.selectedTournamentId) {
      this.selectedTournament = null;
      this.isSameCity = false;
      this.filteredTransports = [];
      return;
    }

    this.selectedTournament = this.tournaments.find(t => t.id == this.selectedTournamentId) || null;
    
    if (this.selectedTournament && this.teamCity) {
      this.isSameCity = this.teamCity.toLowerCase() === this.selectedTournament.city.toLowerCase();
    }

    if (this.isSameCity) {
      this.filteredTransports = [];
    } else {
      this.checkEligibility();
    }
  }

  checkEligibility() {
    if (!this.coachId) {
       this.eligible = false;
       this.reason = 'Authentication required. Please log in as a Coach.';
       return;
    }

    this.isLoading = true;
    this.travelService.checkEligibilityAndGetTransports(this.coachId, this.selectedTournamentId!).subscribe({
      next: (res) => {
        this.eligible = res.eligible;
        this.reason = res.reason;
        this.transports = res.transports || [];
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.eligible = true;
        this.reason = 'Unable to verify eligibility. Showing options for this destination.';
        this.loadAllTransportsFallback();
      }
    });
  }

  loadAllTransportsFallback() {
    this.travelService.getTransports().subscribe({
      next: (res) => {
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

  bookTransport(transport: any) {
    this.router.navigate(['/coach/transport/book'], { 
      state: { 
        data: transport,
        tournamentId: this.selectedTournamentId 
      } 
    });
  }

  submitPersonalCarForm() {
    const data = {
      ...this.personalCar,
      coachId: this.coachId,
      teamId: this.teamId,
      type: 'PRIVATE_CAR'
    };

    this.travelService.submitPersonalCar(data).subscribe({
      next: () => {
        this.pcMessage = "Your vehicle has been submitted for admin approval. It will appear as a transport option once approved.";
        this.personalCar = { destination: '', availableSeats: 0, pricePerSeat: 0, departureTime: '', returnTime: '' };
      },
      error: () => alert('Error submitting personal car.')
    });
  }

  goToLodging() {
    this.router.navigate(['/coach/accommodation']);
  }
}
