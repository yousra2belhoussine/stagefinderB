package ma.stagefinder.services;

import java.util.Set;

public interface FavorisService {

  /**
   * Ajoute une offre aux favoris d'un utilisateur.
   * @param userId L'ID de l'utilisateur
   * @param offreId L'ID de l'offre
   */
  void ajouterFavoris(Long userId, Long offreId);

  /**
   * Supprime une offre des favoris d'un utilisateur.
   * @param userId L'ID de l'utilisateur
   * @param offreId L'ID de l'offre
   */
  void supprimerFavoris(Long userId, Long offreId);

  /**
   * Récupère tous les IDs des offres favorites d'un utilisateur.
   * @param userId L'ID de l'utilisateur
   * @return Un Set de Strings contenant les IDs des offres.
   */
  Set<String> getFavorisByUser(Long userId);

}