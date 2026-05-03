export type TrainingStatus = 'PLANNED' | 'COMPLETED' | 'CANCELLED' | 'SCHEDULED';

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
  startTime: string;       // ← كان trainingDate، الـ backend يتوقع startTime
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
}

export interface TrainingUpdateRequest {
  id: number;              // ← كان idTraining، الـ backend يتوقع id
  title?: string;
  description?: string;
  startTime?: string;      // ← كان trainingDate
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
  status?: string;
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