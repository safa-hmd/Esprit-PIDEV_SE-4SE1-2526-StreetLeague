import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BracketService } from '../../services/bracket.service';
import {
  BracketResponseDto, BracketType,
  MatchSlotDto, SubmitResultDto
} from '../../models/bracket.model';
@Component({
  selector: 'app-bracket',
  templateUrl: './bracket.component.html',
  styleUrls: ['./bracket.component.css']
})
export class BracketComponent implements OnInit {

  tournamentId!: number;
  bracket: BracketResponseDto | null = null;
  loading = false;
  error = '';
  success = '';

  // Toggle vue
  viewMode: 'tree' | 'table' = 'tree';

  // Modal résultat
  selectedMatch: MatchSlotDto | null = null;
  scoreA: number | null = null;
  scoreB: number | null = null;
  winnerId: number | null = null;

  // Génération
  selectedType: BracketType = 'SINGLE_ELIMINATION';

  constructor(
    private route: ActivatedRoute,
    private bracketService: BracketService
  ) {}

  ngOnInit(): void {
    this.tournamentId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadBracket();
  }

  loadBracket(): void {
    this.loading = true;
    this.bracketService.getBracket(this.tournamentId).subscribe({
      next: (b) => { this.bracket = b; this.loading = false; },
      error: () => { this.loading = false; } // pas encore généré → normal
    });
  }

  generate(): void {
    this.loading = true;
    this.error = '';
    this.bracketService.generateBracket(this.tournamentId, this.selectedType).subscribe({
      next: (b) => {
        this.bracket = b;
        this.loading = false;
        this.success = 'Bracket généré avec succès !';
        setTimeout(() => this.success = '', 3000);
      },
      error: (e) => {
        this.error = e.error?.message || 'Erreur lors de la génération.';
        this.loading = false;
      }
    });
  }

  get rounds(): number[] {
    if (!this.bracket) return [];
    return Object.keys(this.bracket.rounds).map(Number).sort((a, b) => a - b);
  }

  getMatches(round: number): MatchSlotDto[] {
    return this.bracket?.rounds[round] ?? [];
  }

  openResultModal(match: MatchSlotDto): void {
    this.selectedMatch = match;
    this.scoreA = null;
    this.scoreB = null;
    this.winnerId = null;
  }

  closeModal(): void {
    this.selectedMatch = null;
  }

  submitResult(): void {
    if (!this.selectedMatch || this.winnerId === null) return;

    const isTeam = this.bracket?.bracketType === 'SINGLE_ELIMINATION'
      ? (this.selectedMatch.participantA?.type === 'TEAM')
      : (this.selectedMatch.participantA?.type === 'TEAM');

    const dto: SubmitResultDto = {
      winnerId: this.winnerId,
      winnerIsTeam: isTeam,
      scoreA: this.scoreA ?? undefined,
      scoreB: this.scoreB ?? undefined
    };

    this.bracketService.submitResult(this.selectedMatch.id, dto).subscribe({
      next: () => {
        this.closeModal();
        this.loadBracket();
        this.success = 'Résultat enregistré !';
        setTimeout(() => this.success = '', 3000);
      },
      error: (e) => {
        this.error = e.error?.message || 'Erreur lors de la soumission.';
      }
    });
  }

  roundLabel(round: number): string {
    if (!this.bracket) return `Round ${round}`;
    const total = this.bracket.totalRounds;
    if (round === total) return '🏆 Finale';
    if (round === total - 1) return 'Demi-finales';
    if (round === total - 2) return 'Quarts de finale';
    return `Round ${round}`;
  }

  statusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'status-pending',
      IN_PROGRESS: 'status-progress',
      COMPLETED: 'status-done',
      BYE: 'status-bye'
    };
    return map[status] ?? '';
  }
}
