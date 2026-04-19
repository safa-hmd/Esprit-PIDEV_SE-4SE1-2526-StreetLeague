import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

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
  idUser: number;
}

@Injectable({
  providedIn: 'root', 
})
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
      //  Sauvegarde automatique du token
      tap(response => {
        localStorage.setItem('TokenUserConnect', response.token);
        localStorage.setItem('EmailUserConnect', response.email);
        localStorage.setItem('RoleUserConnect',  response.role);
       localStorage.setItem('UserIdConnect', String(response.idUser)); 
      })
    );
  }

  logout(): void {
    localStorage.removeItem('TokenUserConnect');
    localStorage.removeItem('EmailUserConnect');
    localStorage.removeItem('RoleUserConnect');
    localStorage.removeItem('UserIdConnect');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('TokenUserConnect');
  }

getRole(): string {
  return localStorage.getItem('RoleUserConnect') || '';
}

  getToken(): string | null {
    return localStorage.getItem('TokenUserConnect');
  }

  //add this without unitaire tests

  loginWithGoogle(): void {
  window.location.href = 
    'http://localhost:8086/StreetLeague/oauth2/authorization/google';
}

forgotPassword(email: string): Observable<string> {
  return this.http.post(
    `http://localhost:8086/StreetLeague/auth/forgot-password`,
    { email },
    { responseType: 'text' }
  );
}

resetPassword(token: string, newPassword: string): Observable<string> {
  return this.http.post(
    `http://localhost:8086/StreetLeague/auth/reset-password`,
    { token, newPassword },
    { responseType: 'text' }
  );
}
}