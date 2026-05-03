import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Team } from 'src/app/models/team.model';
import { TeamService } from 'src/app/services/team.service';

import { TrainingService } from 'src/app/services/training.service';
import { TrainingResponse } from 'src/app/models/training.model';

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

  completedTrainings: TrainingResponse[] = [];
  isLoadingTrainings = false;
  trainingErrorMsg = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService,
    private trainingService: TrainingService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.teamService.getTeamById(id).subscribe({
      next: (data) => { 
        this.team = data; 
        this.isLoading = false; 
        this.loadCompletedTrainings(id);
      },
      error: (err)  => { this.errorMsg = `Team not found (${err.status})`; this.isLoading = false; }
    });
  }

  loadCompletedTrainings(teamId: number): void {
    this.isLoadingTrainings = true;
    this.trainingService.getCompletedTrainingsWithDetails(teamId).subscribe({
      next: (trainings) => {
        this.completedTrainings = trainings;
        this.isLoadingTrainings = false;
      },
      error: (err) => {
        this.trainingErrorMsg = 'Failed to load advanced post trainings history.';
        this.isLoadingTrainings = false;
      }
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


  isLoadingOpponents = false;
  opponentErrorMsg = '';

  formatDate(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  formatMonthYear(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short' });
  }
}