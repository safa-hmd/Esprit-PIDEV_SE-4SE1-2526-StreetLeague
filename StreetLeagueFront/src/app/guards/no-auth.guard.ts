import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    const token = localStorage.getItem('TokenUserConnect');

    if (!token) return true; // pas connecté → accès autorisé

    const role = this.authService.getRole();

    if (role === 'ROLE_ADMIN')  { this.router.navigateByUrl('/admin');  return false; }
    if (role === 'ROLE_COACH')  { this.router.navigateByUrl('/coach');  return false; }
    if (role === 'ROLE_PLAYER') { this.router.navigateByUrl('/client'); return false; }

    // rôle inconnu mais token présent → rediriger vers /client par défaut
    this.router.navigateByUrl('/client');
    return false;
  }
}