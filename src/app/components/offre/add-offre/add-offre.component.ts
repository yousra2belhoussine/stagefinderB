import { Component } from '@angular/core';
import { OffreService } from '../../../services/offre.service';
import { OffreDTO } from '../../../models/offre.model';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-offre',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    InputTextModule,
    CalendarModule,
    DropdownModule,
    ButtonModule
  ],
  templateUrl: './add-offre.component.html',
  styleUrls: ['./add-offre.component.css']
})
export class AddOffreComponent {
  newOffre: OffreDTO = {

    anneesExperience: '',
    description: '',
    ville: '',
    nomEntreprise: '',
    salaire: 0,
    competenceExigee: '',
    dateExpiration: new Date(),
    datePublication: new Date(),
    publieParNom: '',
    categorieNom: '',
    publieParId: 0,
    categorieId: 0
  };

  categories = [
    { id: 1, name: 'INFORMATIQUE' },
    { id: 2, name: 'DEVELOPPEMENT' },
    { id: 3, name: 'CYBERSECURITE' },
    { id: 4, name: 'CLOUD_COMPUTING' },
    { id: 5, name: 'DATA_SCIENCE' },
    { id: 6, name: 'IA' }
  ];

  constructor(private offreService: OffreService, private router: Router) {
    const token = localStorage.getItem('access_token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.newOffre.publieParId = decoded.id;
      // Ne pas définir publieParNom, inutile à l'envoi
    }
  }

  saveOffre() {
    // Création d'un objet épuré sans publieParNom et categorieNom
    const { publieParNom, categorieNom, ...offreToSend } = this.newOffre;

    console.log('Envoi de l\'offre :', offreToSend);

    this.offreService.addOffre(offreToSend).subscribe({
      next: () => {
        this.router.navigate(['/offres']);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur ajout offre :', err.message);
      }
    });  // <-- Parenthèse fermante ajoutée ici
  }
}
