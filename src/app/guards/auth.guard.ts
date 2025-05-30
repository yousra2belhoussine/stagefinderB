import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

 canActivate(
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): Observable<boolean> | Promise<boolean> | boolean {
  return this.authService.isAuthenticated$.pipe(
    take(1),
    map(isAuthenticated => {
      if (!isAuthenticated) {
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return false;
      }

      const token = localStorage.getItem('access_token');
      if (!token) {
        this.router.navigate(['/login']);
        return false;
      }

      try {
        const decodedToken: any = jwtDecode(token);
        const expectedRole = route.data['expectedRole'];

        if (expectedRole) {
          if (Array.isArray(expectedRole)) {
            // Si expectedRole est un tableau, vérifier si le rôle de l'utilisateur y figure
            if (!expectedRole.includes(decodedToken.role)) {
              this.router.navigate(['/login']);
              return false;
            }
          } else {
            // Sinon, comparer directement
            if (decodedToken.role !== expectedRole) {
              this.router.navigate(['/login']);
              return false;
            }
          }
        }

        return true;
      } catch (e) {
        console.error('Erreur lors du décodage du token :', e);
        this.router.navigate(['/login']);
        return false;
      }
    })
  );
}

}
