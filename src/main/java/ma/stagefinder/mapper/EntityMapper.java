package ma.stagefinder.mapper;

import ma.stagefinder.dtos.*;
import ma.stagefinder.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityMapper {

  EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

  // --- User Mappings ---
  UserDTO toUserDTO(User user);
  User toUser(UserDTO userDTO);

  // --- Categorie Mappings ---
  CategorieDTO toCategorieDTO(Categorie categorie);
  Categorie toCategorie(CategorieDTO categorieDTO);

  // --- Offre Mappings (Version améliorée) ---
  @Mapping(source = "categorie.titre", target = "categorieNom")
  @Mapping(source = "publiePar.nom", target = "publieParNom")
  @Mapping(source = "publiePar.nomEntreprise", target = "nomEntreprise")
  @Mapping(source = "publiePar.id", target = "publieParId")
  @Mapping(source = "categorie.id", target = "categorieId")
  OffreDTO toOffreDTO(Offre offre);

  @Mapping(source = "categorieId", target = "categorie.id")
  @Mapping(source = "publieParId", target = "publiePar.id")
  Offre toOffre(OffreDTO offreDTO);

  // --- Notification Mappings ---
  @Mapping(source = "user.id", target = "userId")
  NotificationDTO toNotificationDTO(Notification notification);

  @Mapping(source = "userId", target = "user.id")
  Notification toNotification(NotificationDTO notificationDTO);

  // --- Favoris Mappings ---
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "offre.id", target = "offreId")
  FavorisDTO toFavorisDTO(Favoris favoris);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "offreId", target = "offre.id")
  Favoris toFavoris(FavorisDTO favorisDTO);

  // --- Candidature Mappings (Version améliorée) ---
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "offre.id", target = "offreId")
  @Mapping(source = "offre", target = "offre")
  @Mapping(source = "user.nom", target = "userNom")
  @Mapping(source = "user.email", target = "userEmail")
  @Mapping(source = "cvChoisi", target = "cvFileName")
  @Mapping(source = "lettreMotivation", target = "lettreMotivationFileName")
  @Mapping(source = "statutCandidature", target = "statutCandidature")
  @Mapping(target = "cvChoisi", ignore = true)
  @Mapping(target = "lettreMotivation", ignore = true)
  CandidatureDTO toCandidatureDTO(Candidature candidature);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "offreId", target = "offre.id")
  @Mapping(target = "cvChoisi", ignore = true)
  @Mapping(target = "lettreMotivation", ignore = true)
  Candidature toCandidature(CandidatureDTO candidatureDTO);

  // Les mappings pour Avis et Abonnement ont été supprimés car les entités n'existent plus.
}
