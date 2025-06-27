package ma.stagefinder.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor; // ✅ Ajout
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor // ✅ Ajout pour l'injection propre
public class PaiementService {

    // Injection des clés
    @Value("${stripe.api.secret-key}")
    private String secretKey;
    @Value("${stripe.api.price-id}")
    private String priceId;

    // ✅ Injection du service d'abonnement
    private final AbonnementService abonnementService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    /**
     * Crée une session de paiement Stripe Checkout.
     * ✅ Modification: La méthode accepte maintenant l'ID de l'utilisateur.
     *
     * @param userId l'ID de l'utilisateur qui effectue le paiement.
     * @return L'URL de la page de paiement hébergée par Stripe.
     * @throws StripeException si une erreur se produit.
     */
    public String createCheckoutSession(Long userId) throws StripeException {
        String successUrl = "http://localhost:4200/paiement-reussi";
        String cancelUrl = "http://localhost:4200/paiement-annule";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                // ✅ ZIYADA MOHIMA: On passe l'ID de l'utilisateur à Stripe.
                // C'est le moyen le plus sûr de savoir qui a payé dans le webhook.
                .setClientReferenceId(userId.toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }


    /**
     * ✅ MÉTHODE JDIDA: Gère l'événement de paiement réussi reçu du webhook.
     *
     * @param session L'objet Session complet reçu de Stripe.
     */
    public void handleCheckoutSessionCompleted(Session session) {
        // 1. Récupérer l'ID de l'utilisateur qu'on avait stocké
        String clientReferenceId = session.getClientReferenceId();
        if (clientReferenceId == null) {
            // Log d'erreur: On ne peut pas traiter un paiement sans savoir à qui il appartient.
            System.err.println("Erreur: ClientReferenceId (userId) manquant dans la session Stripe: " + session.getId());
            return;
        }
        Long userId = Long.parseLong(clientReferenceId);

        // 2. Récupérer le montant total payé
        // Le montant est en centimes (ex: 5000 pour 50.00 EUR), il faut le diviser par 100.
        BigDecimal prixPaye = BigDecimal.valueOf(session.getAmountTotal()).divide(new BigDecimal("100"));

        // 3. Récupérer l'ID de la transaction (Payment Intent ID)
        String stripeTransactionId = session.getPaymentIntent();

        // 4. Appeler le service d'abonnement pour activer le plan PAYE
        abonnementService.activerAbonnementPayant(userId, prixPaye, stripeTransactionId);

        System.out.println("Paiement réussi et abonnement activé pour l'utilisateur ID: " + userId);
    }
}
