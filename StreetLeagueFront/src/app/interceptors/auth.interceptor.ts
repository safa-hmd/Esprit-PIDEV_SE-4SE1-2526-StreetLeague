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

<<<<<<< HEAD
    // Si pas de token → send la requête sans modification (endpoint public)
=======
    if (request.url.includes('/auth/')) {
  return next.handle(request);
}

    // Si pas de token → envoyer la requête sans modification (endpoint public)
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
    if (!token) {
      return next.handle(request);
    }

    // Nettoyer le token (delete les guillemets JSON éventuels)
    const cleanToken = token.replace(/"/g, '');

<<<<<<< HEAD
    // Cloner la requête et add le header Authorization
=======
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
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
    const authRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${cleanToken}`,
      },
    });

    return next.handle(authRequest);
  }
}