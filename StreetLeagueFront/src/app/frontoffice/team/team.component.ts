import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TeamService } from '../../services/team.service';
import { MatchService } from '../../services/match.service';
import { Team } from '../../models/team.model';
import { MatchRequest, MatchResponse } from '../../models/match.model';

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.css']
})
export class TeamComponent implements OnInit {

  teams: Team[]            = [];
  filteredTeams: Team[]    = [];
  matches: MatchResponse[] = [];
  filteredMatches: MatchResponse[] = [];
  isLoadingMatches = false;

  errorMsg   = '';
  successMsg = '';
  activeTab  = 'my-teams';
  searchQuery      = '';
  searchMatchQuery = '';
  availableSports = ['Football', 'Basketball', 'Volleyball', 'Tennis', 'Handball', 'Rugby', 'Baseball', 'Cricket', 'Padel'];
  selectedSportFilter = 'All';

  showCreateTeamModal  = false;
  showEditTeamModal    = false;
  showCreateMatchModal = false;

  currentUserEmail = '';

  myTeams:        Team[] = [];
  otherTeams:     Team[] = [];
  availableTeamsB: Team[] = [];
  isCaptain = false;

  captainAName = '';
  captainBName = '';
  teamAId = 0;
  teamBId = 0;

  // ── Reactive Forms ────────────────────────────────────────
  createTeamForm!: FormGroup;
  editTeamForm!:   FormGroup;
  createMatchForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private teamService: TeamService,
    private matchService: MatchService
  ) {}

  ngOnInit(): void {
    this.currentUserEmail = localStorage.getItem('EmailUserConnect') || '';
    this.loadTeams();

    this.route.queryParams.subscribe(params => {
      if (params['tab']) this.activeTab = params['tab'];
    });

    // ── Init Create Team Form ──────────────────────────────
    this.createTeamForm = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      sport:       ['Football', Validators.required],
      level:       ['BEGINNER', Validators.required],
      description: ['', Validators.maxLength(255)]
    });

    // ── Init Edit Team Form ────────────────────────────────
    this.editTeamForm = this.fb.group({
      idTeam:      [null],
      name:        ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      sport:       ['', Validators.required],
      level:       ['', Validators.required],
      description: ['', Validators.maxLength(255)]
    });

    // ── Init Create Match Form ─────────────────────────────
    this.createMatchForm = this.fb.group({
      matchDate: ['', Validators.required],
      location:  ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]]
    });
  }

  // ── Helpers pour accéder aux champs ───────────────────────
  get ctf() { return this.createTeamForm.controls; }
  get etf() { return this.editTeamForm.controls; }
  get cmf() { return this.createMatchForm.controls; }

  // ── Load ──────────────────────────────────────────────────
  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams           = data;
        this.applyFilters();
        this.myTeams         = data.filter(t => this.isMyTeam(t));
        this.otherTeams      = data.filter(t => !this.isMyTeam(t));
        this.availableTeamsB = this.otherTeams;
        this.isCaptain       = this.myTeams.length > 0;
        this.loadMatches();
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
  }

  loadMatches(): void {
    this.isLoadingMatches = true;
    this.matchService.getAllMatchs().subscribe({
      next: (data) => {
        const userTeams = this.teams.filter(t => this.isMyTeam(t) || this.isPlayerInTeam(t)).map(t => t.name.toLowerCase());
        const userMatches = data.filter(m => 
          userTeams.includes(m.teamAName.toLowerCase()) || 
          userTeams.includes(m.teamBName.toLowerCase())
        );
        this.matches         = userMatches;
        this.filteredMatches = userMatches;
        this.isLoadingMatches = false;
        this.onSearchMatch(this.searchMatchQuery);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; this.isLoadingMatches = false; }
    });
  }

  isMyTeam(team: Team): boolean {
    return team.captainEmail?.toLowerCase() === this.currentUserEmail?.toLowerCase();
  }

  // ── Search & Filter ───────────────────────────────────────
  onSearch(query: string): void {
    this.searchQuery = query;
    this.applyFilters();
  }

  setSportFilter(sport: string): void {
    this.selectedSportFilter = sport;
    this.applyFilters();
  }

  applyFilters(): void {
    const q = this.searchQuery.toLowerCase();
    this.filteredTeams = this.teams.filter(t => {
      const matchQuery = t.name.toLowerCase().includes(q) ||
        (t.description || '').toLowerCase().includes(q);
      const matchSport = this.selectedSportFilter === 'All' || t.sport.toLowerCase() === this.selectedSportFilter.toLowerCase();
      return matchQuery && matchSport;
    });
  }

  onSearchMatch(query: string): void {
    this.searchMatchQuery = query;
    const q = query.toLowerCase();
    this.filteredMatches  = this.matches.filter(m =>
      m.teamAName.toLowerCase().includes(q) ||
      m.teamBName.toLowerCase().includes(q) ||
      (m.location || '').toLowerCase().includes(q) ||
      m.status.toLowerCase().includes(q)
    );
  }

  // ── Tabs ──────────────────────────────────────────────────
  switchTab(tab: string): void {
    this.activeTab = tab;
    if (tab === 'matches' && this.matches.length === 0) this.loadMatches();
  }

  // ── Create Team ───────────────────────────────────────────
  createTeam(): void {
    this.errorMsg = '';
    if (this.createTeamForm.invalid) {
      this.createTeamForm.markAllAsTouched();
      return;
    }
    this.teamService.addTeam(this.createTeamForm.value).subscribe({
      next: (created) => {
        this.successMsg = `Team "${created.name}" created!`;
        this.showCreateTeamModal = false;
        this.createTeamForm.reset({ sport: 'Football', level: 'BEGINNER' });
        this.loadTeams();
        setTimeout(() => this.successMsg = '', 4000);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
    
  }

  // ── Edit Team ─────────────────────────────────────────────
  openEditTeamModal(team: Team): void {
    this.editTeamForm.patchValue(team);
    this.showEditTeamModal = true;
  }

  updateTeam(): void {
    this.errorMsg = '';
    if (this.editTeamForm.invalid) {
      this.editTeamForm.markAllAsTouched();
      return;
    }
    this.teamService.updateTeam(this.editTeamForm.value, this.currentUserEmail).subscribe({
      next: () => {
        this.successMsg = 'Team updated successfully!';
        this.showEditTeamModal = false;
        this.loadTeams();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
  }

  // ── Delete Team ───────────────────────────────────────────
  deleteTeam(idTeam: number): void {
    if (!confirm('Delete this team permanently?')) return;
    this.teamService.deleteTeam(idTeam, this.currentUserEmail).subscribe({
      next: () => {
        this.successMsg = 'Team deleted.';
        this.loadTeams();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
  }

  // ── Join Team ─────────────────────────────────────────────
  joinTeam(idTeam: number): void {
    const email = localStorage.getItem('EmailUserConnect');
    this.teamService.joinTeam(idTeam, email!).subscribe({
      next: () => {
        this.successMsg = 'You joined the team successfully!';
        this.loadTeams();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
  }

  // ── Create Match ──────────────────────────────────────────
  createMatch(): void {
    this.errorMsg = '';

    if (!this.teamAId || this.teamAId === 0) {
      this.errorMsg = 'Please select Team A.'; return;
    }
    if (!this.teamBId || this.teamBId === 0) {
      this.errorMsg = 'Please select Team B.'; return;
    }
    if (this.createMatchForm.invalid) {
      this.createMatchForm.markAllAsTouched(); return;
    }
    const matchDate = this.createMatchForm.value.matchDate;
    if (new Date(matchDate) <= new Date()) {
      this.errorMsg = 'Match date must be in the future.'; return;
    }

    this.matchService.addMatch(this.createMatchForm.value, this.teamAId, this.teamBId).subscribe({
      next: () => {
        this.successMsg = 'Match created successfully!';
        this.showCreateMatchModal = false;
        this.resetMatchForm();
        this.loadMatches();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
  }

  // ── Delete Match ──────────────────────────────────────────
  deleteMatch(idMatch: number): void {
    if (!confirm('Delete this match?')) return;
    this.matchService.deleteMatch(idMatch).subscribe({
      next: () => {
        this.matches         = this.matches.filter(m => m.idMatch !== idMatch);
        this.filteredMatches = this.filteredMatches.filter(m => m.idMatch !== idMatch);
        this.successMsg = 'Match deleted.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
    });
  }

  canDeleteMatch(match: MatchResponse): boolean {
    const email = this.currentUserEmail.toLowerCase();
    return (
      match.captainAEmail?.toLowerCase() === email ||
      match.captainBEmail?.toLowerCase() === email
    );
  }

  resetMatchForm(): void {
    this.createMatchForm.reset();
    this.teamAId         = 0;
    this.teamBId         = 0;
    this.captainAName    = '';
    this.captainBName    = '';
    this.availableTeamsB = this.otherTeams;
    this.errorMsg        = '';
  }

  onTeamAChange(): void {
    const team = this.myTeams.find(t => t.idTeam === Number(this.teamAId));
    this.captainAName    = team?.captainFullName || '';
    if (team) {
      this.availableTeamsB = this.teams.filter(t => t.idTeam !== Number(this.teamAId) && !this.isMyTeam(t) && t.sport === team.sport);
    } else {
      this.availableTeamsB = this.teams.filter(t => t.idTeam !== Number(this.teamAId) && !this.isMyTeam(t));
    }
    this.teamBId      = 0;
    this.captainBName = '';
  }

  onTeamBChange(): void {
    const team = this.teams.find(t => t.idTeam === Number(this.teamBId));
    this.captainBName = team?.captainFullName || '';
  }

  goToDetails(match: MatchResponse): void {
    this.router.navigate(['/client/detail-match', match.idMatch]);
  }

  goToTeamDetail(idTeam: number): void {
    this.router.navigate(['/client/detail-team', idTeam]);
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

  // team.component.ts

isPlayerInTeam(team: Team): boolean {
  return team.playerEmails?.some(
    e => e.toLowerCase() === this.currentUserEmail.toLowerCase()
  ) ?? false;
}

leaveTeam(idTeam: number): void {
  const email = localStorage.getItem('EmailUserConnect');
  this.teamService.leaveTeam(idTeam, email!).subscribe({
    next: () => {
      this.successMsg = 'You left the team.';
      this.loadTeams();
      setTimeout(() => this.successMsg = '', 3000);
    },
    error: (err) => { this.errorMsg = err.error?.message || `Error ${err.status}`; }
  });
}
}