package ma.stagefinder.mapper;

import ma.stagefinder.dtos.*;
import ma.stagefinder.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EntityMapper {

   // EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    // User
    UserDTO toUserDTO(User user);
    User toUser(UserDTO userDTO);

    // Categorie
    CategorieDTO toCategorieDTO(Categorie categorie);
    Categorie toCategorie(CategorieDTO categorieDTO);

    // Offre
    @Mapping(source = "categorie.typeCategorie", target = "categorieNom")
    @Mapping(source = "publiePar.nom", target = "publieParNom")
    @Mapping(source = "publiePar.nomEntreprise", target = "nomEntreprise")
    @Mapping(source = "publiePar.id", target = "publieParId")
    @Mapping(source = "categorie.id", target = "categorieId")
    OffreDTO toOffreDTO(Offre offre);

    @Mapping(source = "categorieId", target = "categorie.id")
    @Mapping(source = "publieParId", target = "publiePar.id")
    @Mapping(target = "categorie", ignore = true)
    @Mapping(target = "publiePar", ignore = true)
    Offre toOffre(OffreDTO offreDTO);

    // Notification
    @Mapping(source = "user.id", target = "userId")
    NotificationDTO toNotificationDTO(Notification notification);

    @Mapping(source = "userId", target = "user.id")
    Notification toNotification(NotificationDTO notificationDTO);

    // Favoris
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "offre.id", target = "offreId")
    FavorisDTO toFavorisDTO(Favoris favoris);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "offreId", target = "offre.id")
    Favoris toFavoris(FavorisDTO favorisDTO);

    // Candidature
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "offre.id", target = "offreId")
    CandidatureDTO toCandidatureDTO(Candidature candidature);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "offreId", target = "offre.id")
    Candidature toCandidature(CandidatureDTO candidatureDTO);

    // Avis
    @Mapping(source = "auteur.id", target = "auteurId")
    @Mapping(source = "destinataire.id", target = "destinataireId")
    @Mapping(source = "offre.id", target = "offreId")
    AvisDTO toAvisDTO(Avis avis);

    @Mapping(source = "auteurId", target = "auteur.id")
    @Mapping(source = "destinataireId", target = "destinataire.id")
    @Mapping(source = "offreId", target = "offre.id")
    Avis toAvis(AvisDTO avisDTO);
}
