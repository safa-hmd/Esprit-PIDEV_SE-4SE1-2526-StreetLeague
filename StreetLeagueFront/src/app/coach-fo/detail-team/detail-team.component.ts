import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Team } from 'src/app/models/team.model';
import { TeamService } from 'src/app/services/team.service';
import { MatchmakingService, MatchCandidateResponse } from 'src/app/services/matchmaking.service';
import { MatchService } from 'src/app/services/match.service';

@Component({
  selector: 'app-detail-team',
  templateUrl: './detail-team.component.html',
  styleUrls: ['./detail-team.component.css']
})
export class DetailTeamComponent implements OnInit {

  team!: Team;
  isLoading = true;
  errorMsg  = '';

  captainAName = '';    
  captainBName = '';    

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService,
    private matchmakingService: MatchmakingService,
    private matchService: MatchService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.teamService.getTeamById(id).subscribe({
      next: (data) => { this.team = data; this.isLoading = false; },
      error: (err)  => { this.errorMsg = `Team not found (${err.status})`; this.isLoading = false; }
    });
  }

goBack(): void {
  this.router.navigate(['/coach/teamCoach']);  // ← 'teamCoach' pas 'team'
}

  goToTeamDetail(idTeam: number): void {
  this.router.navigate(['/coach/detail-team', idTeam]);
}

  isEditingStats = false;
  editVictories = 0;
  editDefeats = 0;
  editMatches = 0;

  toggleEditStats(): void {
    this.isEditingStats = !this.isEditingStats;
    if (this.isEditingStats && this.team) {
      this.editVictories = this.team.victories || 0;
      this.editDefeats = this.team.defeats || 0;
      this.editMatches = this.team.matches || 0;
    }
  }

  saveStats(): void {
    if (!this.team?.idTeam) return;
    this.teamService.updateTeamStats(this.team.idTeam, this.editVictories, this.editDefeats, this.editMatches).subscribe({
      next: (updatedTeam) => {
        this.team = updatedTeam;
        this.isEditingStats = false;
      },
      error: (err) => {
        this.errorMsg = 'Failed to update stats.';
      }
    });
  }

  suggestedOpponents: MatchCandidateResponse[] = [];
  isLoadingOpponents = false;
  opponentErrorMsg = '';

  findSmartOpponents(): void {
    if (!this.team?.idTeam) return;
    this.isLoadingOpponents = true;
    this.opponentErrorMsg = '';
    this.matchmakingService.suggestOpponents(this.team.idTeam).subscribe({
      next: (candidates) => {
        this.suggestedOpponents = candidates;
        this.isLoadingOpponents = false;
      },
      error: (err) => {
        this.opponentErrorMsg = 'Failed to generate suggestions. Please ensure the backend algorithm is running.';
        this.isLoadingOpponents = false;
      }
    });
  }

  isChallenging = false;
  challengeSuccessMsg = '';

 challengeTeam(opponentTeamId: number): void {
  if (!this.team?.idTeam) return;
  this.isChallenging = true;
  this.opponentErrorMsg = '';
  this.challengeSuccessMsg = '';

  // Récupérer l'email du capitaine connecté
  const captainEmail = this.team.captainEmail;
  if (!captainEmail) {
    this.opponentErrorMsg = 'Captain email not found.';
    this.isChallenging = false;
    return;
  }

  const newMatch = {
    matchDate: new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000)
                   .toISOString().replace('Z', ''),  // LocalDateTime format
    location: 'Match Programmé par IA',
    status: 'PLANNED'
  };

  // Appel vers le nouvel endpoint dédié
  this.matchService.addMatchByEmail(
    newMatch as any,
    this.team.idTeam,
    opponentTeamId,
    captainEmail
  ).subscribe({
    next: () => {
      this.isChallenging = false;
      this.challengeSuccessMsg = 'Match programmé avec succès !';
      this.suggestedOpponents = this.suggestedOpponents
        .filter(o => o.teamId !== opponentTeamId);
    },
    error: (err) => {
      this.isChallenging = false;
      this.opponentErrorMsg = err.error?.message ||
        'Erreur lors de la programmation du match.';
    }
  });
}
}