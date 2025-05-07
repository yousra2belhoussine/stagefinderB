package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ma.stagefinder.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nom;
    @Column(unique = true)
    private String email;

    @Column(name = "nom_entreprise")
    private String nomEntreprise;
    @Column(unique = true, nullable = true)
     private String RC;
    @Column(unique = true, nullable = true)
     private String ICE;
    private String password;
    @Column(unique = true)
    private String tel;

    @Column(name = "cv_file")
    private String cvFile;

    @Column(name = "est_valide")
    private boolean estValide;

    private String adresse;
    private String image;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Candidature> candidatures;

    @OneToMany(mappedBy = "publiePar")
    @JsonIgnore
    private List<Offre> offres;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Favoris> favoris;

//    @OneToMany(mappedBy = "user")
//    private List<Abonnement> abonnements;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "auteur")
    @JsonIgnore
    private List<Avis> avisLaisses;

    @OneToMany(mappedBy = "destinataire")
    @JsonIgnore
    private List<Avis> avisRecus; // Les avis reçus par cet utilisateur (par exemple, sur une entreprise)
}
