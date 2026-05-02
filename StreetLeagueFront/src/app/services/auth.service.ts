import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { API_BASE_URL } from 'src/environments/api-url';

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
  token: string;
  email: string;
  role: string;
  /** Backend Java record sérialise en idUser */
  id?: number;
  userId?: number;
  idUser?: number;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  constructor(private http: HttpClient) {}

  private readonly apiUrl = `${API_BASE_URL}/auth`;

  register(req: RegisterRequest): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/register`, req,
      { responseType: 'text' }
    );
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/login`, req
    ).pipe(
      tap(response => {
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        const role =
          (response.role && String(response.role).trim()) ||
          this.readRoleFromJwt(response.token) ||
          '';
        if (role) {
          localStorage.setItem('RoleUserConnect', role);
        }
        const userId = response.id ?? response.userId ?? response.idUser;
        if (userId !== undefined && userId !== null) {
          localStorage.setItem('UserIdConnect', String(userId));
        }
      })
    );
  }

  /** Claim `role` du JWT (ex. ROLE_ADMIN) si le JSON de login omet le champ role. */
  readRoleFromJwt(token: string | null | undefined): string | null {
    if (!token || typeof token !== 'string') return null;
    const clean = token.replace(/"/g, '');
    const parts = clean.split('.');
    if (parts.length < 2) return null;
    try {
      let b64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      while (b64.length % 4) b64 += '=';
      const json = atob(b64);
      const payload = JSON.parse(json) as { role?: string };
      const r = payload.role;
      return typeof r === 'string' && r.trim() ? r.trim() : null;
    } catch {
      return null;
    }
  }

  forgotPassword(email: string): Observable<string> {
    const payload: ForgotPasswordRequest = { email };
    return this.http.post(`${this.apiUrl}/forgot-password`, payload, {
      responseType: 'text',
    });
  }

  resetPassword(token: string, newPassword: string): Observable<string> {
    const payload: ResetPasswordRequest = { token, newPassword };
    return this.http.post(`${this.apiUrl}/reset-password`, payload, {
      responseType: 'text',
    });
  }

  loginWithGoogle(): void {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
  }

  getUserId(): number | null {
    const id = localStorage.getItem('UserIdConnect');
    return id ? parseInt(id, 10) : null;
  }

  logout(): void {
    localStorage.removeItem('TokenUserConnect');
    localStorage.removeItem('EmailUserConnect');
    localStorage.removeItem('RoleUserConnect');
    localStorage.removeItem('UserIdConnect');
  }

  getName(): string | null {
    // Backne le name basé sur le rôle de l'utilisateur
    const role = this.normalizeRole(this.getRole());
    switch (role) {
      case 'ROLE_PLAYER':
        return 'Player';
      case 'ROLE_SPONSOR':
        return 'Sponsor';
      case 'ROLE_ADMIN':
        return 'Admin';
      case 'ROLE_COMMUNITY_MANAGER':
        return 'Manager';
      default:
        // Fallback : utilise une partie de l'email
        const email = this.getEmail();
        if (email) {
          const namePart = email.split('@')[0];
          return namePart.charAt(0).toUpperCase() + namePart.slice(1);
        }
        return null;
    }
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('TokenUserConnect');
  }

  getRole(): string | null {
    return localStorage.getItem('RoleUserConnect');
  }

  /**
   * Unifie les rôles renvoyés par le back (ADMIN, ROLE_ADMIN, admin, etc.)
   * pour que les redirections (login, OAuth2) reconnaissent bien ROLE_ADMIN.
   */
  normalizeRole(role: string | null): string | null {
    if (!role) return null;
    const r = role.trim();
    if (!r) return null;
    const upper = r.toUpperCase();
    if (upper.startsWith('ROLE_')) {
      return 'ROLE_' + upper.slice(5);
    }
    return 'ROLE_' + upper;
  }

  getToken(): string | null {
    return localStorage.getItem('TokenUserConnect');
  }

  getEmail(): string | null {
    return localStorage.getItem('EmailUserConnect');
  }
}

