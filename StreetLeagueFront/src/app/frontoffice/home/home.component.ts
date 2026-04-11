import { Component, OnInit } from '@angular/core';
import { TeamService } from 'src/app/services/team.service';
import { Team } from 'src/app/models/team.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  
  // Leaderboard data
  topTeams: Team[] = [];
  selectedSport: string = 'Football'; // Default sport
  isLoadingLeaderboard = false;
  leaderboardError = '';

  constructor(private teamService: TeamService) {}

  ngOnInit(): void {
    this.loadLeaderboard();
  }

  loadLeaderboard(): void {
    this.isLoadingLeaderboard = true;
    this.leaderboardError = '';
    
    this.teamService.getLeaderboard(this.selectedSport).subscribe({
      next: (data) => {
        this.topTeams = data;
        this.isLoadingLeaderboard = false;
      },
      error: (err) => {
        this.leaderboardError = 'Could not load leaderboard.';
        this.isLoadingLeaderboard = false;
      }
    });
  }

  switchSport(sport: string): void {
    this.selectedSport = sport;
    this.loadLeaderboard();
  }

  // Calculate score locally for display
  calculateScore(team: Team): number {
    const v = team.victories || 0;
    const d = team.defeats || 0;
    const m = team.matches || 0;
    return (v * 3) + m - d;
  }
}
