package ma.stagefinder.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "date_envoie")
    private LocalDateTime dateEnvoie;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;  // ← Ajouté avec valeur par défaut

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}