package ma.stagefinder.controllers;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import ma.stagefinder.entities.User; // ✅ Ajout
import ma.stagefinder.repositories.UserRepository; // ✅ Ajout
import ma.stagefinder.services.PaiementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder; // ✅ Ajout
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/paiement")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;
    private final UserRepository userRepository; // ✅ Ajout pour récupérer l'utilisateur

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession() {
        try {
            // ✅ ZIYADA JDIDA: Récupérer l'utilisateur actuellement authentifié
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non authentifié ou introuvable."));

            // On passe l'ID de l'utilisateur au service
            String sessionUrl = paiementService.createCheckoutSession(currentUser.getId());

            // On retourne l'URL dans un objet JSON
            Map<String, String> response = Collections.singletonMap("url", sessionUrl);
            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de la session de paiement: " + e.getMessage());
        } catch (RuntimeException e) {
            // Gère le cas où l'utilisateur n'est pas trouvé
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}
