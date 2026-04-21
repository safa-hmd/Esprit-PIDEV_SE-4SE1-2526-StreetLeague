export interface Tournament {
  id: number;
  city: string;    // ← maps to 'city' column in DB
  name: string;
  startDate?: string;
  endDate?: string;
}
