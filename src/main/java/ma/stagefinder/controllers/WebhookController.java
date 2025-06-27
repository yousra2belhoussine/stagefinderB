package ma.stagefinder.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest; // ✅ Ziyada jdida
import lombok.RequiredArgsConstructor;
import ma.stagefinder.services.PaiementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException; // ✅ Ziyada jdida
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class WebhookController {

    private final PaiementService paiementService;

    @Value("${stripe.api.webhook-secret}")
    private String webhookSecret;

    /**
     * ✅ CORRECTION FINALE ET DÉFINITIVE:
     * On lit le corps en bytes, puis on le convertit manuellement en String UTF-8.
     * C'est la méthode la plus robuste pour éviter les problèmes de compilation.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {

        String payloadString;
        try {
            // On lit le corps de la requête "brut" (raw) en tant que bytes
            byte[] payloadBytes = request.getInputStream().readAllBytes();
            // PUIS on le convertit en String avec l'encodage correct (UTF-8)
            payloadString = new String(payloadBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la lecture du payload du webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Cannot read request body.");
        }

        String sigHeader = request.getHeader("Stripe-Signature");
        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Stripe-Signature header.");
        }

        Event event;
        try {
            // ✅ ON UTILISE LA VERSION QUI PREND UN STRING
            // Cette version est connue par tous les compilateurs.
            event = Webhook.constructEvent(payloadString, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("❌ Erreur de vérification de la signature du Webhook! La clé est probablement incorrecte.");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed.");
        }

        // Le reste du code ne change pas
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: Unable to deserialize event data");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) stripeObject;
                System.out.println("✅ Webhook: Paiement réussi! Traitement de la session: " + session.getId());
                paiementService.handleCheckoutSessionCompleted(session);
                break;
            default:
                System.out.println("Événement non géré (reçu mais ignoré): " + event.getType());
        }

        return ResponseEntity.ok().build();
    }
}
