import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Rôles autorisés sur l’espace client (/client) — aligné sur le backend (Spring: ROLE_*). */
const CLIENT_AREA_ROLES = ['ROLE_PLAYER', 'ROLE_SPONSOR', 'ROLE_DELIVERY'] as const;

@Injectable({ providedIn: 'root' })
export class PlayerGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/login');
      return false;
    }

    const role = this.authService.normalizeRole(this.authService.getRole());

    if (role === 'ROLE_ADMIN') {
      this.router.navigateByUrl('/admin');
      return false;
    }
    if (role === 'ROLE_COACH') {
      this.router.navigateByUrl('/coach');
      return false;
    }

    if (role && CLIENT_AREA_ROLES.includes(role as (typeof CLIENT_AREA_ROLES)[number])) {
      return true;
    }

    this.authService.logout();
    this.router.navigateByUrl('/login');
    return false;
  }
}



