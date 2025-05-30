import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Authentification
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register-stagiaire',
    loadComponent: () => import('./components/auth/register-stagiaire/register-stagiaire.component').then(m => m.RegisterStagiaireComponent)
  },
  {
    path: 'register-recruteur',
    loadComponent: () => import('./components/auth/register-recruteur/register-recruteur.component').then(m => m.RegisterRecruteurComponent)
  },

  // Layout principal
  {
    path: '',
    loadComponent: () => import('./components/layout/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'admin',
        loadComponent: () => import('./components/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'ADMINISTRATEUR' }
      },
      {
        path: 'stagiaire',
        loadComponent: () => import('./components/stagiaire/stagiaire-dashboard/stagiaire-dashboard.component').then(m => m.StagiaireDashboardComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'STAGIAIRE' }
      },
      {
        path: 'recruteur',
        loadComponent: () => import('./components/recruteur/recruteur-dashboard/recruteur-dashboard.component').then(m => m.RecruteurDashboardComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'RECRUTEUR' }
      },

      // Routes offres
      {  
        path: 'offres',
        loadComponent: () => import('./components/offre/offre-list/offre-list.component').then(m => m.OffreListComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: ['ADMINISTRATEUR', 'RECRUTEUR', 'STAGIAIRE'] }
      },
      {
        path: 'offres/ajouter',
        loadComponent: () => import('./components/offre/add-offre/add-offre.component').then(m => m.AddOffreComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'RECRUTEUR' }
      },
      {
        path: 'offres/modifier/:id',
        loadComponent: () => import('./components/offre/edit-offre/edit-offre.component').then(m => m.EditOffreComponent),
        canActivate: [AuthGuard],
        data: { expectedRole: 'RECRUTEUR' }
      },

     
    ]
  },

  // Redirection par défaut
  {
    path: '**',
    redirectTo: ''
  }
];