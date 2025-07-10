package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.stagefinder.dtos.FavorisDTO;
import ma.stagefinder.dtos.OffreDTO; // <-- 1. ZEDNA IMPORT J'DID
import ma.stagefinder.services.FavorisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoris")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class FavorisController {

  private final FavorisService favorisService;

  /**
   * Ajoute une offre aux favoris d'un utilisateur.
   */
  @PostMapping
  public ResponseEntity<FavorisDTO> ajouterFavoris(@RequestBody FavorisDTO dto) {
    log.info("Requête pour ajouter un favori pour l'utilisateur ID: {} et l'offre ID: {}", dto.getUserId(), dto.getOffreId());
    return ResponseEntity.ok(favorisService.ajouterFavoris(dto));
  }

  /**
   * Supprime un favori par son ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> supprimerFavoris(@PathVariable Long id) {
    log.info("Requête pour supprimer le favori avec l'ID: {}", id);
    favorisService.supprimerFavoris(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Récupère toutes les offres favorites d'un utilisateur spécifique.
   * Cette méthode utilise le cache Redis.
   */
  @GetMapping("/user/{userId}")
  // ✅ CORRECTION: On change le type de retour en List<OffreDTO>
  public ResponseEntity<List<OffreDTO>> getFavorisByUser(@PathVariable Long userId) {
    log.info("Requête pour récupérer les offres favorites de l'utilisateur ID: {}", userId);
    return ResponseEntity.ok(favorisService.getFavorisByUser(userId));
  }
}
