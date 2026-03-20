import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TrainingService } from 'src/app/services/training.service';
import { TrainingRequest, TrainingResponse, TrainingUpdateRequest } from 'src/app/models/training.model';
import { TeamService } from 'src/app/services/team.service';   
import { Team } from 'src/app/models/team.model'; 

@Component({
  selector: 'app-trainings',
  templateUrl: './trainings.component.html',
  styleUrls: ['./trainings.component.css']
})
export class TrainingsComponent implements OnInit {

  trainings: TrainingResponse[] = [];
    teams: Team[] = []; 
  isLoading = false;
  errorMsg = '';
  successMsg = '';

  // ── Modals ────────────────────────────────────────────────
  showCreateModal = false;
  showEditModal   = false;   // ← Ajouter

  // ── Create Form ───────────────────────────────────────────
  newTraining: TrainingRequest = {
    title: '', description: '', trainingDate: '',
    durationInMinutes: 60, location: '', exercises: ''
  };
  selectedTeamId: number = 0;

  // ── Edit Form ─────────────────────────────────────────────
  editTraining: TrainingUpdateRequest = { idTraining: 0 };  // ← Ajouter

  constructor(
    private router: Router,
    private trainingService: TrainingService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.loadTrainings();
      this.loadTeams(); 
  }
    loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => { this.teams = data; },
      error: (err)  => { console.error('Error loading teams', err); }
    });
  }

  loadTrainings(): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.trainingService.getAllTrainings().subscribe({
      next: (data) => { this.trainings = data; this.isLoading = false; },
      error: (err)  => { this.errorMsg = `Error loading trainings (${err.status})`; this.isLoading = false; }
    });
  }

  createTraining(): void {
    if (!this.newTraining.title || !this.newTraining.trainingDate) {
      this.errorMsg = 'Title and date are required.';
      return;
    }
    this.trainingService.addTraining(this.newTraining, this.selectedTeamId).subscribe({
      next: () => {
        this.successMsg = 'Training session created successfully!';
        this.showCreateModal = false;
        this.resetForm();
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || `Error ${err.status}`;
        console.error(err);
      }
    });
  }

  // ── Open Edit Modal ───────────────────────────────────────
  openEditModal(training: TrainingResponse): void {
    this.editTraining = {
      idTraining:        training.idTraining,
      title:             training.title,
      description:       training.description,
      trainingDate:      training.trainingDate,
      durationInMinutes: training.durationInMinutes,
      location:          training.location,
      exercises:         training.exercises,
      status:            training.status
    };
    this.showEditModal = true;
  }

  // ── Save Edit ─────────────────────────────────────────────
  updateTraining(): void {
    this.trainingService.updateTraining(this.editTraining).subscribe({
      next: () => {
        this.successMsg = 'Training updated successfully!';
        this.showEditModal = false;
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || `Error ${err.status}`;
        console.error(err);
      }
    });
  }

  deleteTraining(id: number): void {
    if (!confirm('Delete this training session?')) return;
    this.trainingService.deleteTraining(id).subscribe({
      next: () => {
        this.trainings = this.trainings.filter(t => t.idTraining !== id);
        this.successMsg = 'Training deleted.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => { this.errorMsg = `Error deleting (${err.status})`; }
    });
  }

  goToDetail(id: number): void {
    this.router.navigate(['/coach/detail-training', id]);
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

  resetForm(): void {
    this.newTraining = {
      title: '', description: '', trainingDate: '',
      durationInMinutes: 60, location: '', exercises: ''
    };
    this.selectedTeamId = 0;
    this.errorMsg = '';
  }
}