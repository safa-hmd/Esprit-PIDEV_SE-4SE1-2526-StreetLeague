// src/app/guards/no-auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = localStorage.getItem('TokenUserConnect');
    const role  = localStorage.getItem('RoleUserConnect');

    if (!token) return true; // pas connecté → accès login OK

    // Déjà connecté → rediriger selon le rôle
    if (role === 'ROLE_ADMIN')  this.router.navigateByUrl('/admin');
    else if (role === 'ROLE_COACH') this.router.navigateByUrl('/coach');
    else this.router.navigateByUrl('/client');

    return false;
  }
}