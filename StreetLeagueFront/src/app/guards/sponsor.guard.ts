import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class SponsorGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
<<<<<<< HEAD:StreetLeagueFront/src/app/guards/sponsor.guard.ts
    const role = this.authService.normalizeRole(this.authService.getRole());
    
    // Permettre à SPONSOR et ADMIN d'accéder à la route /sponsor
    if (role === 'ROLE_SPONSOR' || role === 'ROLE_ADMIN') return true;
    
    if (!this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/login');
      return false;
    }
    
    // Rediriger selon le rôle
    if (role === 'ROLE_PLAYER') {
      this.router.navigateByUrl('/client');
    } else if (role === 'ROLE_COACH') {
      this.router.navigateByUrl('/coach');
    }
    
=======
    const role = this.authService.getRole();
    if (role === 'ROLE_COACH') return true;
    this.router.navigateByUrl('/login');
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5:StreetLeagueFront/src/app/guards/coach.guard.ts
    return false;
  }
}
