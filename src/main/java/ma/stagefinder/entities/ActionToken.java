package ma.stagefinder.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.stagefinder.entities.enums.ActionTokenType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token; // The unique token string (e.g., UUID)

    @Enumerated(EnumType.STRING) // Stores the enum value as a string ("EMAIL_VERIFICATION")
    private ActionTokenType type; // The purpose of the token

    private LocalDateTime expiresAt; // The timestamp when this token becomes invalid

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // If the user is deleted, delete their tokens too
    private User user;
}