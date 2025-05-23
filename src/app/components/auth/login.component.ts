import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

// PrimeNG
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    RouterModule   // ✅ Nécessaire pour routerLink
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Veuillez remplir tous les champs';
      return;
    }

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: (res) => {
//        const decoded = this.authService.getDecodedToken();
const decoded = this.authService.getDecodedToken();
if (!decoded || !decoded.role || !decoded.id) {
  this.errorMessage = 'Token invalide';
  return;
}

        //        const decoded = JSON.parse(atob(res.token.split('.')[1]));
        const role = decoded.role;
        const userId = decoded.id; // 👈 Ajout de l'extraction de l'id

        // 🔐 Stocker l'id si tu veux l'utiliser plus tard dans d'autres composants
        localStorage.setItem('user_id', userId);


        if (role === 'ADMINISTRATEUR') this.router.navigate(['/admin']);
        else if (role === 'RECRUTEUR') this.router.navigate(['/recruteur']);
        else if (role === 'STAGIAIRE') this.router.navigate(['/stagiaire']);
        else this.errorMessage = 'Rôle non reconnu';
      },
      error: () => {
        this.errorMessage = 'Identifiants invalides';
      }
    });
  }
}
