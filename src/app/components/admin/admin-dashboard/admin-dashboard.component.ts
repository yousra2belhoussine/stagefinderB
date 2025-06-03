import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';

import { UserService } from '../../../services/user.service';
import { FileService } from '../../../services/file.service';
import { User, NewUser } from '../../../models/user.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, SelectModule, ButtonModule, TableModule, DialogModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {
  users: User[] = [];
  filteredUsers: User[] = [];
  selectedRole: string = '';
  userImageUrls: { [id: number]: string } = {};

  roles = [
    { label: 'Tous', value: '' },
    { label: 'Stagiaire', value: 'STAGIAIRE' },
    { label: 'Recruteur', value: 'RECRUTEUR' },
    { label: 'Administrateur', value: 'ADMINISTRATEUR' }
  ];

  newUser: NewUser | User = this.initUser();
  displayModal = false;
  isEditMode = false;

  cvFile?: File;
  lettreFile?: File;
  imageFile?: File;

  constructor(private userService: UserService, private fileService: FileService) {
    this.loadUsers();
  }

  isStagiaire(): boolean {
    return this.newUser.role === 'STAGIAIRE';
  }

  isRecruteur(): boolean {
    return this.newUser.role === 'RECRUTEUR';
  }

  isAdministrateur(): boolean {
    return this.newUser.role === 'ADMINISTRATEUR';
  }

  onRoleChange(): void {
    if (this.newUser.role === 'STAGIAIRE') {
      this.newUser.nomEntreprise = '';
      this.newUser.RC = '';
      this.newUser.ICE = '';
      this.imageFile = undefined;
    } else if (this.newUser.role === 'RECRUTEUR') {
      this.cvFile = undefined;
      this.lettreFile = undefined;
    } else if (this.newUser.role === 'ADMINISTRATEUR') {
      this.cvFile = undefined;
      this.lettreFile = undefined;
      this.imageFile = undefined;
    }
  }

  initUser(): NewUser {
    return {
      nom: '',
      email: '',
      password: '',
      tel: '',
      role: 'STAGIAIRE',
      estValide: true
    };
  }

  onFileSelected(event: any, type: string): void {
    const file = event.target.files[0];
    if (!file) return;

    if (type === 'cv') this.cvFile = file;
    if (type === 'lettre') this.lettreFile = file;
    if (type === 'image') this.imageFile = file;
  }

  download(fileName: string): void {
    this.fileService.downloadFile(fileName);
  }

  /** 👁️ Ouvre un fichier CV dans un nouvel onglet avec token JWT */
  openCv(fileName: string): void {
    this.fileService.openFileInNewTab(fileName);
  }

  /*loadUsers() {
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.filteredUsers = this.users;
        for (let user of this.users) {
          if (user.image) {
            this.fileService.getImageBlobUrl(user.image).then(url => {
              if (url) this.userImageUrls[user.id] = url;
            });
          }
        }
      },*/
      loadUsers() {
  this.userImageUrls = {}; // 🔁 Vider le cache d'images à chaque chargement

  this.userService.getUsers().subscribe({
    next: (data) => {
      this.users = data;
      this.filteredUsers = this.users;

      console.log("🧠 Liste des utilisateurs :");
      this.users.forEach(user => {
        console.log(`ID: ${user.id}, Email: ${user.email}`);
        
        // Sécurité : affecter l'image uniquement si l'id est bien défini
        if (user.image && user.id != null) {
          this.fileService.getImageBlobUrl(user.image).then(url => {
            if (url) {
              this.userImageUrls[user.id] = url;
              console.log(`🖼️ Image chargée pour ID ${user.id}: ${url}`);
            }
          });
        }
      });
    },
      error: (err) => {
        console.error('Erreur lors du chargement des utilisateurs :', err);
      }
    });
  }

  applyFilter() {
    this.filteredUsers = this.selectedRole
      ? this.users.filter(u => u.role === this.selectedRole)
      : this.users;
  }
openModal(user?: User) {
  this.cvFile = undefined;
  this.imageFile = undefined;

  if (user) {
    this.newUser = { ...user };
    this.isEditMode = true;
  } else {
    this.newUser = this.initUser();
    this.isEditMode = false;
  }

  this.displayModal = true;
}

saveUser() {

  if (this.isEditMode && 'id' in this.newUser) {
  const userToUpdate = this.newUser as User;

  // S'il y a un fichier à mettre à jour, utilise le PUT multipart
  if (this.cvFile || this.imageFile) {
    const formData = new FormData();
    formData.append('user', new Blob([JSON.stringify(userToUpdate)], { type: 'application/json' }));

    if (this.cvFile) formData.append('cvFile', this.cvFile);
    if (this.imageFile) formData.append('image', this.imageFile);

    this.userService.updateUserWithFormData(userToUpdate.id, formData).subscribe(() => this.loadUsers());
  } else {
    // Sinon, simple update JSON
    this.userService.updateUser(userToUpdate.id, userToUpdate).subscribe(() => this.loadUsers());
  }
}

  else {
    const formData = new FormData();
    formData.append('user', new Blob([JSON.stringify(this.newUser)], { type: 'application/json' }));

    if (this.cvFile) {
      formData.append('cvFile', this.cvFile);//ici
      //this.newUser.cvFile = this.cvFile.name; // optionnel, utile pour prévisualisation
    }

    if (this.imageFile) {
      formData.append('image', this.imageFile);
     // this.newUser.image = this.imageFile.name; // optionnel, utile pour affichage
    }

    this.userService.registerWithFormData(formData).subscribe(() => this.loadUsers());
  }

  this.displayModal = false;
}


  deleteUser(id: number) {
    this.userService.deleteUser(id).subscribe(() => this.loadUsers());
  }

  toggleValidation(user: User) {
    const updated = { ...user, estValide: !user.estValide };
    this.userService.updateUser(user.id, updated).subscribe(() => this.loadUsers());
  }
}
