package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.AvisDTO;
import ma.stagefinder.entities.Avis;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.User;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.AvisRepository;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvisServiceImpl implements AvisService {

  private final AvisRepository avisRepository;
  private final UserRepository userRepository;
  private final OffreRepository offreRepository;
  private final EntityMapper mapper;

  @Override
  public AvisDTO createAvis(AvisDTO dto) {
    // Vérifier s'il existe déjà un avis pour cette combinaison
    Optional<Avis> existingAvis = avisRepository.findByAuteurIdAndDestinataireIdAndOffreId(
      dto.getAuteurId(), dto.getDestinataireId(), dto.getOffreId());

    if (existingAvis.isPresent()) {
      throw new RuntimeException("Vous avez déjà laissé un avis pour cette offre et ce recruteur.");
    }
    Avis avis = mapper.toAvis(dto);
    avis.setDatePublication(LocalDateTime.now());

    User auteur = userRepository.findById(dto.getAuteurId())
      .orElseThrow(() -> new RuntimeException("Auteur introuvable"));
    User destinataire = userRepository.findById(dto.getDestinataireId())
      .orElseThrow(() -> new RuntimeException("Destinataire introuvable"));
    Offre offre = offreRepository.findById(dto.getOffreId())
      .orElseThrow(() -> new RuntimeException("Offre introuvable"));

    avis.setAuteur(auteur);
    avis.setDestinataire(destinataire);
    avis.setOffre(offre);

    return mapper.toAvisDTO(avisRepository.save(avis));
  }

  @Override
  public AvisDTO getAvisById(Long id) {
    Avis avis = avisRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Avis introuvable"));
    return mapper.toAvisDTO(avis);
  }

  @Override
  public List<AvisDTO> getAllAvis() {
    return avisRepository.findAll().stream()
      .map(mapper::toAvisDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<AvisDTO> getAvisByAuteur(Long auteurId) {
    return avisRepository.findByAuteurId(auteurId).stream()
      .map(mapper::toAvisDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<AvisDTO> getAvisByDestinataire(Long destinataireId) {
    return avisRepository.findByDestinataireId(destinataireId).stream()
      .map(mapper::toAvisDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<AvisDTO> getAvisByOffre(Long offreId) {
    return avisRepository.findByOffreId(offreId).stream()
      .map(mapper::toAvisDTO)
      .collect(Collectors.toList());
  }

  @Override
  public AvisDTO updateAvis(Long id, AvisDTO dto) {
    Avis avis = avisRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Avis introuvable"));

    avis.setCommentaire(dto.getCommentaire());
    avis.setNote(dto.getNote());
    avis.setDatePublication(LocalDateTime.now());

    return mapper.toAvisDTO(avisRepository.save(avis));
  }

  @Override
  public void deleteAvis(Long id) {
    Avis avis = avisRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Avis introuvable"));
    avisRepository.delete(avis);
  }
}
