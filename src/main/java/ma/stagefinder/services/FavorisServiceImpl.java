package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.FavorisDTO;
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
  // bach l'merra jaya i'jib la liste j'dida.
  @CacheEvict(value = "userFavorites", key = "#dto.userId")
  public FavorisDTO ajouterFavoris(FavorisDTO dto) {
    User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    Offre offre = offreRepository.findById(dto.getOffreId())
            .orElseThrow(() -> new RuntimeException("Offre introuvable"));

    Favoris favoris = new Favoris(); // On crée une nouvelle instance
    favoris.setDateAjout(LocalDateTime.now());
    favoris.setUser(user);
    favoris.setOffre(offre);

    Favoris savedFavoris = favorisRepository.save(favoris);
    return mapper.toFavorisDTO(savedFavoris);
  }

  @Override
  @Transactional
  // ✅ CacheEvict: Melli n'supprimiw chi favori, kan mes7o l'cache dyal l'utilisateur
  // li kanet 3ndo had l'favori.
  @CacheEvict(value = "userFavorites", key = "#favoris.user.id")
  public void supprimerFavoris(Long id) {
    Favoris favoris = favorisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Favori introuvable"));
    favorisRepository.delete(favoris);
  }

  @Override
  @Transactional(readOnly = true) // Mzyan l les opérations dyal l'9raya
  // ✅ Cacheable: L'merra l'wla ghadi tjib la liste men PostgreSQL o t'khebbiha f Redis.
  // L'merrat jaya, ghadi tjibha direct men Redis.
  @Cacheable(value = "userFavorites", key = "#userId")
  public List<FavorisDTO> getFavorisByUser(Long userId) {
    return favorisRepository.findByUserId(userId).stream()
            .map(mapper::toFavorisDTO)
            .collect(Collectors.toList());
  }
}
