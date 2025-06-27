package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.AbonnementDTO;
import ma.stagefinder.services.AbonnementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final AbonnementService abonnementService;

    /**
     * Endpoint pour récupérer les informations de l'abonnement de l'utilisateur
     * actuellement authentifié.
     * Idéal pour afficher le statut (GRATUIT/PAYE) sur le profil de l'utilisateur.
     * @return Le DTO de l'abonnement.
     */
    @GetMapping("/me")
    public ResponseEntity<AbonnementDTO> getCurrentUserSubscription() {
        try {
            AbonnementDTO abonnementDTO = abonnementService.getAbonnementForCurrentUser();
            return ResponseEntity.ok(abonnementDTO);
        } catch (RuntimeException e) {
            // Gère les cas où l'utilisateur ou l'abonnement n'est pas trouvé
            // On pourrait retourner un statut 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    // On pourra ajouter plus tard un endpoint pour l'admin pour voir tous les abonnements
    // @GetMapping
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    // public ResponseEntity<List<AbonnementDTO>> getAllSubscriptions() { ... }
}
