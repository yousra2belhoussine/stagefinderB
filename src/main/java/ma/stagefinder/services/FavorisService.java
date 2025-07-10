package ma.stagefinder.services;

import ma.stagefinder.dtos.FavorisDTO;
import ma.stagefinder.dtos.OffreDTO; // <-- 1. ZEDNA IMPORT J'DID

import java.util.List;

public interface FavorisService {
  FavorisDTO ajouterFavoris(FavorisDTO dto);
  void supprimerFavoris(Long id);

  // ✅ CORRECTION: La méthode retourne maintenant une liste d'offres (OffreDTO)
  List<OffreDTO> getFavorisByUser(Long userId);
}
