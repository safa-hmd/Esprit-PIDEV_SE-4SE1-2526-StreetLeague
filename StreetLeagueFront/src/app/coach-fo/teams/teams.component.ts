import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../services/team.service';
import { MatchService } from '../../services/match.service';
import { Team } from '../../models/team.model';
import { MatchResponse } from '../../models/match.model';

@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.css']
})
export class TeamsComponent implements OnInit {

  teams: Team[] = [];
  matches: MatchResponse[] = [];

  errorMsg = '';
  successMsg = '';
  activeTab = 'my-teams';
  isLoadingMatches = false;

 constructor(
  private router: Router,
  private route: ActivatedRoute,
  private teamService: TeamService,
  private matchService: MatchService
) {}

ngOnInit(): void {
  this.loadTeams();
  this.loadMatches();

  // ← Lire le tab depuis l'URL
  this.route.queryParams.subscribe(params => {
    if (params['tab']) {
      this.activeTab = params['tab'];
    }
  });
}

  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => { this.teams = data; },
      error: (err)  => { this.errorMsg = `Error ${err.status}`; }
    });
  }

  loadMatches(): void {
    this.isLoadingMatches = true;
    this.errorMsg = '';
    this.matchService.getAllMatchs().subscribe({
      next: (data) => { this.matches = data; this.isLoadingMatches = false; },
      error: (err)  => { this.errorMsg = `Error ${err.status}`; this.isLoadingMatches = false; }
    });
  }

  switchTab(tab: string): void {
    this.activeTab = tab;
    if (tab === 'matches' && this.matches.length === 0) this.loadMatches();
  }



goToTeamDetail(idTeam: number): void {
  this.router.navigate(['/coach/detail-team', idTeam]);   
}

goToDetails(match: MatchResponse): void {
  this.router.navigate(['/coach/detail-match', match.idMatch]);
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
}