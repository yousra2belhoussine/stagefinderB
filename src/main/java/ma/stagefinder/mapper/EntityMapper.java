package ma.stagefinder.mapper;

import ma.stagefinder.dtos.*;
import ma.stagefinder.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityMapper {

  EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

  // User Mappings
  UserDTO toUserDTO(User user);
  User toUser(UserDTO userDTO);

  // Categorie Mappings
  CategorieDTO toCategorieDTO(Categorie categorie);
  Categorie toCategorie(CategorieDTO categorieDTO);

  // Offre Mappings
  @Mapping(source = "categorie.titre", target = "categorieNom")
  @Mapping(source = "publiePar.nom", target = "publieParNom")
  @Mapping(source = "publiePar.nomEntreprise", target = "nomEntreprise")
  @Mapping(source = "publiePar.id", target = "publieParId")
  @Mapping(source = "categorie.id", target = "categorieId")
  @Mapping(source = "preEmbauche", target = "preEmbauche")
  OffreDTO toOffreDTO(Offre offre);

  @Mapping(source = "categorieId", target = "categorie.id")
  @Mapping(source = "publieParId", target = "publiePar.id")
  @Mapping(target = "categorie", ignore = true)
  @Mapping(target = "publiePar", ignore = true)
  Offre toOffre(OffreDTO offreDTO);

  // Notification Mappings
  @Mapping(source = "user.id", target = "userId")
  NotificationDTO toNotificationDTO(Notification notification);
  Notification toNotification(NotificationDTO notificationDTO);

  // Favoris Mappings
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "offre.id", target = "offreId")
  FavorisDTO toFavorisDTO(Favoris favoris);
  Favoris toFavoris(FavorisDTO favorisDTO);


  // ✅ ==========================================================
  // ==     CORRECTION PRINCIPALE: MAPPING POUR CANDIDATURE      ==
  // ==========================================================
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "offre.id", target = "offreId")
  @Mapping(source = "offre", target = "offre")
  @Mapping(source = "user.nom", target = "userNom")
  @Mapping(source = "user.email", target = "userEmail")
  @Mapping(source = "cvChoisi", target = "cvFileName")
  @Mapping(source = "lettreMotivation", target = "lettreMotivationFileName")
  @Mapping(source = "statutCandidature", target = "statutCandidature")
  // On ignore les champs MultipartFile lors de la conversion de l'Entité vers le DTO
  @Mapping(target = "cvChoisi", ignore = true)
  @Mapping(target = "lettreMotivation", ignore = true)
  CandidatureDTO toCandidatureDTO(Candidature candidature);

  // La conversion inverse est correcte, on ignore déjà les champs
  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "offreId", target = "offre.id")
  @Mapping(target = "cvChoisi", ignore = true)
  @Mapping(target = "lettreMotivation", ignore = true)
  Candidature toCandidature(CandidatureDTO candidatureDTO);

  // Avis Mappings
  @Mapping(source = "auteur.id", target = "auteurId")
  @Mapping(source = "destinataire.id", target = "destinataireId")
  AvisDTO toAvisDTO(Avis avis);
  Avis toAvis(AvisDTO avisDTO);

  // Abonnement Mappings
  AbonnementDTO toAbonnementDTO(Abonnement abonnement);
  Abonnement toAbonnement(AbonnementDTO abonnementDTO);
}
