import { Component, OnInit } from '@angular/core';
import { TeamService } from '../../services/team.service';
import { MatchService } from '../../services/match.service';
import { TrainingService } from '../../services/training.service';
import { Team } from '../../models/team.model';
import { MatchResponse } from '../../models/match.model';
import { TrainingResponse } from '../../models/training.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  myTeams: Team[] = [];
  upcomingMatches: MatchResponse[] = [];
  recentTrainings: TrainingResponse[] = [];
  stats = {
    activePlayers: 0,
    teams: 0,
    tournaments: 0,
    eventsThisWeek: 0
  };

  constructor(
    private teamService: TeamService,
    private matchService: MatchService,
    private trainingService: TrainingService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    // Load coach's teams
    const captainId = this.getCurrentUserId();
    if (captainId) {
      this.teamService.getMyTeams(captainId).subscribe({
        next: (teams) => {
          this.myTeams = teams;
          this.stats.teams = teams.length;
        },
        error: (err) => console.error('Error loading teams:', err)
      });
    }

    // Load all trainings (or filter by coach's teams)
    this.trainingService.getAllTrainings().subscribe({
      next: (trainings) => {
        this.recentTrainings = trainings.slice(0, 3); // Show last 3
        this.stats.eventsThisWeek = trainings.length;
      },
      error: (err) => console.error('Error loading trainings:', err)
    });

    // For matches, we might need a method to get matches for coach's teams
    // For now, keep static or add a method if available
  }

  private getCurrentUserId(): number | null {
    // Assuming user ID is stored in localStorage
    const userId = localStorage.getItem('UserId');
    return userId ? parseInt(userId) : null;
  }

  navigateToTeam(teamId: number): void {
    // Will be handled by routerLink
  }

  navigateToTraining(trainingId: number): void {
    // Will be handled by routerLink
  }
}
