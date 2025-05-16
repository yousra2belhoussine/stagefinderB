import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../../services/auth.service';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule,RouterModule],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent {
  role: string | null = null;
  dropdownOpen: boolean = false;
  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }


  constructor(private authService: AuthService,private router: Router) {
    const token = localStorage.getItem('access_token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.role = decoded.role;
    }
  }

  logout() {
    this.authService.logout();
    this.role=null;
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
  isLoggedIn(): boolean {
    return !!localStorage.getItem('access_token');
  }
  
}
