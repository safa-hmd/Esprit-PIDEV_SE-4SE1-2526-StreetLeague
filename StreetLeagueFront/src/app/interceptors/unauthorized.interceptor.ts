import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpErrorResponse,
} from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * JWT expiré ou refusé : nettoie la session et renvoie vers login ou admin-login.
 * Les appels /auth/* ne déclenchent pas de redirection (évite les boucles).
 */
@Injectable()
export class UnauthorizedInterceptor implements HttpInterceptor {
  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler) {
    return next.handle(req).pipe(
      catchError((err: unknown) => {
        if (err instanceof HttpErrorResponse && err.status === 401) {
          const url = req.url;
          const isAuthCall =
            url.includes('/auth/login') ||
            url.includes('/auth/register') ||
            url.includes('/auth/forgot-password') ||
            url.includes('/auth/reset-password') ||
            url.includes('/auth/complete-google-register');
          if (!isAuthCall) {
            this.authService.logout();
            const target = this.router.url.startsWith('/admin')
              ? '/admin-login'
              : '/login';
            this.router.navigateByUrl(target);
          }
        }
        return throwError(() => err);
      })
    );
  }
}
