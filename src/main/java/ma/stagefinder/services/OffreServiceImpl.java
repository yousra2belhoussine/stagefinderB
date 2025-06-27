package ma.stagefinder.services;

import ma.stagefinder.dtos.NotificationDTO;
import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Categorie;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.entities.enums.TypeCategorie;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.CategorieRepository;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OffreServiceImpl implements OffreService {

  @Autowired
  private OffreRepository offreRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CategorieRepository categorieRepository;
  @Autowired
  private EntityMapper entityMapper;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private EmailService emailService;
  @Autowired
  private WebSocketService webSocketService;

  @Override
  public OffreDTO publierOffre(Offre offre, Long userId, Long categorieId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId));
    Categorie categorie = categorieRepository.findById(categorieId)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + categorieId));

    offre.setPubliePar(user);
    offre.setCategorie(categorie);
    offre.setNomEntreprise(user.getNomEntreprise());

    if (offre.getDatePublication() == null) {
      offre.setDatePublication(LocalDateTime.now());
    }

    Offre savedOffre = offreRepository.save(offre);

    // Processus de notification complet
    try {
      List<User> stagiaires = userRepository.findByRole(Role.STAGIAIRE);

      for (User stagiaire : stagiaires) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage("Nouvelle offre publiée par " + user.getNomEntreprise() + " !");
        notificationDTO.setDateEnvoie(LocalDateTime.now());
        notificationDTO.setUserId(stagiaire.getId());
        notificationDTO.setIsRead(false);

        // 1. Enregistrer dans la DB
        NotificationDTO savedNotification = notificationService.addNotification(notificationDTO);

        // 2. Envoyer via WebSocket
        webSocketService.sendNotificationToUser(stagiaire.getEmail(), savedNotification);

        // 3. Envoyer par Email en arrière-plan
        String subject = "Nouvelle offre de stage: " + savedOffre.getCategorie().getTitre();
        String body = "Bonjour " + stagiaire.getNom() + ",\n\n"
                + "Une nouvelle offre qui pourrait vous intéresser a été publiée par "
                + savedOffre.getNomEntreprise() + ".\n\n"
                + "Consultez-la sur notre plateforme.\n\n"
                + "Cordialement,\nL'équipe StageFinder";
        emailService.sendNotificationEmail(stagiaire.getEmail(), subject, body);
      }
    } catch (Exception e) {
      System.err.println("Erreur lors du processus de notification complet : " + e.getMessage());
    }

    return entityMapper.toOffreDTO(savedOffre);
  }

  @Override
  public Page<OffreDTO> getAllOffres(int page, int size, String typeCategorie, String ville) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Offre> offres;

    if (typeCategorie != null && !typeCategorie.isEmpty() && ville != null && !ville.isEmpty()) {
      TypeCategorie type = TypeCategorie.valueOf(typeCategorie.toUpperCase());
      offres = offreRepository.findByTypeCategorieOrVille(type, ville, pageable);
    } else if (typeCategorie != null && !typeCategorie.isEmpty()) {
      TypeCategorie type = TypeCategorie.valueOf(typeCategorie.toUpperCase());
      offres = offreRepository.findByTypeCategorie(type, pageable);
    } else if (ville != null && !ville.isEmpty()) {
      offres = offreRepository.findByVille(ville, pageable);
    } else {
      offres = offreRepository.findAll(pageable);
    }

    List<OffreDTO> offreDTOs = offres.getContent().stream()
            .map(entityMapper::toOffreDTO)
            .collect(Collectors.toList());

    return new PageImpl<>(offreDTOs, pageable, offres.getTotalElements());
  }

  @Override
  public void deleteOffre(Long id) {
    if (!offreRepository.existsById(id)) {
      throw new RuntimeException("Offre non trouvée avec l'ID : " + id);
    }
    offreRepository.deleteById(id);
  }

  @Override
  public OffreDTO updateOffre(Long id, OffreDTO offreDTO) {
    Offre existingOffre = offreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'ID : " + id));

    if (offreDTO.getDescription() != null) existingOffre.setDescription(offreDTO.getDescription());
    if (offreDTO.getVille() != null) existingOffre.setVille(offreDTO.getVille());
    if (offreDTO.getAnneesExperience() != null) existingOffre.setAnneesExperience(offreDTO.getAnneesExperience());
    if (offreDTO.getDateExpiration() != null) existingOffre.setDateExpiration(offreDTO.getDateExpiration());
    if (offreDTO.getSalaire() > 0) existingOffre.setSalaire(offreDTO.getSalaire());
    if (offreDTO.getCompetenceExigee() != null) existingOffre.setCompetenceExigee(offreDTO.getCompetenceExigee());
    if (offreDTO.getPreEmbauche() != null) existingOffre.setPreEmbauche(offreDTO.getPreEmbauche());

    if (offreDTO.getCategorieId() != null && !offreDTO.getCategorieId().equals(existingOffre.getCategorie().getId())) {
      Categorie categorie = categorieRepository.findById(offreDTO.getCategorieId())
              .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + offreDTO.getCategorieId()));
      existingOffre.setCategorie(categorie);
    }

    Offre updatedOffre = offreRepository.save(existingOffre);
    return entityMapper.toOffreDTO(updatedOffre);
  }

  @Override
  public long countOffres() {
    return offreRepository.count();
  }

  @Override
  public OffreDTO getOffreById(Long id) {
    Offre offre = offreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'ID : " + id));
    return entityMapper.toOffreDTO(offre);
  }

  @Override
  public List<OffreDTO> findOffresByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    List<Offre> offres = offreRepository.findAllById(ids);
    return offres.stream()
            .map(entityMapper::toOffreDTO)
            .collect(Collectors.toList());
  }
}