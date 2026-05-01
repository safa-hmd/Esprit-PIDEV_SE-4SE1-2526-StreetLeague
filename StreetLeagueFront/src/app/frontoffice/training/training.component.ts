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
  isLoading        = false;
  errorMsg         = '';
  successMsg       = '';
  currentUserEmail = '';
  upcomingWithCoach: TrainingResponse[] = [];

  constructor(private trainingService: TrainingService) {}

  ngOnInit(): void {
    this.currentUserEmail = localStorage.getItem('EmailUserConnect') || '';
    this.loadTrainings();
  this.loadUpcomingDetailed();

}
isLoadingUpcoming = true;
// Nouvelle méthode
loadUpcomingDetailed(): void {
  this.isLoadingUpcoming = true;
  this.trainingService.getUpcomingTrainingsWithDetails().subscribe({
    next: (data) => {
      this.upcomingWithCoach = data;
      this.isLoadingUpcoming = false;
    },
    error: (err) => {
      console.error(err);
      this.isLoadingUpcoming = false;
    }
  });
}



  loadTrainings(): void {
    this.isLoading = true;
    this.errorMsg  = '';
    this.trainingService.getMyTeamTrainings().subscribe({
      next: (data) => {
        this.trainings = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMsg  = `Error loading trainings (${err.status})`;
        this.isLoading = false;
      }
    });
  }

  hasJoined(training: TrainingResponse): boolean {
    return training.participantEmails?.some(
      e => e.toLowerCase() === this.currentUserEmail.toLowerCase()
    ) ?? false;
  }

  joinTraining(id: number): void {
    this.errorMsg   = '';
    this.successMsg = '';
    this.trainingService.joinTraining(id).subscribe({
      next: () => {
        this.successMsg = 'Successfully joined the session!';
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || `Cannot join this session (${err.status})`;
      }
    });
  }

  leaveTraining(id: number): void {
    this.errorMsg   = '';
    this.successMsg = '';
    this.trainingService.leaveTraining(id).subscribe({
      next: () => {
        this.successMsg = 'You have left the session.';
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || `Cannot leave this session (${err.status})`;
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PLANNED':   return 'badge-blue';
      case 'COMPLETED': return 'badge-green';
      case 'CANCELLED': return 'badge-red';
      default:          return 'badge-gray';
    }
  }

  getProgress(current: number, max: number = 25): number {
    return Math.min(Math.round((current / max) * 100), 100);
  }
}