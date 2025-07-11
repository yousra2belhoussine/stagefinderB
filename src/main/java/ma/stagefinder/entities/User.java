package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ma.stagefinder.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
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
  private Boolean estValide;

  private String adresse;

  private String image;
//ajoute pour db
  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Candidature> candidatures;

  //@OneToMany(mappedBy = "publiePar")
  @OneToMany(mappedBy = "publiePar", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Offre> offres;

  //@OneToMany(mappedBy = "user")
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Favoris> favoris;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Notification> notifications;


  //  Ajouté pour cascade delete automatique des tokens
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Token> tokens;

}
