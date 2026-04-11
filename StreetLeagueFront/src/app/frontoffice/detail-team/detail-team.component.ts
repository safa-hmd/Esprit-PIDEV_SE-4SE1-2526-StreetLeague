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
    this.router.navigate(['/client/team']);
  }

  goToTeamDetail(idTeam: number): void {
    this.router.navigate(['/client/detail-team', idTeam]);
  }

  // --- IA & Matchmaking --- //
  
  get isCaptain(): boolean {
    if (!this.team) return false;
    const currentEmail = localStorage.getItem('EmailUserConnect');
    // Vérification de sécurité: seul le capitaine voit le bouton
    return this.team.captainEmail === currentEmail;
  }

  suggestedOpponents: MatchCandidateResponse[] = [];
  isLoadingOpponents = false;
  opponentErrorMsg = '';
  isChallenging = false;
  challengeSuccessMsg = '';

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

  challengeTeam(opponentTeamId: number): void {
    if (!this.team?.idTeam) return;
    this.isChallenging = true;
    this.opponentErrorMsg = '';
    this.challengeSuccessMsg = '';

    const newMatch = {
      matchDate: new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000).toISOString(),
      location: 'Match Programmé par IA',
      status: 'PLANNED',
      scoreTeamA: null,
      scoreTeamB: null
    };

    this.matchService.addMatch(newMatch as any, this.team.idTeam, opponentTeamId).subscribe({
      next: (res) => {
        this.isChallenging = false;
        this.challengeSuccessMsg = 'Match Programmé avec Succès ! Le statut est maintenant "PLANNED".';
        // Retire l'adversaire de la liste
        this.suggestedOpponents = this.suggestedOpponents.filter(o => o.teamId !== opponentTeamId);
      },
      error: (err) => {
        this.isChallenging = false;
        this.opponentErrorMsg = 'Erreur lors de la programmation du match.';
      }
    });
  }
}