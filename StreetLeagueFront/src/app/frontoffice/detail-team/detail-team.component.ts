import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Team } from 'src/app/models/team.model';
import { TeamService } from 'src/app/services/team.service';
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


}