import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

canActivate(): boolean {
  const token = localStorage.getItem('TokenUserConnect');
  if (!token) return true;

<<<<<<< HEAD
    // Toudays autoriser les écrans d'authentification (token expiré / changement de compte).
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
    if (
      role === 'ROLE_COACH' ||
      role === 'ROLE_PLAYER' ||
      role === 'ROLE_SPONSOR' ||
      role === 'ROLE_DELIVERY'
    ) {
      this.router.navigateByUrl('/client');
      return false;
    }

    this.authService.logout();
=======
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (payload.exp * 1000 < Date.now()) {
      localStorage.clear();
      return true;
    }
  } catch (e) {
    localStorage.clear();
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
    return true;
  }

  const role = localStorage.getItem('RoleUserConnect');

  if (role === 'ROLE_ADMIN'    || role === 'ADMIN')    { this.router.navigateByUrl('/admin');    return false; }
  if (role === 'ROLE_COACH'    || role === 'COACH')    { this.router.navigateByUrl('/coach');    return false; }
  if (role === 'ROLE_PLAYER'   || role === 'PLAYER')   { this.router.navigateByUrl('/client');   return false; }
  if (role === 'ROLE_DELIVERY' || role === 'DELIVERY') { this.router.navigateByUrl('/delivery'); return false; } // ✅

  localStorage.clear();
  return true;
}
}