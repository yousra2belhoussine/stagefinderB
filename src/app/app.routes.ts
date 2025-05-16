import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Authentification
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register-stagiaire',
    loadComponent: () => import('./components/auth/register-stagiaire/register-stagiaire.component')
      .then(m => m.RegisterStagiaireComponent)
  },
  {
    path: 'register-recruteur',
    loadComponent: () => import('./components/auth/register-recruteur/register-recruteur.component')
      .then(m => m.RegisterRecruteurComponent)
  },


  // Layout principal (protégé avec AuthGuard sur les enfants)
  {
    path: '',
    loadComponent: () => import('./components/layout/main-layout/main-layout.component')
      .then(m => m.MainLayoutComponent),
    children: [
      {
        path: 'admin',
        loadComponent: () => import('./components/admin/admin-dashboard/admin-dashboard.component')
          .then(m => m.AdminDashboardComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'ADMINISTRATEUR' }
      },
      {
        path: 'stagiaire',
        loadComponent: () => import('./components/stagiaire/stagiaire-dashboard/stagiaire-dashboard.component')
          .then(m => m.StagiaireDashboardComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'STAGIAIRE' }
      },
      {
        path: 'recruteur',
        loadComponent: () => import('./components/recruteur/recruteur-dashboard/recruteur-dashboard.component')
          .then(m => m.RecruteurDashboardComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'RECRUTEUR' }
      }
    ]
  },

  // Redirection par défaut
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
