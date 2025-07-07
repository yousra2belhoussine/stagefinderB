package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.FavorisDTO;
import ma.stagefinder.services.FavorisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoris")
@RequiredArgsConstructor
public class FavorisController {

  private final FavorisService favorisService;

  @PostMapping
  public ResponseEntity<FavorisDTO> ajouterFavoris(@RequestBody FavorisDTO dto) {
    return ResponseEntity.ok(favorisService.ajouterFavoris(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<FavorisDTO> getFavorisById(@PathVariable Long id) {
    return ResponseEntity.ok(favorisService.getFavorisById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> supprimerFavoris(@PathVariable Long id) {
    favorisService.supprimerFavoris(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<FavorisDTO>> getFavorisByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(favorisService.getFavorisByUser(userId));
  }

  @GetMapping("/offre/{offreId}")
  public ResponseEntity<List<FavorisDTO>> getFavorisByOffre(@PathVariable Long offreId) {
    return ResponseEntity.ok(favorisService.getFavorisByOffre(offreId));
  }

  @GetMapping
  public ResponseEntity<List<FavorisDTO>> getAllFavoris() {
    return ResponseEntity.ok(favorisService.getAllFavoris());
  }
}
