package ma.stagefinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.stagefinder.entities.enums.StatutAbonnement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAbonnement statut = StatutAbonnement.GRATUIT;

    @Column(precision = 10, scale = 2) // Pour gérer les nombres décimaux (ex: 50.00)
    private BigDecimal prix;

    private LocalDateTime date_paiement;

    @Column(unique = true) // Chaque transaction Stripe a un ID unique
    private String stripeTransactionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonIgnore
    private User user;
}
