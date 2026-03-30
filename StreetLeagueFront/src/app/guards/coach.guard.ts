// src/app/guards/coach.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class CoachGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    const role = this.authService.normalizeRole(this.authService.getRole());
    if (role === 'ROLE_COACH') return true;
    if (!this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/login');
      return false;
    }
    this.authService.logout();
    this.router.navigateByUrl('/login');
    return false;
  }
}