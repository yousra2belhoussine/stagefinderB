package ma.stagefinder.services;

import ma.stagefinder.dtos.CandidatureDTO;
import ma.stagefinder.dtos.OffreDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface CandidatureService {
    CandidatureDTO createCandidature(CandidatureDTO candidatureDTO) throws IOException;
    Page<CandidatureDTO> getCandidaturesByOffre(Long offreId, int page, int size);
    CandidatureDTO updateCandidatureStatus(Long id, String statutCandidature);
    List<CandidatureDTO> getAppliedOffersByUser(Long userId);
}