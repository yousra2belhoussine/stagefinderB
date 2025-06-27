package ma.stagefinder.entities;

import ma.stagefinder.entities.enums.StatutCandidature;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_de_candidature")
    private LocalDateTime dateCandidature;

    // ✅ ==========================================================
    // ==     MODIFICATION: Valeur par défaut pour le statut     ==
    // ==========================================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // On s'assure qu'il n'est jamais null dans la BDD
    private StatutCandidature statutCandidature = StatutCandidature.EN_ATTENTE;


    @Column(name = "lettre_de_motivation")
    private String lettreMotivation;

    @Column(name = "cv_choisi")
    private String cvChoisi;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

}
