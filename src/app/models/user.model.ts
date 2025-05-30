export interface User {
  id: number;
  nom: string;
  email: string;
  password?: string;
  tel: string;
  adresse?: string;
  image?: string;
  cvFile?: string;
  estValide?: boolean;
  nomEntreprise?: string;
  RC?: string;
  ICE?: string;
  role: 'STAGIAIRE' | 'RECRUTEUR' | 'ADMINISTRATEUR';
}
export type NewUser = Omit<User, 'id'>;
