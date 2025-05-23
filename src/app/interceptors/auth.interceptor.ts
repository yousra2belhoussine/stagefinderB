import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AuthResponse } from '../models/auth-response.model';

export const authInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    request = addTokenToRequest(request, token);
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const refreshToken = localStorage.getItem('refresh_token');

      // ✅ Protection AVANT de tenter un refresh
      if (
        error.status === 401 &&
        !request.url.includes('refresh-token') &&
        refreshToken &&
        refreshToken.trim() !== ''
      ) {
        return handle401Error(request, next, authService);
      }

      return throwError(() => error);
    })
  );
};

function addTokenToRequest(
  request: HttpRequest<unknown>,
  token: string
): HttpRequest<unknown> {
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });
}

function handle401Error(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService
) {
  return authService.refreshToken().pipe(
    switchMap((response: AuthResponse) => {
      return next(addTokenToRequest(request, response.token));
    }),
    catchError((error) => {
      authService.logout();
      return throwError(() => error);
    })
  );
}
