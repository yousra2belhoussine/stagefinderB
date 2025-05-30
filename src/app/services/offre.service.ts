import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OffreDTO } from '../models/offre.model';
import { OffrePageResponse } from '../models/offre-page-response.model';

@Injectable({
  providedIn: 'root'
})
export class OffreService {
  private baseUrl = 'http://localhost:8080/api/offres'; // adapte selon ton backend

  constructor(private http: HttpClient) {}

  getOffres(page: number, size: number): Observable<OffrePageResponse> {
    return this.http.get<OffrePageResponse>(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  getOffreById(id: number): Observable<OffreDTO> {
    return this.http.get<OffreDTO>(`${this.baseUrl}/${id}`);
  }

  addOffre(offre: OffreDTO): Observable<any> {
    return this.http.post(
    `${this.baseUrl}/publier?userId=${offre.publieParId}&categorieId=${offre.categorieId}`,


      offre
    );
  }

  updateOffre(id: number, offre: OffreDTO): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, offre);
  }

  deleteOffre(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }


}
