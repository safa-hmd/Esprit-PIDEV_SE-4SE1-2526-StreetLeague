import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BracketService } from '../../services/bracket.service';
import { BracketResponseDto, MatchSlotDto } from '../../models/bracket.model';

@Component({
  selector: 'app-bracket',
  templateUrl: './bracket.component.html',
  styleUrls: ['./bracket.component.css']
})
export class BracketComponent implements OnInit {

  tournamentId!: number;
  bracket: BracketResponseDto | null = null;
  loading = true;
  viewMode: 'tree' | 'table' = 'tree';

  constructor(
    private route: ActivatedRoute,
    private bracketService: BracketService
  ) {}

  ngOnInit(): void {
    this.tournamentId = Number(this.route.snapshot.paramMap.get('id'));
    this.bracketService.getBracket(this.tournamentId).subscribe({
      next: (b) => { this.bracket = b; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  get rounds(): number[] {
    if (!this.bracket) return [];
    return Object.keys(this.bracket.rounds).map(Number).sort((a, b) => a - b);
  }

  getMatches(round: number): MatchSlotDto[] {
    return this.bracket?.rounds[round] ?? [];
  }

  roundLabel(round: number): string {
    if (!this.bracket) return `Round ${round}`;
    const total = this.bracket.totalRounds;
    if (round === total) return '🏆 Finale';
    if (round === total - 1) return 'Demi-finales';
    if (round === total - 2) return 'Quarts de finale';
    return `Round ${round}`;
  }
}
