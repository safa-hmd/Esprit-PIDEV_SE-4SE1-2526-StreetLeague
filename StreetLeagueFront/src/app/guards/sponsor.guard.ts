import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class SponsorGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
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
    
    return false;
  }
}
