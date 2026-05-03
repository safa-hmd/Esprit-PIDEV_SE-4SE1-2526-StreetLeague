import { Component, OnInit } from '@angular/core';
import { TeamService } from 'src/app/services/team.service';
import { MatchService } from 'src/app/services/match.service';
import { Team } from 'src/app/models/team.model';
import { MatchResponse } from 'src/app/models/match.model';

@Component({
  selector: 'app-list-teams',
  standalone: false,
  templateUrl: './list-teams.component.html',
  styleUrls: ['./list-teams.component.css']
})
export class ListTeamsComponent implements OnInit {

  // ── Teams ─────────────────────────────────────────────────
  teams: Team[] = [];
  filteredTeams: Team[] = [];
  searchQuery: string = '';
  errorMsg: string = '';
  isLoading: boolean = false;

  // ── Matches ───────────────────────────────────────────────
  matches: MatchResponse[] = [];
  filteredMatches: MatchResponse[] = [];
  matchSearchQuery: string = '';
  isLoadingMatches: boolean = false;
  matchErrorMsg: string = '';

  constructor(
    private teamService: TeamService,
    private matchService: MatchService
  ) {}

  ngOnInit(): void {
    this.loadTeams();
    this.loadMatches();
  }

  // ── Load Teams ────────────────────────────────────────────
  loadTeams(): void {
    this.isLoading = true;
    this.teamService.getAllTeams().subscribe({
      next: (data: Team[]) => {
        this.teams = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err: any) => {
        this.errorMsg = 'Error loading teams.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // ── Load Matches ──────────────────────────────────────────
  loadMatches(): void {
    this.isLoadingMatches = true;
    this.matchService.getAllMatchs().subscribe({
      next: (data: MatchResponse[]) => {
        this.matches = data;
        this.filteredMatches = data;
        this.isLoadingMatches = false;
      },
      error: (err: any) => {
        this.matchErrorMsg = 'Error loading matches.';
        this.isLoadingMatches = false;
        console.error(err);
      }
    });
  }

  // ── Filter Teams ──────────────────────────────────────────
  applyFilter(): void {
    const q = this.searchQuery.toLowerCase().trim();
    this.filteredTeams = this.teams.filter(team =>
      !q
      || team.name.toLowerCase().includes(q)
      || (team.captainFullName?.toLowerCase().includes(q) ?? false)
    );
  }

  // ── Filter Matches ────────────────────────────────────────
  applyMatchFilter(): void {
    const q = this.matchSearchQuery.toLowerCase().trim();
    this.filteredMatches = this.matches.filter(m =>
      !q
      || m.teamAName?.toLowerCase().includes(q)
      || m.teamBName?.toLowerCase().includes(q)
      || m.location?.toLowerCase().includes(q)
    );
  }

  // ── Status badge CSS ──────────────────────────────────────
  getStatusClass(statut: string): string {
    switch (status) {
      case 'SCHEDULED': return 'a-badge-blue';
      case 'ONGOING':   return 'a-badge-orange';
      case 'FINISHED':  return 'a-badge-green';
      case 'CANCELLED': return 'a-badge-red';
      default:          return 'a-badge-gray';
    }
  }

  // ── Team actions ──────────────────────────────────────────
  viewTeam(team: Team): void {
    console.log('View team:', team);
  }

deleteTeam(team: Team): void {
  if (!confirm(`Delete "${team.name}" permanently?`)) return;

  const idTeam = team.idTeam ?? 0;
  
  // ✅ Utiliser l'email de l'admin connecté
  const email = localStorage.getItem('EmailUserConnect') || '';

  if (!email) {
    this.errorMsg = 'Admin not connected.';
    return;
  }

  this.teamService.deleteTeam(idTeam, email).subscribe({
    next: () => {
      this.teams = this.teams.filter(t => t.idTeam !== idTeam);
      this.applyFilter();
    },
    error: (err: any) => {
      this.errorMsg = `Cannot delete "${team.name}": ${err.error?.message || err.status}`;
      console.error(err);
    }
  });
}

  // ── Match actions ─────────────────────────────────────────
  deleteMatch(idMatch: number): void {
    if (!confirm('Delete this match?')) return;
    this.matchService.deleteMatch(idMatch).subscribe({
      next: () => {
        this.matches = this.matches.filter(m => m.idMatch !== idMatch);
        this.filteredMatches = this.filteredMatches.filter(m => m.idMatch !== idMatch);
      },
      error: (err: any) => {
        this.matchErrorMsg = 'Cannot delete this match.';
        console.error(err);
      }
    });
  }

  openAddModal(): void {}


  // ── Teams Pagination ──────────────────────────────────────
teamPage: number = 1;
teamPageSize: number = 5;

get pagedTeams(): Team[] {
  const start = (this.teamPage - 1) * this.teamPageSize;
  return this.filteredTeams.slice(start, start + this.teamPageSize);
}
get teamTotalPages(): number {
  return Math.ceil(this.filteredTeams.length / this.teamPageSize);
}
changeTeamPage(p: number): void {
  if (p >= 1 && p <= this.teamTotalPages) this.teamPage = p;
}

// ── Matches Pagination ────────────────────────────────────
matchPage: number = 1;
matchPageSize: number = 5;

get pagedMatches(): MatchResponse[] {
  const start = (this.matchPage - 1) * this.matchPageSize;
  return this.filteredMatches.slice(start, start + this.matchPageSize);
}
get matchTotalPages(): number {
  return Math.ceil(this.filteredMatches.length / this.matchPageSize);
}
changeMatchPage(p: number): void {
  if (p >= 1 && p <= this.matchTotalPages) this.matchPage = p;
}
}

