import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  role: 'PLAYER' | 'ADMIN' | 'COACH' | 'SPONSOR' | 'DELIVERY';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  id: number;
  token: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly base = 'http://localhost:8086/StreetLeague';

  constructor(private http: HttpClient) {}

  // ── Register ────────────────────────────────────────────
  register(req: RegisterRequest): Observable<string> {
    return this.http.post(
      `${this.base}/auth/register`, req,
      { responseType: 'text' }
    );
  }

  // ── Login ───────────────────────────────────────────────
  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.base}/auth/login`, req
    ).pipe(
      tap(response => {
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        localStorage.setItem('RoleUserConnect',  response.role);
        localStorage.setItem('IdUserConnect',    response.id.toString());
      })
    );
  }

  // ── Logout ──────────────────────────────────────────────
  logout(): void {
    localStorage.removeItem('TokenUserConnect');
    localStorage.removeItem('EmailUserConnect');
    localStorage.removeItem('RoleUserConnect');
    localStorage.removeItem('IdUserConnect');
  }

  // ── Google OAuth2 (feature/match-team-training) ─────────
  loginWithGoogle(): void {
    window.location.href =
      `${this.base}/oauth2/authorization/google`;
  }

  // ── Forgot Password (feature/match-team-training) ───────
  forgotPassword(email: string): Observable<string> {
    return this.http.post(
      `${this.base}/auth/forgot-password`,
      { email },
      { responseType: 'text' }
    );
  }

  // ── Reset Password (feature/match-team-training) ────────
  resetPassword(token: string, newPassword: string): Observable<string> {
    return this.http.post(
      `${this.base}/auth/reset-password`,
      { token, newPassword },
      { responseType: 'text' }
    );
  }

  // ── Getters ─────────────────────────────────────────────
  isLoggedIn(): boolean {
    return !!localStorage.getItem('TokenUserConnect');
  }

  getRole(): string | null {
    return localStorage.getItem('RoleUserConnect');
  }

  getToken(): string | null {
    return localStorage.getItem('TokenUserConnect');
  }

  getUserId(): number | null {
    const id = localStorage.getItem('IdUserConnect');
    return id ? Number(id) : null;
  }

  getEmail(): string | null {
    return localStorage.getItem('EmailUserConnect');
  }
}