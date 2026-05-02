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

  private baseUrl = 'http://localhost:8086/StreetLeague'; // ✅ port + context path corrects

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

<<<<<<< HEAD
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
=======

>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5

  getRole(): string | null {
    return localStorage.getItem('RoleUserConnect');
  }



  getEmail(): string | null {
    return localStorage.getItem('EmailUserConnect');
  }

  getUserId(): string | null {
    return localStorage.getItem('UserIdConnect');
  }

<<<<<<< HEAD
  getEmail(): string | null {
    return localStorage.getItem('EmailUserConnect');
  }
}

=======
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
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
