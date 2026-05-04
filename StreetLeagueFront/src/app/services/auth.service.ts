import { environment } from 'src/environments/environment';
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
  token: string;
  email: string;
  role: string;

  idUser: number;

}

@Injectable({
  providedIn: 'root', 
})
export class AuthService {

  private baseUrl = `${environment.baseUrl}`; // ✅ port + context path corrects

  constructor(private http: HttpClient) {}

  register(req: RegisterRequest): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/auth/register`, req,
      { responseType: 'text' }
    );
  }

  isLoggedIn(): boolean {
  const token = localStorage.getItem('TokenUserConnect');
  if (!token) return false;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const isExpired = payload.exp * 1000 < Date.now();
    if (isExpired) {
      this.logout(); // nettoie automatiquement
      return false;
    }
    return true;
  } catch (e) {
    this.logout(); // token corrompu → nettoie
    return false;
  }
}

getToken(): string | null {
  if (!this.isLoggedIn()) return null; // vérifie expiration
  return localStorage.getItem('TokenUserConnect');
}

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.baseUrl}/auth/login`, req
    ).pipe(


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



  getRole(): string | null {
    return localStorage.getItem('RoleUserConnect');
  }



  getEmail(): string | null {
    return localStorage.getItem('EmailUserConnect');
  }

  getUserId(): string | null {
    return localStorage.getItem('UserIdConnect');
  }

  //add this without unitaire tests

  loginWithGoogle(): void {
  window.location.href = 
    `${environment.baseUrl}/oauth2/authorization/google`;
}



forgotPassword(email: string): Observable<string> {
  return this.http.post(
    `${environment.baseUrl}/auth/forgot-password`,
    { email },
    { responseType: 'text' }
  );
}

resetPassword(token: string, newPassword: string): Observable<string> {
  return this.http.post(
    `${environment.baseUrl}/auth/reset-password`,
    { token, newPassword },
    { responseType: 'text' }
  );
}

// auth.service.ts - Ajouter cette méthode
getCurrentUserEmail(): string {
  return localStorage.getItem('EmailUserConnect') || '';
}

getCurrentUserId(): number {
  const id = localStorage.getItem('UserIdConnect');
  return id ? parseInt(id) : 0;
}

  // ✅ URL correcte avec port 8086 + context path /StreetLeague
  getUserIdByEmail(): Observable<number> {
    const email = localStorage.getItem('EmailUserConnect');
    return this.http.get<number>(
      `${this.baseUrl}/auth/getUserId?email=${email}`
    );
  }

}

