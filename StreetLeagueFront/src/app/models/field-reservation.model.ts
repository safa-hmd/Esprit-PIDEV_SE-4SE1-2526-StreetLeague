export enum SportType {
  FOOTBALL = 'FOOTBALL',
  BASKETBALL = 'BASKETBALL',
  TENNIS = 'TENNIS',
  PADEL = 'PADEL',
  VOLLEYBALL = 'VOLLEYBALL',
  OTHER = 'OTHER'
}

export enum ReservationStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED'
}

export interface Field {
  id?: number;
  name: string;
  description?: string;
  sportType: SportType;
  location: string;
  imageUrl?: string;
  pricePerHour: number;
  capacity: number;
  available: boolean;
}

export interface FieldReservation {
  id?: number;
  fieldId: number;
  fieldName?: string;
  fieldLocation?: string;
  playerId: number;
  playerUsername?: string;
  startTime: string;
  endTime: string;
  notes?: string;
  status?: ReservationStatus;
  totalPrice?: number;
  adminNote?: string;
  createdAt?: string;
}