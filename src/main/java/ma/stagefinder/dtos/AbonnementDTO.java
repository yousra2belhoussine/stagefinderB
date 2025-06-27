package ma.stagefinder.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.stagefinder.entities.enums.StatutAbonnement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbonnementDTO {

    private Long id;

    private StatutAbonnement statut;

    private BigDecimal prix;

    private LocalDateTime date_paiement;

    // On n'expose pas l'ID de transaction Stripe au DTO par sécurité,
    // sauf si on en a explicitement besoin côté front-end.
    // Pour l'instant, on le laisse commenté.
    // private String stripeTransactionId;

}