





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

  loadUsers() {
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
    if (user) {
      this.newUser = { ...user };
      this.isEditMode = true;
    } else {
      this.newUser = this.initUser();
      this.isEditMode = false;
      this.cvFile = undefined;
      this.imageFile = undefined;
    }
    this.displayModal = true;
  }

  saveUser() {
    if (this.isEditMode && 'id' in this.newUser) {
      const userToUpdate = this.newUser as User;
      this.userService.updateUser(userToUpdate.id, userToUpdate).subscribe(() => this.loadUsers());
    } else {
      if (!this.newUser.cvFile) this.newUser.cvFile = '';
      if (!this.newUser.image) this.newUser.image = '';

      const formData = new FormData();
      formData.append('user', new Blob([JSON.stringify(this.newUser)], { type: 'application/json' }));

      if (this.cvFile) formData.append('cv', this.cvFile);
      if (this.imageFile) formData.append('image', this.imageFile);

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
