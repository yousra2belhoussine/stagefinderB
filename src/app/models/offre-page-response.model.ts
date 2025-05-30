import { OffreDTO } from './offre.model';

export interface OffrePageResponse {
  offres: OffreDTO[];
  totalItems: number;
  currentPage: number;
  totalPages: number;
}
