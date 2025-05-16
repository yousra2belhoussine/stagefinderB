import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = 'http://localhost:8080/api/files';

  constructor(private http: HttpClient) {}

  downloadFile(filename: string): void {
    const token = localStorage.getItem('access_token');
    if (!token) {
      console.error('Aucun token JWT trouvé');
      return;
    }

    this.http.get(`${this.apiUrl}/${filename}`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      }),
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      error: (err) => {
        console.error(`Erreur lors du téléchargement du fichier "${filename}"`, err);
      }
    });
  }

  getImageBlobUrl(fileName: string): Promise<string | null> {
    const token = localStorage.getItem('access_token');
    if (!token) return Promise.resolve(null);

    return this.http.get(`${this.apiUrl}/${fileName}`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      }),
      responseType: 'blob'
    }).toPromise().then(blob => {
      if (!blob) return null;
      return URL.createObjectURL(blob);
    }).catch((err) => {
      console.error(`Erreur lors du chargement de l'image "${fileName}"`, err);
      return null;
    });
  }
}
