package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.CandidatureDTO;
import ma.stagefinder.entities.Abonnement;
import ma.stagefinder.entities.Candidature;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.StatutAbonnement;
import ma.stagefinder.entities.enums.StatutCandidature;
import ma.stagefinder.exceptions.PremiumOfferException;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.AbonnementRepository;
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
@RequiredArgsConstructor
public class CandidatureServiceImpl implements CandidatureService {

  private final CandidatureRepository candidatureRepository;
  private final UserRepository userRepository;
  private final OffreRepository offreRepository;
  private final EntityMapper entityMapper;
  private final FileStorageService fileStorageService;
  private final AbonnementRepository abonnementRepository;

  @Override
  public CandidatureDTO createCandidature(CandidatureDTO candidatureDTO) throws IOException {

    User user = userRepository.findById(candidatureDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + candidatureDTO.getUserId()));

    Offre offre = offreRepository.findById(candidatureDTO.getOffreId())
            .orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'ID : " + candidatureDTO.getOffreId()));

    // ✅ ======================================================================
    // ==     CORRECTION: On utilise le getter offre.getPreEmbauche()        ==
    // ======================================================================
    if (offre.getPreEmbauche() != null && offre.getPreEmbauche()) {
      Abonnement abonnement = abonnementRepository.findByUserId(user.getId())
              .orElseThrow(() -> new RuntimeException("Abonnement non trouvé pour l'utilisateur: " + user.getEmail()));

      if (abonnement.getStatut() == StatutAbonnement.GRATUIT) {
        throw new PremiumOfferException("Vous devez avoir un abonnement payant pour postuler à cette offre premium.");
      }
    }

    // Le reste du code ne change pas
    Candidature candidature = entityMapper.toCandidature(candidatureDTO);
    candidature.setUser(user);
    candidature.setOffre(offre);

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

  // ... (le reste des méthodes ne changent pas)

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
