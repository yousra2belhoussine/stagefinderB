package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ma.stagefinder.entities.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user") // On garde le nom de la table de la branche "malkii"
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // --- Ajout de la validation ---
  @NotBlank(message = "Le nom est obligatoire")
  @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
  @Column(unique = true)
  private String nom;

  // --- Ajout de la validation ---
  @Column(unique = true)
  @NotBlank(message = "L'email est obligatoire")
  @Email(message = "Format d'email invalide")
  private String email;

  @Column(name = "nom_entreprise")
  @Size(max = 100, message = "Le nom de l'entreprise ne peut pas dépasser 100 caractères")
  private String nomEntreprise;

  @Column(unique = true, nullable = true)
  @Size(max = 20, message = "Le RC ne peut pas dépasser 20 caractères")
  private String RC;

  @Column(unique = true, nullable = true)
  @Size(max = 20, message = "L'ICE ne peut pas dépasser 20 caractères")
  private String ICE;

  // --- Ajout de la validation ---
  @NotBlank(message = "Le mot de passe est obligatoire")
  private String password;

  // --- Ajout de la validation ---
  @Column(unique = true)
  @NotBlank(message = "Le téléphone est obligatoire")
  @Pattern(regexp = "^(06|07)\\d{8}$",
          message = "Le téléphone doit commencer par 06 ou 07 et contenir 10 chiffres")
  private String tel;

  @Column(name = "cv_file")
  private String cvFile;

  @Column(name = "est_valide")
  private Boolean estValide;

  // --- CHAMP AJOUTÉ ---
  @Column(name = "verified_at")
  private LocalDateTime verifiedAt;

  @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
  private String adresse;

  private String image;

  // On garde le champ de "malkii"
  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  // --- Ajout de la validation ---
  @NotNull(message = "Le rôle est obligatoire")
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Candidature> candidatures;

  // --- Ajout de la gestion en cascade ---
  @OneToMany(mappedBy = "publiePar", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Offre> offres;

  // --- Ajout de la gestion en cascade ---
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Favoris> favoris;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Notification> notifications;

  // --- Ajout de la gestion en cascade ---
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Token> tokens;

  // On n'ajoute PAS les relations 'Avis' et 'Abonnement' pour le moment.
}
