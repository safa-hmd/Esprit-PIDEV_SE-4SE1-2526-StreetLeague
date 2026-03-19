import { Component, OnInit } from '@angular/core';
import { TrainingService } from 'src/app/services/training.service';
import { TrainingResponse } from 'src/app/models/training.model';

@Component({
  selector: 'app-training',
  templateUrl: './training.component.html',
  styleUrls: ['./training.component.css']
})
export class TrainingComponent implements OnInit {

  trainings: TrainingResponse[] = [];
  isLoading = false;
  errorMsg = '';
  successMsg = '';

  constructor(private trainingService: TrainingService) {}

  ngOnInit(): void {
    this.loadTrainings();
  }

  // ── Load All Trainings ────────────────────────────────────
  loadTrainings(): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.trainingService.getAllTrainings().subscribe({
      next: (data) => {
        this.trainings = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMsg = `Error loading trainings (${err.status})`;
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // ── Join Training ─────────────────────────────────────────
  joinTraining(id: number): void {
    this.trainingService.joinTraining(id).subscribe({
      next: () => {
        this.successMsg = 'Successfully joined the session!';
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = `Cannot join this session (${err.status})`;
        console.error(err);
      }
    });
  }

  // ── Leave Training ────────────────────────────────────────
  leaveTraining(id: number): void {
    this.trainingService.leaveTraining(id).subscribe({
      next: () => {
        this.successMsg = 'You have left the session.';
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = `Cannot leave this session (${err.status})`;
        console.error(err);
      }
    });
  }

  // ── Status badge CSS ──────────────────────────────────────
  getStatusClass(status: string): string {
    switch (status) {
      case 'PLANNED':   return 'badge-blue';
      case 'COMPLETED': return 'badge-green';
      case 'CANCELLED': return 'badge-red';
      default:          return 'badge-gray';
    }
  }

  // ── Progress bar % ────────────────────────────────────────
  getProgress(current: number, max: number = 25): number {
    return Math.min(Math.round((current / max) * 100), 100);
  }
}