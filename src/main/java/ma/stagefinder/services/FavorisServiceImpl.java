package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.FavorisDTO;
import ma.stagefinder.dtos.OffreDTO; // <-- IMPORT J'DID
import ma.stagefinder.entities.Favoris;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.User;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.FavorisRepository;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavorisServiceImpl implements FavorisService {

  private final FavorisRepository favorisRepository;
  private final UserRepository userRepository;
  private final OffreRepository offreRepository;
  private final EntityMapper mapper;

  @Override
  @Transactional
  // ✅ CacheEvict: Melli n'zid chi favori, kan mes7o l'cache dyal had l'utilisateur
  @CacheEvict(value = "userFavorites", key = "#dto.userId")
  public FavorisDTO ajouterFavoris(FavorisDTO dto) {
    User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    Offre offre = offreRepository.findById(dto.getOffreId())
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));

    Favoris favoris = new Favoris();
    favoris.setDateAjout(LocalDateTime.now());
    favoris.setUser(user);
    favoris.setOffre(offre);

    Favoris savedFavoris = favorisRepository.save(favoris);
    return mapper.toFavorisDTO(savedFavoris);
  }

  @Override
  @Transactional
  // ✅ CacheEvict: Melli n'supprimiw chi favori, kan mes7o l'cache dyal kolchi.
  // Hada 7el sehel o mdmoun.
  @CacheEvict(value = "userFavorites", allEntries = true)
  public void supprimerFavoris(Long id) {
    Favoris favoris = favorisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Favori introuvable"));
    favorisRepository.delete(favoris);
  }

  @Override
  @Transactional(readOnly = true)
  // ✅ Cacheable: L'merra l'wla ghadi tjib la liste dyal les Offres o t'khebbiha f Redis.
  @Cacheable(value = "userFavorites", key = "#userId")
  public List<OffreDTO> getFavorisByUser(Long userId) {
    // 1. On récupère la liste des entités Favoris
    List<Favoris> favorisList = favorisRepository.findByUserId(userId);

    // 2. On extrait uniquement les entités Offre de chaque favori
    List<Offre> offresFavorites = favorisList.stream()
            .map(Favoris::getOffre)
            .collect(Collectors.toList());

    // 3. On convertit la liste d'entités Offre en une liste de DTOs
    return offresFavorites.stream()
            .map(mapper::toOffreDTO)
            .collect(Collectors.toList());
  }
}
