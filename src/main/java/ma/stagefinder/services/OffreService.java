package ma.stagefinder.services;

import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Offre;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OffreService {
  OffreDTO publierOffre(Offre offre, Long userId, Long categorieId);
  Page<OffreDTO> getAllOffres(int page, int size, String typeCategorie, String ville);
  void deleteOffre(Long id);
  OffreDTO updateOffre(Long id, OffreDTO offreDTO);
  long countOffres();
  OffreDTO getOffreById(Long id);
  List<OffreDTO> findOffresByIds(List<Long> ids);

}
