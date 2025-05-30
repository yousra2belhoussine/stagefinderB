import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChipModule } from 'primeng/chip';
import { BadgeModule } from 'primeng/badge';
import { ButtonModule } from 'primeng/button';
import { PaginatorModule } from 'primeng/paginator';
import { Router, RouterModule } from '@angular/router';
import { OffreService } from '../../../services/offre.service';
import { OffreDTO } from '../../../models/offre.model';
import { OffrePageResponse } from '../../../models/offre-page-response.model';

@Component({
  standalone: true,
  selector: 'app-offre-list',
  imports: [
    CommonModule,
    RouterModule,
    ChipModule,
    BadgeModule,
    ButtonModule,
    PaginatorModule
  ],
  templateUrl: './offre-list.component.html',
  styleUrls: ['./offre-list.component.css']
})
export class OffreListComponent implements OnInit {

  offres: OffreDTO[] = [];
  currentPage = 0;
  rowsPerPage = 3;
  totalRecords = 0;

  constructor(
    private offreService: OffreService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOffres(this.currentPage, this.rowsPerPage);
  }

  loadOffres(page: number, size: number): void {
    this.offreService.getOffres(page, size).subscribe({
      next: (response: OffrePageResponse) => {
        // Tri sécurisé avec fallback sur 0 si id manquant
        this.offres = response.offres.sort((a, b) => (a.id ?? 0) - (b.id ?? 0));
        this.currentPage = response.currentPage;
        this.totalRecords = response.totalItems;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des offres :', err);
      }
    });
  }

  deleteOffre(id: number): void {
    this.offreService.deleteOffre(id).subscribe({
      next: () => this.loadOffres(this.currentPage, this.rowsPerPage),
      error: (err) => console.error('Erreur suppression offre:', err)
    });
  }

  navigateToModifier(id: number): void {
    this.router.navigate(['/offres/modifier', id]);
  }
}
