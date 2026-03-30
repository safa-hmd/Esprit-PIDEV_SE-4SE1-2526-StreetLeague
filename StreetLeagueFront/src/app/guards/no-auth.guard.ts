import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const path = state.url.split('?')[0] || '/';

    // Toujours autoriser les écrans d'authentification (token expiré / changement de compte).
    const publicAuthPaths = [
      '/login',
      '/register',
      '/admin-login',
      '/forgot-password',
      '/reset-password',
    ];
    if (
      publicAuthPaths.some(
        (p) => path === p || path.startsWith(`${p}/`)
      )
    ) {
      return true;
    }

    const token = localStorage.getItem('TokenUserConnect');

    if (!token) return true;

    const role = this.authService.normalizeRole(this.authService.getRole());

    if (role === 'ROLE_ADMIN') {
      this.router.navigateByUrl('/admin');
      return false;
    }
    if (role === 'ROLE_COACH') {
      this.router.navigateByUrl('/coach');
      return false;
    }
    if (
      role === 'ROLE_PLAYER' ||
      role === 'ROLE_SPONSOR' ||
      role === 'ROLE_DELIVERY'
    ) {
      this.router.navigateByUrl('/client');
      return false;
    }

    this.authService.logout();
    return true;
  }
}