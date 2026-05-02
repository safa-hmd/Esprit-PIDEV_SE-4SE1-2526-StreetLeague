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

    // Si pas de token → send la requête sans modification (endpoint public)
    if (!token) {
      return next.handle(request);
    }

    // Nettoyer le token (delete les guillemets JSON éventuels)
    const cleanToken = token.replace(/"/g, '');

    // Cloner la requête et add le header Authorization
    const authRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${cleanToken}`,
      },
    });

    return next.handle(authRequest);
  }


  
}