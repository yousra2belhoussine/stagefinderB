package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.AbonnementDTO;
import ma.stagefinder.entities.Abonnement;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.StatutAbonnement;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.AbonnementRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Transactional
    public void createInitialAbonnementForUser(User user) {
        System.out.println("➡️ [AbonnementService] Création d'un abonnement GRATUIT pour l'utilisateur: " + user.getEmail());
        Abonnement abonnement = new Abonnement();
        abonnement.setUser(user);
        abonnementRepository.save(abonnement);
        System.out.println("✅ [AbonnementService] Abonnement GRATUIT créé avec succès.");
    }

    /**
     * ✅ MÉTHODE DE DÉBOGAGE: On va tracer chaque étape ici.
     */
    @Transactional
    public void activerAbonnementPayant(Long userId, BigDecimal prix, String stripeTransactionId) {
        System.out.println("\n--- [AbonnementService] Tentative d'activation de l'abonnement PAYE ---");
        System.out.println("1. Recherche de l'abonnement pour userId: " + userId);

        Abonnement abonnement = abonnementRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Abonnement non trouvé pour l'utilisateur avec l'ID: " + userId));

        System.out.println("2. Abonnement trouvé! ID: " + abonnement.getId() + ", Statut actuel: " + abonnement.getStatut());

        System.out.println("3. Mise à jour des nouvelles valeurs...");
        abonnement.setStatut(StatutAbonnement.PAYE);
        abonnement.setPrix(prix);
        abonnement.setDate_paiement(LocalDateTime.now());
        abonnement.setStripeTransactionId(stripeTransactionId);

        System.out.println("4. Sauvegarde des modifications dans la base de données...");
        abonnementRepository.save(abonnement);

        System.out.println("✅ SUCCÈS: L'abonnement a été mis à jour en PAYE.");
        System.out.println("--- [AbonnementService] Fin de l'activation --- \n");
    }

    public AbonnementDTO getAbonnementForCurrentUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Abonnement abonnement = abonnementRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Abonnement non trouvé pour l'utilisateur actuel"));

        return entityMapper.toAbonnementDTO(abonnement);
    }
}
