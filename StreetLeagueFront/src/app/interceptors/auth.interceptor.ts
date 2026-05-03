import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const token = localStorage.getItem('TokenUserConnect');

    if (request.url.includes('/auth/')) {
      return next.handle(request);
    }

    if (!token) {
      return next.handle(request);
    }

    const cleanToken = token.replace(/"/g, '');

    if (request.body instanceof FormData) {
      const authRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${cleanToken}`,
        },
      });
      return next.handle(authRequest);
    }

    const authRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${cleanToken}`,
      },
    });

    return next.handle(authRequest);
  }
}