package ma.stagefinder.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.stagefinder.entities.enums.TokenType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
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

  private boolean expired;
  private boolean revoked;

  @Enumerated(EnumType.STRING)
  @Column(name = "token_type")
  private TokenType tokenType;

  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;
}
