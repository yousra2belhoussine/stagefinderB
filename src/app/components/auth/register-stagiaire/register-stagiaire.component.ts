import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../models/user.model';

// PrimeNG modules
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-register-stagiaire',
  standalone: true,
  templateUrl: './register-stagiaire.component.html',
  styleUrls: ['./register-stagiaire.component.css'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    ButtonModule
  ]
})
export class RegisterStagiaireComponent {
  registerForm: FormGroup;
  errorMessage = '';
  cvFile: File | null = null;
  lettreMotivationFile: File | null = null;


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      nom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      tel: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      password: ['', Validators.required]
    });
  }

  onFileSelected(event: Event, type: 'cv' | 'lettre') {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (type === 'cv') {
        this.cvFile = file;
      } else if (type === 'lettre') {
        this.lettreMotivationFile = file;
      }
    }
  }
  
  onSubmit() {
    if (this.registerForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs requis.';
      return;
    }
  
    const user: User = {
      ...this.registerForm.value,
      role: 'STAGIAIRE'
    };
  
    const formData = new FormData();
    formData.append('user', new Blob([JSON.stringify(user)], { type: 'application/json' }));
  
    if (this.cvFile) {
      formData.append('cv', this.cvFile);
      console.log("📄 CV sélectionné :", this.cvFile.name);
    }
  
    if (this.lettreMotivationFile) {
      formData.append('lettre', this.lettreMotivationFile); // ✅ correction ici
      console.log("📨 Lettre sélectionnée :", this.lettreMotivationFile.name);
    }
  
    this.authService.registerWithFormData(formData).subscribe({
      next: () => {
        console.log("✅ Stagiaire enregistré avec succès");
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error("❌ Erreur lors de l'inscription :", err);
        this.errorMessage = "Erreur lors de l'inscription.";
      }
    });
  }
  

}
