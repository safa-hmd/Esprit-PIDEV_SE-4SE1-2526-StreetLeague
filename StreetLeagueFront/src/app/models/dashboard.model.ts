export interface FinancialSummary {
  totalRevenue: number;
  totalPayments: number;
  totalRefunds: number;
  pendingCount: number;
}

export interface RevenueByField {
  fieldName: string;
  revenue: number;
  reservationCount: number;
}

export interface RevenueBySport {
  sportType: string;
  revenue: number;
}

export interface RevenueByMonth {
  month: string; // "2025-03"
  revenue: number;
}

export interface TopPlayer {
  playerName: string;
  email: string;
  totalSpent: number;
  paymentCount: number;
}