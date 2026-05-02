export type TrainingStatus = 'PLANNED' | 'COMPLETED' | 'CANCELLED';

export interface Training {
  idTraining?: number;
  title?: string;
  description?: string;
  trainingDate?: string;
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
  performanceReport?: string;
  status?: TrainingStatus;
  team?: { id: number; name: string; };
  participants?: any[];
}

export interface TrainingRequest {
  title: string;
  description?: string;
  trainingDate: string;
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
}

export interface TrainingUpdateRequest {
  idTraining: number;
  title?: string;
  description?: string;
  trainingDate?: string;
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
  status?: TrainingStatus;
}

export interface TrainingResponse {
  idTraining: number;
  title: string;
  description: string;
  trainingDate: string;
  durationInMinutes: number;
  location: string;
  exercises: string;
  status: TrainingStatus;
  teamName: string;
  participantCount: number;
  participantEmails: string[];
  coachFullName?: string;
  performanceReport?: string;
}