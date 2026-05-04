import { environment } from 'src/environments/environment';
// src/app/frontoffice/matchmaking/matchmaking.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatchCandidate } from 'src/app/models/match-candidate.model';
import { MatchmakingService } from 'src/app/services/matchmaking.service';
import { AuthService } from 'src/app/services/auth.service';
import { TeamService } from 'src/app/services/team.service';

// Icônes par sport (emoji SVG-friendly)
const SPORT_ICONS: Record<string, string> = {
  FOOTBALL:   '⚽',
  BASKETBALL: '🏀',
  TENNIS:     '🎾',
  PADEL:      '🏓',
  VOLLEYBALL: '🏐',
  OTHER:      '🏅',
};

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

  // Filtres
  sortBy: 'matchCompatibilityScore' | 'eloScore' | 'h2hScore' = 'matchCompatibilityScore';
  minCompat = 0;

  // ── Nouveau : sélection du sport ─────────────────────────────
  availableSports: string[] = [];
  selectedSport = 'SAME'; // 'SAME' = sport de mon équipe (défaut), 'ALL' = tous, ou un sport précis

  myTeamId = 0;
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
    this.loadAvailableSports();
  }

  // ── Sports disponibles ────────────────────────────────────────
  private loadAvailableSports(): void {
    this.matchmakingService.getAvailableSports().subscribe({
      next: (sports) => { this.availableSports = sports; },
      error: () => {
        // Fallback si l'endpoint n'est pas encore déployé
        this.availableSports = ['FOOTBALL','BASKETBALL','TENNIS','PADEL','VOLLEYBALL','OTHER'];
      }
    });
  }

  /** Label affiché dans le select (ex: "⚽ Football") */
  sportLabel(sport: string): string {
    const icon = SPORT_ICONS[sport] ?? '🏅';
    const name = sport.charAt(0) + sport.slice(1).toLowerCase();
    return `${icon} ${name}`;
  }

  /** Déclenche un rechargement quand le sport change */
  onSportChange(): void {
    if (this.myTeamId) {
      this.loadCandidates();
    }
  }

  // ── Auth ─────────────────────────────────────────────────────
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
    let captainId = Number(localStorage.getItem('IdUserConnect'));
    if (!captainId) {
      const token = this.authService.getToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          captainId = payload.id ?? payload.userId ?? payload.user_id ?? payload.idUser ?? 0;
        } catch (e) { console.error('❌ Erreur décodage JWT:', e); }
      }
    }
    if (!captainId) {
      this.error = 'Session expirée. Veuillez vous reconnecter.';
      this.loading = false;
      return;
    }
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

  retry(): void {
    this.myTeamId = 0;
    this.error = null;
    this.loadMyTeam();
  }

  // ── Chargement des candidats ──────────────────────────────────
  loadCandidates(): void {
    if (!this.myTeamId) {
      this.error = 'Team not loaded yet. Please retry.';
      return;
    }
    this.loading = true;
    this.error = null;

    // 'SAME' → on ne passe pas de sport, le backend utilise le sport de l'équipe A
    const sportParam = this.selectedSport === 'SAME' ? null : this.selectedSport;

    this.matchmakingService.getCandidates(this.myTeamId, this.myLocation, 50, sportParam).subscribe({
      next: (data) => {
        this.candidates = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        const msg = err?.error?.message || err?.error || 'Unable to load opponents.';
        this.error = typeof msg === 'string' ? msg : 'Unable to load opponents.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filtered = this.candidates
      .filter(c => c.matchCompatibilityScore >= this.minCompat)
      .sort((a, b) => b[this.sortBy] - a[this.sortBy]);
  }

  // ── Challenge ─────────────────────────────────────────────────
  challenge(candidate: MatchCandidate): void {
    if (!this.currentUserEmail) { this.showErrorModal('Please login to create a match'); return; }
    if (!this.myTeamId) { this.showErrorModal('Team not loaded, please refresh the page.'); return; }

    this.challengeSent = candidate.teamId;

    const params = new HttpParams()
      .set('teamAId', this.myTeamId.toString())
      .set('teamBId', candidate.teamId.toString())
      .set('email', this.currentUserEmail);

    const matchDate = new Date();
    matchDate.setDate(matchDate.getDate() + 7);
    matchDate.setHours(15, 0, 0, 0);
    const pad = (n: number) => String(n).padStart(2, '0');
    const formattedDate =
      `${matchDate.getFullYear()}-${pad(matchDate.getMonth()+1)}-${pad(matchDate.getDate())}` +
      `T${pad(matchDate.getHours())}:${pad(matchDate.getMinutes())}:00`;

    this.http.post(`${environment.baseUrl}/match/add`,
      { matchDate: formattedDate, location: this.myLocation }, { params })
      .subscribe({
        next: () => { this.challengeSent = null; this.showSuccessModal(candidate.teamName); },
        error: (err) => {
          this.challengeSent = null;
          let errorMsg = '';
          if (typeof err.error === 'string') errorMsg = err.error;
          else if (err.error?.message) errorMsg = err.error.message;
          else if (err.error?.error) errorMsg = err.error.error;
          const lower = errorMsg.toLowerCase();
          if (lower.includes('already') || lower.includes('pending') ||
              lower.includes('accepted') || lower.includes('existe') || lower.includes('déjà')) {
            this.showAlreadyExistsModal(candidate.teamName);
          } else {
            this.showErrorModal(errorMsg || 'Unable to create match.');
          }
        }
      });
  }

  // ── Modals ────────────────────────────────────────────────────
  showAlreadyExistsModal(teamName: string): void { this.modalType = 'exists'; this.modalTeamName = teamName; this.showModal = true; }
  showSuccessModal(teamName: string): void { this.modalType = 'success'; this.modalTeamName = teamName; this.showModal = true; }
  showErrorModal(msg: string): void { this.modalType = 'error'; this.modalMessage = msg; this.showModal = true; }
  closeModal(): void { this.showModal = false; }

  // ── Helpers ───────────────────────────────────────────────────
  compatClass(score: number): string {
    if (score >= 75) return 'badge-high';
    if (score >= 50) return 'badge-mid';
    return 'badge-low';
  }

  initials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }

  sportIcon(sport: string): string {
    return SPORT_ICONS[sport?.toUpperCase()] ?? '🏅';
  }
}

