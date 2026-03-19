import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatchResponse } from 'src/app/models/match.model';
import { MatchService } from 'src/app/services/match.service';

@Component({
  selector: 'app-detail-match',
  templateUrl: './detail-match.component.html',
  styleUrls: ['./detail-match.component.css']
})
export class DetailMatchComponent implements OnInit {

  match!: MatchResponse;
  isLoading = true;
  errorMsg = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService
  ) {}

ngOnInit(): void {
  const id = this.route.snapshot.paramMap.get('id');
  if (id) {
    this.matchService.getMatchById(+id).subscribe({
      next: (data) => { this.match = data; this.isLoading = false; },
      error: () => { this.errorMsg = 'Match not found.'; this.isLoading = false; }
    });
  } else {
    this.router.navigate(['/coach/teamCoach']);  // ← corriger
  }
}

goBack(): void {
  this.router.navigate(['/coach/teamCoach']);   // ← corriger
}

  getStatusClass(status: string): string {
    switch (status) {
      case 'SCHEDULED': return 'badge-blue';
      case 'ONGOING':   return 'badge-green';
      case 'FINISHED':  return 'badge-gray';
      case 'CANCELLED': return 'badge-red';
      default:          return '';
    }
  }
}