package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.stagefinder.entities.enums.Ville;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String ville;
    private Boolean preEmbauche;
    private String anneesExperience;
    @Column(name = "nom_entreprise")
    private String nomEntreprise;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    private float salaire;

    @Column(name = "competence_exigee")
    private String competenceExigee;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @JsonIgnore
    private Categorie categorie;

    //@OneToMany(mappedBy = "offre")
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)

    @JsonIgnore
    private List<Candidature> candidatures;



  //  @OneToMany(mappedBy = "offre")
  @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)

  @JsonIgnore
    private List<Favoris> favoris;

    @ManyToOne
    @JoinColumn(name = "publie_par_id")
    private User publiePar;
}
