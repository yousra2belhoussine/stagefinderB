package ma.stagefinder.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OffreDTO {
    private Long id;
    private String description;
    private String ville;
    private String anneesExperience;
    private LocalDateTime datePublication;
    private LocalDateTime dateExpiration;
    private float salaire;
    private String competenceExigee;

    // ✅ ==========================================================
    // ==     ZIYADA JDIDA: Champ pour l'affichage premium       ==
    // ==========================================================
    private Boolean preEmbauche;


    // Les champs qui restent les mêmes
    private String categorieNom;
    private String publieParNom;
    private String nomEntreprise;
    private Long categorieId;
    private Long publieParId;
}
