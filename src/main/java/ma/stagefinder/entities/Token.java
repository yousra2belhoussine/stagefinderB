package ma.stagefinder.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.stagefinder.entities.enums.TokenType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "token") // Ajout pour la clarté
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String token;

  @Enumerated(EnumType.STRING)
  @Column(name = "token_type") // Correspond au liquibase
  private TokenType tokenType;

  private boolean expired;
  private boolean revoked;

  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE) // Si l'utilisateur est supprimé, ses tokens le sont aussi
  private User user;
}
