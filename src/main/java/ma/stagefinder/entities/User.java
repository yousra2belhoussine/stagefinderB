package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ma.stagefinder.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Le nom est obligatoire")
  @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
  private String nom;

  @Column(unique = true)
  @NotBlank(message = "L'email est obligatoire")
  @Email(message = "Format d'email invalide")
  private String email;

  @Column(name = "nom_entreprise")
  @Size(max = 100, message = "Le nom de l'entreprise ne peut pas dépasser 100 caractères")
  private String nomEntreprise;

  @Column(nullable = true)
  @Size(max = 20, message = "Le RC ne peut pas dépasser 20 caractères")
  private String RC;

  @Column(nullable = true)
  @Size(max = 10, message = "L'ICE ne peut pas dépasser 20 caractères")
  private String ICE;

  @NotBlank(message = "Le mot de passe est obligatoire")
  private String password;

  @Column(unique = true)
  @NotBlank(message = "Le téléphone est obligatoire")
  @Pattern(regexp = "^(06|07)\\d{8}$",
          message = "Le téléphone doit commencer par 06 ou 07 et contenir 10 chiffres")
  private String tel;

  @Column(name = "cv_file")
  private String cvFile;

  @Column(name = "est_valide")
  private Boolean estValide;

  @Column(name = "verified_at")
  private LocalDateTime verifiedAt;

  @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
  private String adresse;

  private String image;

  @NotNull(message = "Le rôle est obligatoire")
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Candidature> candidatures;

  @OneToMany(mappedBy = "publiePar", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Offre> offres;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Favoris> favoris;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Notification> notifications;

  @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Avis> avisLaisses;

  @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Avis> avisRecus;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Token> tokens;
}