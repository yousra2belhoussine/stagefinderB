package ma.stagefinder.services;

import lombok.RequiredArgsConstructor; // ✅ Ziyada jdida
import ma.stagefinder.entities.enums.StatutAbonnement; // ✅ Ziyada jdida
import ma.stagefinder.repositories.AbonnementRepository; // ✅ Ziyada jdida
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap; // ✅ Ziyada jdida
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor // ✅ B'blasset @Autowired w @AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final UserRepository userRepository;
  private final OffreRepository offreRepository;

  // ✅ Ziyada jdida: Injection du repository d'abonnement
  private final AbonnementRepository abonnementRepository;

  @Override
  public List<Map<String, Object>> getUsersRegisteredPerMonth() {
    return userRepository.findUsersRegisteredPerMonth();
  }

  @Override
  public List<Map<String, Object>> getJobPostingsPerMonth() {
    return offreRepository.findJobPostingsPerMonth();
  }

  @Override
  public List<Map<String, Object>> getMostPublishedCategories() {
    return offreRepository.findMostPublishedCategories();
  }

  // ✅ ==========================================================
  // ==     IMPLÉMENTATION DE LA MÉTHODE JDIDA                 ==
  // ==========================================================
  @Override
  public Map<String, Long> getAbonnementStatistiques() {
    // 1. On appelle le repository pour avoir les comptes
    long payants = abonnementRepository.countByStatut(StatutAbonnement.PAYE);
    long gratuits = abonnementRepository.countByStatut(StatutAbonnement.GRATUIT);
    long total = payants + gratuits;

    // 2. On crée la Map pour stocker les résultats
    Map<String, Long> statistiques = new HashMap<>();

    // 3. On met les résultats dans la Map
    statistiques.put("utilisateursPayants", payants);
    statistiques.put("utilisateursGratuits", gratuits);
    statistiques.put("totalUtilisateurs", total);

    // 4. On retourne la Map
    return statistiques;
  }
}
