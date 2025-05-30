export interface OffreDTO {
  id?: number;
  anneesExperience: string;
  description: string;
  ville: string;
  nomEntreprise: string;
  salaire: number;
  competenceExigee: string;
  datePublication: Date;
  dateExpiration: Date;
  publieParNom?: string;
  categorieNom?: string;
  publieParId: number;
  categorieId: number;
}
