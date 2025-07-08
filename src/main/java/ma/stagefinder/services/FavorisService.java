package ma.stagefinder.services;

import ma.stagefinder.dtos.FavorisDTO;

import java.util.List;

public interface FavorisService {
  FavorisDTO ajouterFavoris(FavorisDTO dto);
  void supprimerFavoris(Long id);
  List<FavorisDTO> getFavorisByUser(Long userId);
}
