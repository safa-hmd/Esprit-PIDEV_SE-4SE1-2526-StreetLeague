import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatchResponse } from '../../models/match.model';
import { MatchService } from '../../services/match.service';

@Component({
  selector: 'app-detail-match',
  templateUrl: './detail-match.component.html',
  styleUrls: ['./detail-match.component.css']
})
export class DetailMatchComponent implements OnInit {

  match!: MatchResponse;
  isLoading = true;
  errorMsg  = '';
  editMsg   = '';
  editError = '';

  showEditModal = false;
  editForm!: FormGroup;

  currentEmail = localStorage.getItem('EmailUserConnect') || '';

  constructor(
    private route:        ActivatedRoute,
    private router:       Router,
    private fb:           FormBuilder,
    private matchService: MatchService
  ) {}

  ngOnInit(): void {
    this.editForm = this.fb.group({
      idMatch:    [0],
      location:   ['', [Validators.minLength(3), Validators.maxLength(100)]],
      matchDate:  [''],
      status:     [''],
      scoreTeamA: [null],
      scoreTeamB: [null]
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.matchService.getMatchById(+id).subscribe({
        next: (data) => {
          this.match = data;
          this.isLoading = false;
          this.editForm.patchValue({
            idMatch:    data.idMatch,
            location:   data.location,
            matchDate:  data.matchDate,
            status:     data.status,
            scoreTeamA: data.scoreTeamA,
            scoreTeamB: data.scoreTeamB
          });
        },
        error: () => { this.errorMsg = 'Match not found.'; this.isLoading = false; }
      });
    } else {
      this.router.navigate(['/client/team']);
    }
  }

  // ── Vérifie si le user connecté est le créateur du match ──
  isCreator(): boolean {
    if (!this.match) return false;
    // captainAName est le créateur (TeamA captain)
    // On compare avec le nom stocké, ou mieux : on compare l'email
    const storedName = localStorage.getItem('userName') || '';
    return this.match.captainAName === storedName;
  }

  openEditModal(): void {
    this.editError = '';
    this.editMsg   = '';
    this.showEditModal = true;
  }

  updateMatch(): void {
    this.editError = '';
    const val = this.editForm.value;

    if (val.status === 'FINISHED' &&
       (val.scoreTeamA == null || val.scoreTeamB == null)) {
      this.editError = 'Scores are required when status is FINISHED.';
      return;
    }

    this.matchService.updateMatch(val).subscribe({
      next: (res) => {
        this.match         = res;
        this.showEditModal = false;
        this.editMsg       = 'Match updated successfully!';
        setTimeout(() => this.editMsg = '', 3000);
      },
      error: (err) => {
        this.editError = err.error?.message || `Error ${err.status}`;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/client/team'], { queryParams: { tab: 'matches' } });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING':   return 'badge-blue';
      case 'ACCEPTED':  return 'badge-green';
      case 'FINISHED':  return 'badge-gray';
      case 'CANCELLED': return 'badge-red';
      case 'REJECTED':  return 'badge-red';
      default:          return '';
    }
  }
}