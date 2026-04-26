// src/app/frontoffice/matchmaking/matchmaking.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatchCandidate } from 'src/app/models/match-candidate.model';
import { MatchmakingService } from 'src/app/services/matchmaking.service';
import { AuthService } from 'src/app/services/auth.service';
import { TeamService } from 'src/app/services/team.service';

@Component({
  selector: 'app-matchmaking',
  templateUrl: './matchmaking.component.html',
  styleUrls: ['./matchmaking.component.css']
})
export class MatchmakingComponent implements OnInit {
  candidates: MatchCandidate[] = [];
  filtered: MatchCandidate[] = [];
  loading = false;
  error: string | null = null;
  challengeSent: number | null = null;

  // Modal
  showModal = false;
  modalType: 'exists' | 'success' | 'error' = 'success';
  modalTeamName = '';
  modalMessage = '';

  sortBy: 'matchCompatibilityScore' | 'eloScore' | 'h2hScore' = 'matchCompatibilityScore';
  minCompat = 0;

  myTeamId = 1;
  readonly myLocation = '36.8065,10.1815';
  myTeam: any;
  currentUserEmail: string = '';

  constructor(
    private matchmakingService: MatchmakingService,
    private http: HttpClient,
    private authService: AuthService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadMyTeam();
  }

  private loadCurrentUser(): void {
    this.currentUserEmail = localStorage.getItem('EmailUserConnect') || '';

    if (!this.currentUserEmail) {
      const token = this.authService.getToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          this.currentUserEmail = payload.sub || '';
        } catch (e) {}
      }
    }

    if (!this.currentUserEmail) {
      this.error = 'Please login to continue';
    }
  }

  private loadMyTeam(): void {
    this.loading = true;
    const captainId = Number(localStorage.getItem('IdUserConnect'));

    this.teamService.getMyTeams(captainId).subscribe({
      next: (teams) => {
        if (!teams || teams.length === 0) {
          this.error = 'You must be captain of a team to use matchmaking.';
          this.loading = false;
          return;
        }
        this.myTeam = teams[0];
        this.myTeamId = teams[0].idTeam!;
        this.loadCandidates();
      },
      error: () => {
        this.error = 'Unable to load your team.';
        this.loading = false;
      }
    });
  }

  loadCandidates(): void {
    this.loading = true;
    this.error = null;
    this.matchmakingService.getCandidates(this.myTeamId, this.myLocation).subscribe({
      next: (data) => {
        this.candidates = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Error loading candidates:', err);
        this.error = 'Unable to load opponents.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filtered = this.candidates
      .filter(c => c.matchCompatibilityScore >= this.minCompat)
      .sort((a, b) => b[this.sortBy] - a[this.sortBy]);
  }

  // ✅ Une seule méthode challenge() avec les modals
  challenge(candidate: MatchCandidate): void {
    if (!this.currentUserEmail) {
      this.showErrorModal('Please login to create a match');
      return;
    }

    this.challengeSent = candidate.teamId;

    const params = new HttpParams()
      .set('teamAId', this.myTeamId.toString())
      .set('teamBId', candidate.teamId.toString())
      .set('email', this.currentUserEmail);

    const matchDate = new Date();
    matchDate.setDate(matchDate.getDate() + 7);
    matchDate.setHours(15, 0, 0, 0);

    const body = {
      matchDate: matchDate.toISOString(),
      location: this.myLocation
    };

    this.http.post('http://localhost:8086/StreetLeague/match/add', body, { params })
      .subscribe({
        next: () => {
          this.challengeSent = null;
          this.showSuccessModal(candidate.teamName);
        },
        error: (err) => {
          this.challengeSent = null;
          const errorMsg: string = err.error || '';

          if (errorMsg.toLowerCase().includes('already exists') ||
              errorMsg.toLowerCase().includes('pending') ||
              errorMsg.toLowerCase().includes('accepted')) {
            this.showAlreadyExistsModal(candidate.teamName);
          } else {
            this.showErrorModal(errorMsg || 'Unable to create match.');
          }
        }
      });
  }

  // ── Modals ──────────────────────────────────────────────
  showAlreadyExistsModal(teamName: string): void {
    this.modalType = 'exists';
    this.modalTeamName = teamName;
    this.showModal = true;
  }

  showSuccessModal(teamName: string): void {
    this.modalType = 'success';
    this.modalTeamName = teamName;
    this.showModal = true;
  }

  showErrorModal(msg: string): void {
    this.modalType = 'error';
    this.modalMessage = msg;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  // ── Helpers ─────────────────────────────────────────────
  compatClass(score: number): string {
    if (score >= 75) return 'badge-high';
    if (score >= 50) return 'badge-mid';
    return 'badge-low';
  }

  initials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }
}