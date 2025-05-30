import { Component, OnInit } from '@angular/core';
import { OffreService } from '../../../services/offre.service';
import { OffreDTO } from '../../../models/offre.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-edit-offre',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
    ToastModule
  ],
  templateUrl: './edit-offre.component.html',
  styleUrls: ['./edit-offre.component.css'],
  providers: [MessageService]
})
export class EditOffreComponent implements OnInit {
  offreId: number | null = null;

  offre: OffreDTO = {
    id: 0,
    anneesExperience: '',
    description: '',
    ville: '',
    categorieNom: '',
    publieParNom: '',
    nomEntreprise: '',
    salaire: 0,
    competenceExigee: '',
    datePublication: new Date(),
    dateExpiration: new Date(),
    categorieId: 0,
    publieParId: 0
  };

  categories = [
    { id: 1, name: 'INFORMATIQUE' },
    { id: 2, name: 'DEVELOPPEMENT' },
    { id: 3, name: 'CYBERSECURITE' },
    { id: 4, name: 'CLOUD_COMPUTING' },
    { id: 5, name: 'DATA_SCIENCE' },
    { id: 6, name: 'IA' }
  ];

  constructor(
    private offreService: OffreService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.offreId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.offreId) {
      this.offreService.getOffreById(this.offreId).subscribe({
        next: (data) => {
          this.offre = data;
        },
        error: (err: HttpErrorResponse) => {
          console.error('Erreur récupération offre :', err.message);
        }
      });
    }
  }

  updateOffre(): void {
    if (this.offreId) {
      this.offreService.updateOffre(this.offreId, this.offre).subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: 'Succès', detail: 'Offre mise à jour.' });
          this.router.navigate(['/offres']);
        },
        error: (err: HttpErrorResponse) => {
          console.error('Erreur mise à jour offre :', err.message);
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Échec mise à jour offre.' });
        }
      });
    }
  }
}
