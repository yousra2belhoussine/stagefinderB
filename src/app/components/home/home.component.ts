import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service'; // adapte ce chemin si besoin
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  dropdownOpen = false;
  role: string | null = null;

  constructor(private authService: AuthService, private router: Router) {
    const token = localStorage.getItem('access_token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.role = decoded.role;
    }
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }

  logout(): void {
    this.authService.logout();
    this.role = null;
    this.router.navigate(['/login']);
  }

  isAdmin(): boolean {
    return this.role === 'ADMINISTRATEUR';
  }

  isStagiaire(): boolean {
    return this.role === 'STAGIAIRE';
  }

  isRecruteur(): boolean {
    return this.role === 'RECRUTEUR';
  }

 // isLoggedIn(): boolean {
   // return !!localStorage.getItem('access_token');
  //}
  isLoggedIn(): boolean {
  return this.authService.isLoggedIn(); // prend en compte expiration du token
}
getRole(): string | null {
  const token = this.authService.getToken();
  if (!token) return null;

  try {
    const decoded: any = jwtDecode(token);
    return decoded.role || null;
  } catch (e) {
    return null;
  }
}

}
