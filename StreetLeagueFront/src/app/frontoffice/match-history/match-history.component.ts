// src/app/frontoffice/match-history/match-history.component.ts
import { Component, OnInit } from '@angular/core';
import { MatchHistoryService } from '../../services/match-history.service';
import {
  MatchHistoryDto, MatchResponse, MatchStatus
} from '../../models/match-history.model';

@Component({
  selector: 'app-match-history',
  templateUrl: './match-history.component.html',
  styleUrls: ['./match-history.component.css']
})
export class MatchHistoryComponent implements OnInit {

  // ── Données ────────────────────────────────────────────────────────────
  enrichedMatches: MatchHistoryDto[] = [];
  searchResults:   MatchResponse[]   = [];
  loading = false;

  // ── Filtres JPQL ──────────────────────────────────────────────────────
  selectedStatus: MatchStatus | '' = '';
  fromDate = '';
  toDate   = '';

  // ── Filtres Keywords (multi-table: TeamA | TeamB | Location) ─────────
  keyword = '';
  allStatuses: MatchStatus[] = ['PENDING','ACCEPTED','REJECTED','CANCELLED','FINISHED'];
  selectedStatuses: Set<MatchStatus> = new Set(this.allStatuses);
  searchHasRun = false;

  // ── UI ────────────────────────────────────────────────────────────────
  activeTab: 'history' | 'search' = 'history';

  constructor(private matchHistorySvc: MatchHistoryService) {}

  ngOnInit(): void {
    this.loadEnrichedHistory();
  }

  // ─────────────────────────────────────────────────────────────────────
  loadEnrichedHistory(): void {
    this.loading = true;
    this.matchHistorySvc.getEnrichedHistory(
      this.selectedStatus || undefined,
      this.fromDate  ? new Date(this.fromDate).toISOString()  : undefined,
      this.toDate    ? new Date(this.toDate).toISOString()    : undefined
    ).subscribe({
      next:  data  => { this.enrichedMatches = data; this.loading = false; },
      error: _err  => { this.loading = false; }
    });
  }

  searchMatches(): void {
    this.loading = true;
    this.searchHasRun = true;
    this.matchHistorySvc.searchMatches(
      this.keyword,
      Array.from(this.selectedStatuses)
    ).subscribe({
      next:  data  => { this.searchResults = data; this.loading = false; },
      error: _err  => { this.loading = false; }
    });
  }

  toggleStatus(s: MatchStatus): void {
    this.selectedStatuses.has(s)
      ? this.selectedStatuses.delete(s)
      : this.selectedStatuses.add(s);
  }

  resetFilters(): void {
    this.selectedStatus  = '';
    this.fromDate        = '';
    this.toDate          = '';
    this.loadEnrichedHistory();
  }

  getStatusClass(status: MatchStatus): string {
    const map: Record<MatchStatus, string> = {
      FINISHED:  'badge-success',
      ACCEPTED:  'badge-info',
      PENDING:   'badge-warning',
      CANCELLED: 'badge-danger',
      REJECTED:  'badge-secondary'
    };
    return map[status] ?? 'badge-secondary';
  }

  getScoreLabel(m: MatchHistoryDto | MatchResponse): string {
    if (m.status !== 'FINISHED') return '— vs —';
    return `${m.scoreTeamA ?? 0} - ${m.scoreTeamB ?? 0}`;
  }

  getWinner(m: MatchHistoryDto): string {
    if (m.status !== 'FINISHED' || m.scoreTeamA == null) return '';
    if (m.scoreTeamA > m.scoreTeamB!) return m.teamAName;
    if (m.scoreTeamB! > m.scoreTeamA)  return m.teamBName;
    return 'Draw';
  }
}