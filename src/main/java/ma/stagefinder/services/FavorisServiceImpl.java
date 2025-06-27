package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavorisServiceImpl implements FavorisService
{

  // ZEDNA HADA : L-Outil dyal Redis
  private final RedisTemplate<String, String> redisTemplate;

  // KHELLINA HADO L-VALIDATION :
  private final UserRepository userRepository;
  private final OffreRepository offreRepository;

  // Helper method bach nssahlo l-khedma w manb9awch n3awdo l-code
  private String getKeyForUserFavorites(Long userId) {
    return "user:" + userId + ":favorites";
  }


  /**
   * Méthode Jdida : Ajoute une offre aux favoris d'un utilisateur dans Redis.
   * @param userId L'ID de l'utilisateur
   * @param offreId L'ID de l'offre à ajouter
   */
  public void ajouterFavoris(Long userId, Long offreId) {
    // 1. Kan-vérifiw anaho l'user w l'offre kaynin bse7 9bel mandiro ay 7aja
    if (!userRepository.existsById(userId)) {
      throw new RuntimeException("Utilisateur introuvable avec l'ID: " + userId);
    }
    if (!offreRepository.existsById(offreId)) {
      throw new RuntimeException("Offre introuvable avec l'ID: " + offreId);
    }

    // 2. Kanṣawbo l-clé dyal Redis
    String key = getKeyForUserFavorites(userId);

    // 3. Kanst3mlo RedisTemplate bach n'executiw SADD
    redisTemplate.opsForSet().add(key, String.valueOf(offreId));
  }


  /**
   * Méthode Jdida : Supprime une offre des favoris d'un utilisateur dans Redis.
   * @param userId L'ID de l'utilisateur
   * @param offreId L'ID de l'offre à supprimer
   */
  public void supprimerFavoris(Long userId, Long offreId) {
    // Kanṣawbo l-clé dyal Redis
    String key = getKeyForUserFavorites(userId);

    // Kanst3mlo RedisTemplate bach n'executiw SREM
    redisTemplate.opsForSet().remove(key, String.valueOf(offreId));
  }


  /**
   * Méthode Jdida : Récupère la liste des IDs des offres favorites pour un utilisateur.
   * @param userId L'ID de l'utilisateur
   * @return Un Set<String> contenant les IDs des offres favorites.
   */
  public Set<String> getFavorisByUser(Long userId) {
    // Kanṣawbo l-clé dyal Redis
    String key = getKeyForUserFavorites(userId);

    // Kanst3mlo RedisTemplate bach n'executiw SMEMBERS
    return redisTemplate.opsForSet().members(key);
  }

  // GA3 LES MÉTHODES LOKHRIN T-SUPPRIMAW.
}