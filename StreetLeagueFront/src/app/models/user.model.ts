export interface UserProfile {
  idUser: number;
  fullName: string;
  email: string;
  role: 'PLAYER' | 'COACH' | 'ADMIN' | 'SPONSOR' | 'DELIVERY';
  teamCount: number;
  matchCount: number;
  trainingCount: number;
}

export interface UpdateProfileRequest {
  fullName: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}