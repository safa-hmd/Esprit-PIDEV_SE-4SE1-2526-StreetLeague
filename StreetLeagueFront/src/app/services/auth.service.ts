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
  id: string;
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
       localStorage.setItem('UserIdConnect', response.id); 
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

  getRole(): string | null {
    return localStorage.getItem('RoleUserConnect');
  }

  getToken(): string | null {
    return localStorage.getItem('TokenUserConnect');
  }
  
  // ✅ Decode JWT payload — works without any external library
  private decodeToken(): Record<string, any> | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      // Fix base64url padding before decoding
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch {
      return null;
    }
  }
 
  // ✅ Get user ID from JWT claim 'id' (added in JwtService.java)
  getUserId(): number | null {
    const decoded = this.decodeToken();
    if (!decoded) return null;
    const id = decoded['id'];
    return id != null ? Number(id) : null;
  }
 
  // ✅ Get full name from JWT claim 'fullName'
  getFullName(): string | null {
    const decoded = this.decodeToken();
    return decoded?.['fullName'] ?? localStorage.getItem('EmailUserConnect');
  }
 
  // ✅ Get email from JWT subject
  getEmail(): string | null {
    return localStorage.getItem('EmailUserConnect');
  }

}