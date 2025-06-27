package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.entities.User; // <-- ZID HADA L'IMPORT
import ma.stagefinder.repositories.UserRepository; // <-- ZID HADA L'IMPORT
import ma.stagefinder.services.FavorisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; // <-- IMPORT STANDARD
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/favoris")
@RequiredArgsConstructor
public class FavorisController {

  private final FavorisService favorisService;
  private final UserRepository userRepository; // <-- ZEDNA HADI BACH NJIBO L'USER KAMEL

  @PostMapping("/{offreId}")
  public ResponseEntity<Void> ajouterFavoris(@PathVariable Long offreId, Authentication authentication) {
    // 1. Kan-jbdo l'objet UserDetails li standard f Spring
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String username = userDetails.getUsername(); // Hada غالبا ghadi ykon howa l'email

    // 2. Kan-jbdo l'objet User kaml men la base b l'email/username
    User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + username));

    // 3. Daba 3ndna l'ID w n9dro n3ayto l service
    favorisService.ajouterFavoris(user.getId(), offreId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{offreId}")
  public ResponseEntity<Void> supprimerFavoris(@PathVariable Long offreId, Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String username = userDetails.getUsername();

    User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + username));

    favorisService.supprimerFavoris(user.getId(), offreId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint Jdid w Sécurisé : Récupère les favoris de l'utilisateur ACTUELLEMENT authentifié.
   * @param authentication L'objet d'authentification injecté par Spring Security.
   * @return Une liste d'IDs d'offres.
   */
  @GetMapping("/my-favorites") // <-- L'URL Jdid
  public ResponseEntity<Set<String>> getMyFavorites(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + userDetails.getUsername()));

    // Kan3ayto l nafs l-service, walakin b l'ID li jbedna men l-token
    return ResponseEntity.ok(favorisService.getFavorisByUser(user.getId()));
  }
}