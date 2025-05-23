package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.AvisDTO;
import ma.stagefinder.services.AvisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

  private final AvisService avisService;

  @PostMapping
  public ResponseEntity<AvisDTO> createAvis(@RequestBody AvisDTO dto) {
    return ResponseEntity.ok(avisService.createAvis(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AvisDTO> getAvisById(@PathVariable Long id) {
    return ResponseEntity.ok(avisService.getAvisById(id));
  }

  @GetMapping
  public List<AvisDTO> getAllAvis() {
    return avisService.getAllAvis();
  }

  @GetMapping("/auteur/{auteurId}")
  public List<AvisDTO> getAvisByAuteur(@PathVariable Long auteurId) {
    return avisService.getAvisByAuteur(auteurId);
  }

  @GetMapping("/destinataire/{destId}")
  public List<AvisDTO> getAvisByDestinataire(@PathVariable Long destId) {
    return avisService.getAvisByDestinataire(destId);
  }

  @GetMapping("/offre/{offreId}")
  public List<AvisDTO> getAvisByOffre(@PathVariable Long offreId) {
    return avisService.getAvisByOffre(offreId);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AvisDTO> updateAvis(@PathVariable Long id, @RequestBody AvisDTO dto) {
    return ResponseEntity.ok(avisService.updateAvis(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAvis(@PathVariable Long id) {
    avisService.deleteAvis(id);
    return ResponseEntity.noContent().build();
  }
}
