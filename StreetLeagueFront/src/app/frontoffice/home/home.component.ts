import { Component, OnInit } from '@angular/core';
import { TeamService } from 'src/app/services/team.service';
import { Team } from 'src/app/models/team.model';
import { LeaderboardDto } from 'src/app/models/leaderboard.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  topTeams: LeaderboardDto[] = [];   // ← plus Team[]
  selectedSport: string = 'Football';
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
      next: (data: LeaderboardDto[]) => {
        this.topTeams = data;
        this.isLoadingLeaderboard = false;
      },
      error: () => {
        this.leaderboardError = 'Could not load leaderboard.';
        this.isLoadingLeaderboard = false;
      }
    });
  }

  switchSport(sport: string): void {
    this.selectedSport = sport;
    this.loadLeaderboard();
  }

  calculateScore(team: LeaderboardDto): number {
    return team.points; // ← déjà calculé côté backend, pas besoin de recalculer
  }

  readonly avatarColors = [
  { bg: '#EEEDFE', color: '#3C3489' },
  { bg: '#E1F5EE', color: '#085041' },
  { bg: '#FAECE7', color: '#712B13' },
  { bg: '#E6F1FB', color: '#0C447C' },
  { bg: '#EAF3DE', color: '#3B6D11' },
];

getAvatarStyle(i: number): object {
  const av = this.avatarColors[i % this.avatarColors.length];
  return { background: av.bg, color: av.color };
}

getScoreClass(i: number): string {
  if (i === 0) return 'score-gold';
  if (i === 1) return 'score-silver';
  if (i === 2) return 'score-bronze';
  return 'score-default';
}
}