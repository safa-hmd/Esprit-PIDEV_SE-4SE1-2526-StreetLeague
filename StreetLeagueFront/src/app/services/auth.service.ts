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
  id: number;        // ← nouveau
  token: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private http: HttpClient) {}

  register(req: RegisterRequest): Observable<string> {
    return this.http.post(
      `http://localhost:8086/StreetLeague/auth/register`, req,
      { responseType: 'text' }
    );
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `http://localhost:8086/StreetLeague/auth/login`, req
    ).pipe(
      tap(response => {
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        localStorage.setItem('RoleUserConnect',  response.role);
        localStorage.setItem('IdUserConnect',    response.id.toString()); // ← nouveau
      })
    );
  }

  logout(): void {
    localStorage.removeItem('TokenUserConnect');
    localStorage.removeItem('EmailUserConnect');
    localStorage.removeItem('RoleUserConnect');
    localStorage.removeItem('IdUserConnect');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('TokenUserConnect');
  }

  getRole(): string | null {
    return localStorage.getItem('RoleUserConnect');
  }

  getToken(): string | null {
    return localStorage.getItem('TokenUserConnect');
  }

  // ← nouvelle méthode
  getUserId(): number | null {
    const id = localStorage.getItem('IdUserConnect');
    return id ? Number(id) : null;
  }
  getEmail(): string | null {
  return localStorage.getItem('EmailUserConnect');
}
}