import { Component, OnInit } from '@angular/core';
import { TrainingService } from 'src/app/services/training.service';
import { TrainingResponse } from 'src/app/models/training.model';

export interface Coach {
  nom: string;
  specialty: string;
  sessions: number;
  rating: number;
  statut: 'Active' | 'Pending';
}

@Component({
  selector: 'app-list-training',
  templateUrl: './list-training.component.html',
  styleUrls: ['./list-training.component.css']
})
export class ListTrainingComponent implements OnInit {

  // ── Trainings (API) ───────────────────────────────────────
  trainings: TrainingResponse[] = [];
  isLoading = false;
  errorMsg = '';
  successMsg = '';

  // ── Coaches (static — replace with API if available) ─────
  coaches: Coach[] = [];

  constructor(private trainingService: TrainingService) {}

  // ── Computed stats ────────────────────────────────────────
  get plannedCount(): number {
    return this.trainings.filter(t => t.status === 'PLANNED').length;
  }
  get completedCount(): number {
    return this.trainings.filter(t => t.status === 'COMPLETED').length;
  }
  get totalParticipants(): number {
    return this.trainings.reduce((sum, t) => sum + (t.participantCount || 0), 0);
  }
  get activeCoaches(): number {
    return this.coaches.filter(c => c.statut === 'Active').length;
  }

  ngOnInit(): void {
    this.loadTrainings();
  }

  // ── Load All Trainings ────────────────────────────────────
  loadTrainings(): void {
    this.isLoading = true;
    this.trainingService.getAllTrainings().subscribe({
      next: (data) => {
        this.trainings = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMsg = `Error loading trainings (${err.status})`;
        this.isLoading = false;
      }
    });
  }

  // ── Delete Training ───────────────────────────────────────
  deleteTraining(id: number): void {
    if (!confirm('Delete this training session?')) return;
    this.trainingService.deleteTraining(id).subscribe({
      next: () => {
        this.trainings = this.trainings.filter(t => t.idTraining !== id);
        this.successMsg = 'Training deleted.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = `Error deleting training (${err.status})`;
      }
    });
  }

  // ── Status badge CSS ──────────────────────────────────────
  getStatusClass(statut: string): string {
    switch (status) {
      case 'PLANNED':   return 'a-badge-blue';
      case 'COMPLETED': return 'a-badge-green';
      case 'CANCELLED': return 'a-badge-red';
      default:          return 'a-badge-gray';
    }
  }

  getCoachStatusClass(statut: string): string {
    return status === 'Active' ? 'a-badge-green' : 'a-badge-orange';
  }

  // ── Progress ──────────────────────────────────────────────
  getProgress(current: number, max: number = 25): number {
    return Math.min(Math.round((current / max) * 100), 100);
  }

  getProgressColor(current: number, max: number = 25): string {
    const pct = this.getProgress(current, max);
    if (pct >= 100) return 'var(--red)';
    if (pct >= 75)  return 'var(--orange)';
    return 'var(--teal)';
  }
}



