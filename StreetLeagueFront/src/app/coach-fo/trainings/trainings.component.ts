import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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
  isLoading  = false;
  errorMsg   = '';
  successMsg = '';

  showCreateModal = false;
  showEditModal   = false;

  selectedTeamId: number = 0;
  currentUserEmail = '';

  createTrainingForm!: FormGroup;
  editTrainingForm!:   FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private trainingService: TrainingService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.currentUserEmail = localStorage.getItem('EmailUserConnect') || '';
    this.loadTeams();

    this.createTrainingForm = this.fb.group({
      title:             ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description:       ['', Validators.maxLength(500)],
      trainingDate:      ['', Validators.required],
      durationInMinutes: [60, [Validators.required, Validators.min(15), Validators.max(480)]],
      location:          ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      exercises:         ['']
    });

    this.editTrainingForm = this.fb.group({
      idTraining:        [0],
      title:             ['', [Validators.minLength(3), Validators.maxLength(100)]],
      description:       ['', Validators.maxLength(500)],
      trainingDate:      [''],
      durationInMinutes: [null, [Validators.min(15), Validators.max(480)]],
      location:          ['', [Validators.minLength(3), Validators.maxLength(100)]],
      exercises:         [''],
      status:            ['']
    });
  }

  get cf() { return this.createTrainingForm.controls; }
  get ef() { return this.editTrainingForm.controls; }

  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams = data;
        this.loadTrainings();
      },
      error: (err) => { console.error('Error loading teams', err); }
    });
  }

  loadTrainings(): void {
    this.isLoading = true;
    this.errorMsg  = '';
    this.trainingService.getTrainingsByCoach().subscribe({
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

  private formatDateForBackend(dateStr: string): string {
    if (!dateStr) return dateStr;
    if (dateStr.length === 16) {
      return dateStr + ':00';
    }
    return dateStr;
  }

  createTraining(): void {
    this.errorMsg = '';

    if (this.createTrainingForm.invalid) {
      this.createTrainingForm.markAllAsTouched();
      return;
    }
    if (!this.selectedTeamId || this.selectedTeamId === 0) {
      this.errorMsg = 'Please select a team.';
      return;
    }
    const date = this.createTrainingForm.value.trainingDate;
    if (new Date(date) <= new Date()) {
      this.errorMsg = 'Training date must be in the future.';
      return;
    }

    const formValue = this.createTrainingForm.value;
    // ✅ FIX: startTime بدل trainingDate
    const dto: TrainingRequest = {
      title:             formValue.title,
      description:       formValue.description,
      startTime:         this.formatDateForBackend(formValue.trainingDate),
      durationInMinutes: formValue.durationInMinutes,
      location:          formValue.location,
      exercises:         formValue.exercises
    };

    this.trainingService.addTraining(dto, this.selectedTeamId).subscribe({
      next: () => {
        this.successMsg = 'Training session created successfully!';
        this.showCreateModal = false;
        this.resetForm();
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        console.error('Backend error:', err.error);
        this.errorMsg = err.error?.error || err.error?.message || err.error || `Error ${err.status}`;
      }
    });
  }

  openEditModal(training: TrainingResponse): void {
    this.editTrainingForm.patchValue({
      idTraining:        training.idTraining,
      title:             training.title,
      description:       training.description,
      trainingDate:      training.trainingDate ? training.trainingDate.substring(0, 16) : '',
      durationInMinutes: training.durationInMinutes,
      location:          training.location,
      exercises:         training.exercises,
      status:            training.status
    });
    this.showEditModal = true;
  }

  updateTraining(): void {
    this.errorMsg = '';

    if (this.editTrainingForm.invalid) {
      this.editTrainingForm.markAllAsTouched();
      return;
    }
    const date = this.editTrainingForm.value.trainingDate;
    if (date && new Date(date) <= new Date()) {
      this.errorMsg = 'Training date must be in the future.';
      return;
    }

    const formValue = this.editTrainingForm.value;
    // ✅ FIX: id بدل idTraining، startTime بدل trainingDate
    const dto: TrainingUpdateRequest = {
      id:                formValue.idTraining,
      title:             formValue.title,
      description:       formValue.description,
      startTime:         formValue.trainingDate
                           ? this.formatDateForBackend(formValue.trainingDate)
                           : undefined,
      durationInMinutes: formValue.durationInMinutes,
      location:          formValue.location,
      exercises:         formValue.exercises,
      status:            formValue.status
    };

    this.trainingService.updateTraining(dto).subscribe({
      next: () => {
        this.successMsg = 'Training updated successfully!';
        this.showEditModal = false;
        this.loadTrainings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        console.error('Backend error:', err.error);
        this.errorMsg = err.error?.error || err.error?.message || err.error || `Error ${err.status}`;
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
      error: (err) => {
        console.error('Backend error:', err.error);
        this.errorMsg = err.error?.error || err.error?.message || err.error || `Error deleting (${err.status})`;
      }
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
      case 'SCHEDULED': return 'badge-blue';
      default:          return 'badge-gray';
    }
  }

  getProgress(current: number, max: number = 25): number {
    return Math.min(Math.round((current / max) * 100), 100);
  }

  resetForm(): void {
    this.createTrainingForm.reset({ durationInMinutes: 60 });
    this.selectedTeamId = 0;
    this.errorMsg = '';
  }
}