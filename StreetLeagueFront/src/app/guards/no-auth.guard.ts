import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

canActivate(): boolean {
  const token = localStorage.getItem('TokenUserConnect');
  if (!token) return true;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (payload.exp * 1000 < Date.now()) {
      localStorage.clear();
      return true;
    }
  } catch (e) {
    localStorage.clear();
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