package ma.stagefinder.services;

import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Categorie;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.User;
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
import org.springframework.http.ResponseEntity;
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


  @Override
  public OffreDTO publierOffre(Offre offre, Long userId, Long categorieId) {
    try {
      // Vérifie d’abord que user et categorie existent
      User user = userRepository.findById(userId)
              .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
      Categorie categorie = categorieRepository.findById(categorieId)
              .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

      offre.setPubliePar(user);
      offre.setCategorie(categorie);
      Offre saved = offreRepository.save(offre);
      return entityMapper.toOffreDTO(saved);
    } catch (Exception e) {
      e.printStackTrace(); // ✅ pour voir dans la console la vraie erreur
      throw new RuntimeException("Erreur lors de la publication de l'offre : " + e.getMessage());
    }
  }

  @Override
  public Page<OffreDTO> getAllOffres(int page, int size, String typeCategorie, String ville) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Offre> offresPage;
    // Convertir typeCategorie (String) en TypeCategorie (enum)
    TypeCategorie typeCategorieEnum = null;
    if (typeCategorie != null && !typeCategorie.isEmpty()) {
      try {
        typeCategorieEnum = TypeCategorie.valueOf(typeCategorie.toUpperCase());
      } catch (IllegalArgumentException e) {
        // Si la catégorie n'existe pas dans l'enum, retourner une page vide
        return new PageImpl<>(List.of(), pageable, 0);
      }
    }

    // Logique de recherche
    if (typeCategorieEnum != null && (ville == null || ville.isEmpty())) {
      // Recherche uniquement par typeCategorie
      offresPage = offreRepository.findByTypeCategorie(typeCategorieEnum, pageable);
    } else if (ville != null && !ville.isEmpty() && typeCategorieEnum == null) {
      // Recherche uniquement par ville
      offresPage = offreRepository.findByVille(ville, pageable);
    } else if (typeCategorieEnum != null && ville != null && !ville.isEmpty()) {
      // Recherche par typeCategorie ou ville
      offresPage = offreRepository.findByTypeCategorieOrVille(typeCategorieEnum, ville, pageable);
    } else {
      // Aucun filtre : retourner toutes les offres
      offresPage = offreRepository.findAll(pageable);
    }
    List<OffreDTO> offresDTO = offresPage.stream()
      .map(entityMapper::toOffreDTO)
      .collect(Collectors.toList());
    return new PageImpl<>(offresDTO, pageable, offresPage.getTotalElements());
  }

  @Override
  public void deleteOffre(Long id) {
    Optional<Offre> optionalOffre = offreRepository.findById(id);
    if (optionalOffre.isEmpty()) {
      throw new RuntimeException("Offre non trouvée avec l'ID : " + id);
    }
    offreRepository.deleteById(id);
  }

  @Override
  public OffreDTO updateOffre(Long id, OffreDTO offreDTO) {
    Optional<Offre> optionalOffre = offreRepository.findById(id);
    if (optionalOffre.isEmpty()) {
      throw new RuntimeException("Offre non trouvée avec l'ID : " + id);
    }

    Offre offre = optionalOffre.get();

    // Journal pour vérifier les données reçues
    System.out.println("OffreDTO reçu pour mise à jour (ID: " + id + "):");
    System.out.println("  nomEntreprise = " + offreDTO.getNomEntreprise());
    System.out.println("  categorieId = " + offreDTO.getCategorieId());
    System.out.println("  description = " + offreDTO.getDescription());
    System.out.println("  ville = " + offreDTO.getVille());
    System.out.println("  anneesExperience = " + offreDTO.getAnneesExperience());
    System.out.println("  salaire = " + offreDTO.getSalaire());
    System.out.println("  competenceExigee = " + offreDTO.getCompetenceExigee());
    System.out.println("  datePublication = " + offreDTO.getDatePublication());
    System.out.println("  dateExpiration = " + offreDTO.getDateExpiration());
    System.out.println("  publieParId = " + offreDTO.getPublieParId());

    // Mettre à jour les champs de l'offre
    offre.setAnneesExperience(offreDTO.getAnneesExperience());
    offre.setDescription(offreDTO.getDescription());
    offre.setVille(offreDTO.getVille());
    offre.setSalaire(offreDTO.getSalaire());
    offre.setCompetenceExigee(offreDTO.getCompetenceExigee());
    offre.setDatePublication(offreDTO.getDatePublication());
    offre.setDateExpiration(offreDTO.getDateExpiration());
    String newNomEntreprise = offreDTO.getNomEntreprise();
    if (newNomEntreprise != null && !newNomEntreprise.trim().isEmpty()) {
      offre.setNomEntreprise(newNomEntreprise);
    } else {
      System.out.println("nomEntreprise est null ou vide, conservation de la valeur existante : " + offre.getNomEntreprise());
    }

    // Mettre à jour la catégorie si categorieId est fourni
    Long categorieId = offreDTO.getCategorieId();
    if (categorieId != null && categorieId > 0) {
      Optional<Categorie> optionalCategorie = categorieRepository.findById(categorieId);
      if (optionalCategorie.isEmpty()) {
        throw new RuntimeException("Catégorie non trouvée avec l'ID : " + categorieId);
      }
      offre.setCategorie(optionalCategorie.get());
      System.out.println("Catégorie mise à jour : ID = " + categorieId + ", Nom = " + optionalCategorie.get().getTypeCategorie());
    } else {
      System.out.println("Aucun categorieId fourni ou invalide, conservation de la catégorie existante : " + offre.getCategorie().getId());
    }

    // Mettre à jour l'utilisateur si publieParId est fourni
    Long userId = offreDTO.getPublieParId();
    if (userId != null) {
      Optional<User> optionalUser = userRepository.findById(userId);
      if (optionalUser.isEmpty()) {
        throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId);
      }
      offre.setPubliePar(optionalUser.get());
      System.out.println("Utilisateur mis à jour : ID = " + userId);
    }

    // Journal avant sauvegarde
    System.out.println("Avant sauvegarde (ID: " + id + "):");
    System.out.println("  nomEntreprise = " + offre.getNomEntreprise());
    System.out.println("  categorieId = " + (offre.getCategorie() != null ? offre.getCategorie().getId() : "null"));

    // Sauvegarder les modifications
    Offre updatedOffre = offreRepository.save(offre);

    // Journal après sauvegarde
    System.out.println("Après sauvegarde (ID: " + id + "):");
    System.out.println("  nomEntreprise = " + updatedOffre.getNomEntreprise());
    System.out.println("  categorieId = " + (updatedOffre.getCategorie() != null ? updatedOffre.getCategorie().getId() : "null"));

    // Convertir en DTO
    OffreDTO result = entityMapper.toOffreDTO(updatedOffre);

    // Journal pour le DTO retourné
    System.out.println("OffreDTO retourné (ID: " + id + "):");
    System.out.println("  nomEntreprise = " + result.getNomEntreprise());
    System.out.println("  categorieId = " + result.getCategorieId());
    System.out.println("  categorieNom = " + result.getCategorieNom());

    return result;
  }

  @Override
  public long countOffres() {
    return offreRepository.count();
  }

  @Override
  public OffreDTO getOffreById(Long id) {
    Optional<Offre> optionalOffre = offreRepository.findById(id);
    if (optionalOffre.isEmpty()) {
      throw new RuntimeException("Offre non trouvée avec l'ID : " + id);
    }
    return entityMapper.toOffreDTO(optionalOffre.get());
  }
}
