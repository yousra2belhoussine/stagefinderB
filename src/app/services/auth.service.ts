import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { User, NewUser } from '../models/user.model';
import { AuthResponse } from '../models/auth-response.model';
import {jwtDecode} from 'jwt-decode';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.checkAuthStatus();
  }

  private checkAuthStatus(): void {
    const token = localStorage.getItem('access_token');
    this.isAuthenticatedSubject.next(!!token);
  }

  login(email: string, password: string): Observable<AuthResponse> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, password }, { headers }).pipe(
      tap((response) => {
        if (response.token) { // ✅ Correction ici
          localStorage.setItem('access_token', response.token);
          localStorage.setItem('refresh_token', response.refreshToken || '');
          this.isAuthenticatedSubject.next(true);
        } else {
          console.error('Token manquant dans la réponse');
        }
      })
    );
  }

  register(userData: User): Observable<AuthResponse> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData, { headers });
  }

  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }
  getDecodedToken(): any | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return jwtDecode(token);
    } catch (e) {
      console.error('Erreur de décodage du token', e);
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem('refresh_token');
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('Aucun token de rafraîchissement disponible'));
    }

    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json')
      .set('Authorization', `Bearer ${refreshToken}`);

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh-token`, {}, { headers }).pipe(
      tap((response) => {
        if (response.token) { // ✅ Correction ici aussi
          localStorage.setItem('access_token', response.token);
          if (response.refreshToken) {
            localStorage.setItem('refresh_token', response.refreshToken);
          }
        } else {
          this.logout();
        }
      }),
      catchError((error) => {
        this.logout();
        return throwError(() => error);
      })
    );
  }
  registerWithFormData(formData: FormData): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, formData);
  }
  registerAdmin(userData: User): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('http://localhost:8080/auth/register-admin', userData);
  }
  getUserIdFromToken(): number | null {
    const token = localStorage.getItem('access_token');
    if (!token) return null;
  
    try {
      const decoded: any = jwtDecode(token);
      return decoded.id || null;
    } catch (e) {
      console.error('Erreur de décodage du token :', e);
      return null;
    }
  }
  
  
}
