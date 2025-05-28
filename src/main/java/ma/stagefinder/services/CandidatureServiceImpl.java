package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.CandidatureDTO;
import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Candidature;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.StatutCandidature;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.CandidatureRepository;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CandidatureServiceImpl implements CandidatureService {

  private CandidatureRepository candidatureRepository;
  private UserRepository userRepository;
  private OffreRepository offreRepository;
  private EntityMapper entityMapper;
  private FileStorageService fileStorageService;

  @Override
  public CandidatureDTO createCandidature(CandidatureDTO candidatureDTO) throws IOException {
    Candidature candidature = entityMapper.toCandidature(candidatureDTO);

    Optional<User> userOptional = userRepository.findById(candidatureDTO.getUserId());
    if (userOptional.isEmpty()) {
      throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + candidatureDTO.getUserId());
    }
    candidature.setUser(userOptional.get());

    Optional<Offre> offreOptional = offreRepository.findById(candidatureDTO.getOffreId());
    if (offreOptional.isEmpty()) {
      throw new RuntimeException("Offre non trouvée avec l'ID : " + candidatureDTO.getOffreId());
    }
    candidature.setOffre(offreOptional.get());

    if (candidatureDTO.getCvChoisi() != null && !candidatureDTO.getCvChoisi().isEmpty()) {
      String cvFilename = fileStorageService.storeFile(candidatureDTO.getCvChoisi(), "cvChoisi");
      candidature.setCvChoisi(cvFilename);
    }
    if (candidatureDTO.getLettreMotivation() != null && !candidatureDTO.getLettreMotivation().isEmpty()) {
      String lettreFilename = fileStorageService.storeFile(candidatureDTO.getLettreMotivation(), "lettreMotivation");
      candidature.setLettreMotivation(lettreFilename);
    }

    candidature.setDateCandidature(LocalDateTime.now());
    Candidature savedCandidature = candidatureRepository.save(candidature);
    return entityMapper.toCandidatureDTO(savedCandidature);
  }

  @Override
  public Page<CandidatureDTO> getCandidaturesByOffre(Long offreId, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    Page<Candidature> candidaturesPage = candidatureRepository.findByOffreId(offreId, pageRequest);
    List<CandidatureDTO> candidaturesDTO = candidaturesPage.stream()
      .map(entityMapper::toCandidatureDTO)
      .collect(Collectors.toList());
    return new PageImpl<>(candidaturesDTO, pageRequest, candidaturesPage.getTotalElements());
  }

  @Override
  public CandidatureDTO updateCandidatureStatus(Long id, String statutCandidature) {
    Optional<Candidature> candidatureOptional = candidatureRepository.findById(id);
    if (candidatureOptional.isEmpty()) {
      throw new RuntimeException("Candidature non trouvée avec l'ID : " + id);
    }
    Candidature candidature = candidatureOptional.get();
    try {
      candidature.setStatutCandidature(StatutCandidature.valueOf(statutCandidature));
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Statut de candidature invalide : " + statutCandidature);
    }
    Candidature updatedCandidature = candidatureRepository.save(candidature);
    return entityMapper.toCandidatureDTO(updatedCandidature);
  }

  @Override
  public List<CandidatureDTO> getAppliedOffersByUser(Long userId) {
    List<Candidature> candidatures = candidatureRepository.findByUserId(userId);
    return candidatures.stream()
      .filter(candidature -> candidature.getOffre() != null)
      .map(entityMapper::toCandidatureDTO)
      .collect(Collectors.toList());
  }
}
