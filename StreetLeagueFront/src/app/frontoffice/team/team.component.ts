import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TeamService } from '../../services/team.service';
import { MatchService } from '../../services/match.service';
import { Team } from '../../models/team.model';
import { ActivatedRoute } from '@angular/router';
import { MatchRequest, MatchResponse } from '../../models/match.model';

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.css']
})
export class TeamComponent implements OnInit {

  teams: Team[]         = [];
  filteredTeams: Team[] = [];
  matches: MatchResponse[] = [];
  isLoadingMatches = false;

  errorMsg    = '';
  successMsg  = '';
  activeTab   = 'my-teams';
  searchQuery = '';

  showCreateTeamModal  = false;
  showEditTeamModal    = false;   // ← nouveau
  showCreateMatchModal = false;

  currentUserEmail = '';

  newTeam:  Team = { name: '', sport: 'Soccer', description: '', level: 'BEGINNER' };
  editTeam: Team = { name: '', sport: '',       description: '', level: '' };  // ← nouveau

  newMatch: MatchRequest = { matchDate: '', location: '' };
  teamAId = 0;
  teamBId = 0;

 constructor(
  private router: Router,
  private route: ActivatedRoute,
  private teamService: TeamService,
  private matchService: MatchService
) {}

ngOnInit(): void {
  this.currentUserEmail = localStorage.getItem('EmailUserConnect') || '';
  this.loadTeams();
  this.loadMatches();

  // ← Lire le tab depuis l'URL
  this.route.queryParams.subscribe(params => {
    if (params['tab']) {
      this.activeTab = params['tab'];
    }
  });
}


isMyTeam(team: Team): boolean {
  return team.captainEmail?.toLowerCase() === this.currentUserEmail?.toLowerCase();
}


myTeams: Team[]      = [];
otherTeams: Team[]   = [];
availableTeamsB: Team[] = [];
isCaptain = false;

loadTeams(): void {
  this.teamService.getAllTeams().subscribe({
    next: (data) => {
      this.teams        = data;
      this.filteredTeams = data;
      this.myTeams      = data.filter(t => this.isMyTeam(t));
      this.otherTeams   = data.filter(t => !this.isMyTeam(t));
      this.availableTeamsB = this.otherTeams;
      this.isCaptain    = this.myTeams.length > 0;
    },
    error: (err) => { this.errorMsg = `Error ${err.status}`; }
  });
}
searchMatchQuery = '';
filteredMatches: MatchResponse[] = [];

loadMatches(): void {
  this.isLoadingMatches = true;
  this.matchService.getAllMatchs().subscribe({
    next: (data) => {
      this.matches = data;
      this.filteredMatches = data;  // ← ajouter
      this.isLoadingMatches = false;
    },
    error: (err) => { this.errorMsg = `Error ${err.status}`; this.isLoadingMatches = false; }
  });
}
onSearchMatch(query: string): void {
  this.searchMatchQuery = query;
  const q = query.toLowerCase();
  this.filteredMatches = this.matches.filter(m =>
    m.teamAName.toLowerCase().includes(q) ||
    m.teamBName.toLowerCase().includes(q) ||
    (m.location || '').toLowerCase().includes(q) ||
    m.status.toLowerCase().includes(q)
  );
}

  // ── Search ────────────────────────────────────────────────────────────
  onSearch(query: string): void {
    this.searchQuery   = query;
    const q = query.toLowerCase();
    this.filteredTeams = this.teams.filter(t =>
      t.name.toLowerCase().includes(q) ||
      t.sport.toLowerCase().includes(q) ||
      (t.description || '').toLowerCase().includes(q)
    );
  }

  // ── Tabs ──────────────────────────────────────────────────────────────
  switchTab(tab: string): void {
    this.activeTab = tab;
    if (tab === 'matches' && this.matches.length === 0) this.loadMatches();
  }

  // ── Create Team ───────────────────────────────────────────────────────
  createTeam(): void {
    if (!this.newTeam.name || !this.newTeam.sport) {
      this.errorMsg = 'Name and sport are required.';
      return;
    }
    this.teamService.addTeam(this.newTeam).subscribe({
      next: (created) => {
        this.successMsg = `Team "${created.name}" created! Captain: ${created.captainFullName}`;
        this.showCreateTeamModal = false;
        this.newTeam = { name: '', sport: 'Soccer', description: '', level: 'BEGINNER' };
        this.loadTeams();
        setTimeout(() => this.successMsg = '', 4000);
      },
      error: (err) => { this.errorMsg = `Error: ${err.error?.message || err.status}`; }
    });
  }

