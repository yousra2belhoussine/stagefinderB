import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-recruteur-dashboard',
  standalone: true,
  imports: [CommonModule, ButtonModule],
  templateUrl: './recruteur-dashboard.component.html',
  styleUrls: ['./recruteur-dashboard.component.css']
})
export class RecruteurDashboardComponent {
  constructor(private router: Router) {}

  goTo(path: string): void {
    this.router.navigate([path]);
  }
}
