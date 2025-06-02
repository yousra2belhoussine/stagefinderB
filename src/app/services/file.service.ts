import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = 'http://localhost:8080/api/files';

  constructor(private http: HttpClient) {}

  /**
   * 🔓 Ouvre un fichier PDF/image dans un nouvel onglet sécurisé via JWT
   * Utilise Blob pour contourner les problèmes de sécurité de window.open direct.
   */
  openFileInNewTab(fileName: string): void {
    const token = localStorage.getItem('access_token');
    if (!token) {
      console.error('Aucun token JWT trouvé');
      return;
    }

    this.http.get(`${this.apiUrl}/${fileName}`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      }),
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const blobUrl = window.URL.createObjectURL(blob);
        window.open(blobUrl, '_blank');
      },
      error: (err) => {
        console.error(`Erreur lors de l'ouverture du fichier "${fileName}"`, err);
      }
    });
  }

  /**
   * 💾 Télécharge un fichier sécurisé avec JWT.
   */
  downloadFile(fileName: string): void {
    const token = localStorage.getItem('access_token');
    if (!token) {
      console.error('Aucun token JWT trouvé');
      return;
    }

    this.http.get(`${this.apiUrl}/${fileName}`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      }),
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error(`Erreur lors du téléchargement du fichier "${fileName}"`, err);
      }
    });
  }

  /**
   * 🖼️ Récupère une image (logo utilisateur) sécurisée, et retourne son URL local blob.
   * Utilisable directement dans un [src] d’image Angular.
   */
  getImageBlobUrl(fileName: string): Promise<string | null> {
    const token = localStorage.getItem('access_token');
    if (!token) return Promise.resolve(null);

    return this.http.get(`${this.apiUrl}/${fileName}`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      }),
      responseType: 'blob'
    }).toPromise()
      .then(blob => {
        return blob ? URL.createObjectURL(blob) : null;
      })
      .catch((err) => {
        console.error(`Erreur lors du chargement de l'image "${fileName}"`, err);
        return null;
      });
  }
}
