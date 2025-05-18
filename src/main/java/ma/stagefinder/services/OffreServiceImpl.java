package ma.stagefinder.services;

import ma.stagefinder.dtos.NotificationDTO;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Override
    public OffreDTO publierOffre(Offre offre, Long userId, Long categorieId) {
        // Vérifier si l'utilisateur existe
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId);
        }
        User user = optionalUser.get();

        // Vérifier si la catégorie existe
        Optional<Categorie> optionalCategorie = categorieRepository.findById(categorieId);
        if (optionalCategorie.isEmpty()) {
            throw new RuntimeException("Catégorie non trouvée avec l'ID : " + categorieId);
        }
        Categorie categorie = optionalCategorie.get();

        // Associer l'utilisateur et la catégorie à l'offre
        offre.setPubliePar(user);
        offre.setCategorie(categorie);
        offre.setNomEntreprise(user.getNomEntreprise());

        // Définir la date de publication si non fournie
        if (offre.getDatePublication() == null) {
            offre.setDatePublication(LocalDateTime.now());
        }

        // Sauvegarder l'offre
        Offre savedOffre = offreRepository.save(offre);

        try {
            List<Long> stagiaireIds = Arrays.asList(1L, 2L);

            for (Long stagiaireId : stagiaireIds) {
                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setMessage("Nouvelle offre publiée par " + user.getNomEntreprise() + " !");
                notificationDTO.setDateEnvoie(LocalDateTime.now());
                notificationDTO.setUserId(stagiaireId);
                notificationService.addNotification(notificationDTO);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création des notifications : " + e.getMessage());
        }

        // Convertir en DTO et retourner
        return entityMapper.toOffreDTO(savedOffre);
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

        // Mettre à jour les champs de l'offre
        offre.setAnneesExperience(offreDTO.getAnneesExperience());
        offre.setDescription(offreDTO.getDescription());
        offre.setVille(offreDTO.getVille());
        offre.setSalaire(offreDTO.getSalaire());
        offre.setCompetenceExigee(offreDTO.getCompetenceExigee());
        offre.setDatePublication(offreDTO.getDatePublication());
        offre.setDateExpiration(offreDTO.getDateExpiration());

//        // Mettre à jour la catégorie si categorieId est fourni
//        if (categorieId != null) {
//            Optional<Categorie> optionalCategorie = categorieRepository.findById(categorieId);
//            if (optionalCategorie.isEmpty()) {
//                throw new RuntimeException("Catégorie non trouvée avec l'ID : " + categorieId);
//            }
//            offre.setCategorie(optionalCategorie.get());
//        }
//
//        // Mettre à jour l'utilisateur si userId est fourni
//        if (userId != null) {
//            Optional<User> optionalUser = userRepository.findById(userId);
//            if (optionalUser.isEmpty()) {
//                throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + userId);
//            }
//            offre.setPubliePar(optionalUser.get());
//            offre.setNomEntreprise(optionalUser.get().getNomEntreprise());
//        }

        // Sauvegarder les modifications
        Offre updatedOffre = offreRepository.save(offre);
        return entityMapper.toOffreDTO(updatedOffre);

    }
}