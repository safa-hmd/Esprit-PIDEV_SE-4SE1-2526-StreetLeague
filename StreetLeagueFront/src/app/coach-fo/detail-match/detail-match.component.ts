import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatchResponse } from 'src/app/models/match.model';
import { MatchService } from 'src/app/services/match.service';
import { TrainingService } from 'src/app/services/training.service';  // ← ajouter

@Component({
  selector: 'app-detail-match',
  templateUrl: './detail-match.component.html',
  styleUrls: ['./detail-match.component.css']
})
export class DetailMatchComponent implements OnInit {

  match!: MatchResponse;
  isLoading = true;
  errorMsg = '';
  generateMsg = '';      // ← ajouter
  generateError = '';    // ← ajouter
  isGenerating = false;  // ← ajouter

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService,
    private trainingService: TrainingService  // ← ajouter
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.matchService.getMatchById(+id).subscribe({
        next: (data) => { this.match = data; this.isLoading = false; },
        error: () => { this.errorMsg = 'Match not found.'; this.isLoading = false; }
      });
    } else {
      this.router.navigate(['/coach/teamCoach']);
    }
  }

  // ← ajouter cette méthode
  onGenerateTraining(): void {
    this.generateMsg   = '';
    this.generateError = '';
    this.isGenerating  = true;

    this.trainingService.generateFromMatch(this.match.idMatch).subscribe({
      next: (res) => {
        this.generateMsg  = `✅ Training session "${res.title}" created successfully!`;
        this.isGenerating = false;
      },
      error: (err) => {
        this.generateError = err.error?.message || 'Failed to generate training.';
        this.isGenerating  = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/coach/teamCoach'], { queryParams: { tab: 'matches' } });
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

  formatDate(dateStr: string | undefined): string {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString('en-US', { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
  }
}