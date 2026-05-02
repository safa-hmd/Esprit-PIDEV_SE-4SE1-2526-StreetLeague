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
export interface FieldScheduleEntry {
  type: 'RESERVATION' | 'TOURNAMENT';
  date: string;                // "2026-04-05"
  startTime?: string;          // "10:00:00" — null si tournoi
  endTime?: string;            // "12:00:00" — null si tournoi
  sport: string;               // "FOOTBALL"
  label: string;               // nom du joueur OU nom du tournoi
  status: string;              // "APPROVED" | "PENDING" | "UPCOMING" | ...
  eventId: number;             // id réservation ou tournoi
  tournamentType?: string;     // "INDIVIDUAL" | "TEAM" — null si réservation
}



// ── Enums Paiement ────────────────────────────────────────────────────────

export type PaymentStatus = 'PENDING' | 'PAID' | 'REFUNDED' | 'FAILED';
export type PaymentMethod = 'CARD' | 'CASH' | 'ONLINE';

// ── Payment ───────────────────────────────────────────────────────────────

export interface Payment {
  id: number;

  // Paiement
  amount: number;
  method?: PaymentMethod;
  status: PaymentStatus;
  transactionRef?: string;

  // Dates
  createdAt: string;
  paidAt?: string;
  refundedAt?: string;

  // Réservation
  reservationId: number;
  slotStart: string;
  slotEnd: string;

  // Terrain
  fieldId: number;
  fieldName: string;
  fieldLocation: string;

  // Joueur
  playerId: number;
  playerName: string;
}