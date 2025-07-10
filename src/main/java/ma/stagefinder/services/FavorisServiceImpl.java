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
import org.springframework.stereotype.Service;

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
  public FavorisDTO ajouterFavoris(FavorisDTO dto) {
    User user = userRepository.findById(dto.getUserId())
      .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    Offre offre = offreRepository.findById(dto.getOffreId())
      .orElseThrow(() -> new RuntimeException("Offre introuvable"));

    Favoris favoris = mapper.toFavoris(dto);
    favoris.setDateAjout(LocalDateTime.now());
    favoris.setUser(user);
    favoris.setOffre(offre);

    return mapper.toFavorisDTO(favorisRepository.save(favoris));
  }

  @Override
  public void supprimerFavoris(Long id) {
    Favoris favoris = favorisRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Favori introuvable"));
    favorisRepository.delete(favoris);
  }

  @Override
  public FavorisDTO getFavorisById(Long id) {
    Favoris favoris = favorisRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Favori introuvable"));
    return mapper.toFavorisDTO(favoris);
  }

  @Override
  public List<FavorisDTO> getFavorisByUser(Long userId) {
    return favorisRepository.findByUserId(userId).stream()
      .map(mapper::toFavorisDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<FavorisDTO> getFavorisByOffre(Long offreId) {
    return favorisRepository.findByOffreId(offreId).stream()
      .map(mapper::toFavorisDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<FavorisDTO> getAllFavoris() {
    return favorisRepository.findAll().stream()
      .map(mapper::toFavorisDTO)
      .collect(Collectors.toList());
  }
}
