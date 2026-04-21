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
    // Récupérer le token depuis localStorage
    const token = localStorage.getItem('TokenUserConnect');

    // Si pas de token → envoyer la requête sans modification (endpoint public)
    if (!token) {
      return next.handle(request);
    }

    // Nettoyer le token (supprimer les guillemets JSON éventuels)
    const cleanToken = token.replace(/"/g, '');

    // For FormData requests, only add Authorization header
    // The browser will automatically handle Content-Type with boundary
    if (request.body instanceof FormData) {
      const authRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${cleanToken}`,
        },
      });
      return next.handle(authRequest);
    }

    // For regular JSON requests, clone and add Authorization header
    const authRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${cleanToken}`,
      },
    });

    return next.handle(authRequest);
  }
}