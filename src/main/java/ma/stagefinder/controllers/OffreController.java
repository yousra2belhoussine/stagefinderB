package ma.stagefinder.controllers;

import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.services.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "http://localhost:4200")
public class OffreController {

  @Autowired
  private OffreService offreService;

  @Autowired
  private OffreRepository offreRepository;

  @Autowired
  private EntityMapper entityMapper;

  @GetMapping("/count")
  public ResponseEntity<Long> countOffres() {
    return ResponseEntity.ok(offreService.countOffres());
  }

  @PostMapping("/publier")
  public ResponseEntity<OffreDTO> publierOffre(
          @RequestBody Offre offre,
          @RequestParam Long userId,
          @RequestParam Long categorieId) {
    try {
      OffreDTO nouvelleOffre = offreService.publierOffre(offre, userId, categorieId);
      return ResponseEntity.ok(nouvelleOffre);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<OffreDTO> getOffreById(@PathVariable Long id) {
    try {
      OffreDTO offre = offreService.getOffreById(id);
      return ResponseEntity.ok(offre);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<Page<OffreDTO>> getOffresByUser(
          @PathVariable Long userId,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "datePublication"));
    Page<Offre> offres = offreRepository.findByPublieParId(userId, pageable);
    Page<OffreDTO> offreDTOs = offres.map(entityMapper::toOffreDTO);
    return ResponseEntity.ok(offreDTOs);
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllOffres(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "3") int size,
          @RequestParam(required = false) String typeCategorie,
          @RequestParam(required = false) String ville) {

    try {
      Page<OffreDTO> offres = offreService.getAllOffres(page, size, typeCategorie, ville);
      Map<String, Object> response = new HashMap<>();
      response.put("offres", offres.getContent());
      response.put("currentPage", offres.getNumber());
      response.put("totalItems", offres.getTotalElements());
      response.put("totalPages", offres.getTotalPages());
      response.put("hasNext", offres.hasNext());
      response.put("hasPrevious", offres.hasPrevious());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
    try {
      offreService.deleteOffre(id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<OffreDTO> updateOffre(
          @PathVariable Long id,
          @RequestBody OffreDTO offreDTO) {
    try {
      OffreDTO updatedOffre = offreService.updateOffre(id, offreDTO);
      return ResponseEntity.ok(updatedOffre);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/by-ids")
  public ResponseEntity<List<OffreDTO>> getOffresByIds(@RequestParam List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(offreService.findOffresByIds(ids));
  }
}