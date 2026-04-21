// src/app/models/training.model.ts

export type TrainingStatus = 'PLANNED' | 'COMPLETED' | 'CANCELLED';

// ── Main Model ───────────────────────────────────────────────
export interface Training {
  idTraining?: number;
  title?: string;
  description?: string;
  trainingDate?: string;       // ISO string ex: "2025-06-15T18:00:00"
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
  performanceReport?: string;
  status?: TrainingStatus;
  team?: {
    id: number;
    name: string;
  };
  participants?: any[];
}

// ── Request DTO ──────────────────────────────────────────────
export interface TrainingRequest {
  title: string;
  description?: string;
  trainingDate: string;
  durationInMinutes?: number;
  location?: string;
  exercises?: string;
}

// ── Update Request DTO ───────────────────────────────────────
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

// ── Response DTO ─────────────────────────────────────────────
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
}