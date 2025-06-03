

//////////// stagiare aussi ////////////////////////////////////////////////
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
import { FileUploadModule } from 'primeng/fileupload';

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
    ButtonModule,
    FileUploadModule
  ]
})
export class RegisterStagiaireComponent {
  registerForm: FormGroup;
  errorMessage = '';
  cvFile: File | null = null;

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

  onCvSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.cvFile = input.files[0];
      console.log("📄 CV sélectionné :", this.cvFile.name);
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
      formData.append('cv', this.cvFile); // le nom 'cv' doit correspondre à celui attendu dans le backend
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