  // ── Edit Team ─────────────────────────────────────────────────────────
  openEditTeamModal(team: Team): void {
    this.editTeam = { ...team };   // copie pour ne pas modifier l'original
    this.showEditTeamModal = true;
  }

updateTeam(): void {
  this.teamService.updateTeam(this.editTeam, this.currentUserEmail).subscribe({
    next: () => {
      this.successMsg = 'Team updated successfully!';
      this.showEditTeamModal = false;
      this.loadTeams();
      setTimeout(() => this.successMsg = '', 3000);
    },
    error: (err) => { this.errorMsg = `Error: ${err.error?.message || err.status}`; }
  });
}

  // ── Delete Team ───────────────────────────────────────────────────────
deleteTeam(idTeam: number): void {
  if (!confirm('Delete this team permanently?')) return;
  this.teamService.deleteTeam(idTeam, this.currentUserEmail).subscribe({
    next: () => {
      this.successMsg = 'Team deleted.';
      this.loadTeams();
      setTimeout(() => this.successMsg = '', 3000);
    },
    error: (err) => { this.errorMsg = `Error: ${err.error?.message || err.status}`; }
  });
}

joinTeam(idTeam: number): void {
  const email = localStorage.getItem('EmailUserConnect');
  this.teamService.joinTeam(idTeam, email!).subscribe({
    next: () => {
      this.successMsg = 'You joined the team successfully!';
      this.loadTeams();
      setTimeout(() => this.successMsg = '', 3000);
    },
    error: (err) => { this.errorMsg = `Error: ${err.error?.message || err.status}`; }
  });
}

  // ── Match ─────────────────────────────────────────────────────────────
createMatch(): void {
  if (!this.newMatch.matchDate || !this.teamAId || !this.teamBId) {
    this.errorMsg = 'Date, Team A and Team B are required.';
    return;
  }
  if (this.teamAId === this.teamBId) {
    this.errorMsg = 'Team A and Team B must be different.';
    return;
  }
  this.matchService.addMatch(this.newMatch, this.teamAId, this.teamBId).subscribe({
    next: () => {
      this.successMsg = 'Match created successfully!';
      this.showCreateMatchModal = false;
      this.resetMatchForm();
      this.loadMatches();
      setTimeout(() => this.successMsg = '', 3000);
    },
    error: (err) => {
      // ← affiche le message exact du backend
      console.error('Backend error:', err);
      this.errorMsg = err.error?.message || err.error || `Error ${err.status}`;
    }
  });
}

  deleteMatch(idMatch: number): void {
    if (!confirm('Delete this match?')) return;
    this.matchService.deleteMatch(idMatch).subscribe({
      next: () => {
        this.matches = this.matches.filter(m => m.idMatch !== idMatch);
        this.successMsg = 'Match deleted.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = `Error: ${err.status}`; }
    });
  }
  canDeleteMatch(match: MatchResponse): boolean {
  const email = this.currentUserEmail.toLowerCase();
  return (
    match.captainAEmail?.toLowerCase() === email ||
    match.captainBEmail?.toLowerCase() === email
  );
}

  goToDetails(match: MatchResponse): void {
    this.router.navigate(['/client/detail-match', match.idMatch]);
  }

resetMatchForm(): void {
  this.newMatch        = { matchDate: '', location: '' };
  this.teamAId         = 0;
  this.teamBId         = 0;
  this.captainAName    = '';
  this.captainBName    = '';
  this.availableTeamsB = this.otherTeams;
  this.errorMsg        = '';
}

  getStatusClass(status: string): string {
    switch (status) {
      case 'SCHEDULED': return 'badge-blue';
      case 'ONGOING':   return 'badge-orange';
      case 'FINISHED':  return 'badge-green';
      case 'CANCELLED': return 'badge-red';
      default:          return 'badge-gray';
    }
  }

  // ── Navigate to Detail page ───────────────────────────────────────────
    goToTeamDetail(idTeam: number): void {
  this.router.navigate(['/client/detail-team', idTeam]);
  
}



// ── Ajouter ces propriétés ────────────────────────────────────────────
captainAName = '';
captainBName = '';

// ── Ajouter ces méthodes ──────────────────────────────────────────────
onTeamAChange(): void {
  const team = this.myTeams.find(t => t.idTeam === Number(this.teamAId));
  this.captainAName    = team?.captainFullName || '';
  // Exclure la team A des options de Team B
  this.availableTeamsB = this.teams.filter(t => t.idTeam !== Number(this.teamAId) && !this.isMyTeam(t));
  this.teamBId = 0;
  this.captainBName = '';
}

onTeamBChange(): void {
  const team = this.teams.find(t => t.idTeam === Number(this.teamBId));
  this.captainBName = team?.captainFullName || '';
}



}