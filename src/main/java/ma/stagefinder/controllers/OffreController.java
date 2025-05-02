package ma.stagefinder.controllers;
import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.services.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offres")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OffreController {

    @Autowired
    private OffreService offreService;

    @PostMapping("/publier")
    public ResponseEntity<ma.stagefinder.dtos.OffreDTO> publierOffre(
            @RequestBody Offre offre,
            @RequestParam Long userId,
            @RequestParam Long categorieId) {
        OffreDTO nouvelleOffre = offreService.publierOffre(offre, userId, categorieId);
        return ResponseEntity.ok(nouvelleOffre);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOffres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String typeCategorie,
            @RequestParam(required = false) String ville) {
        Page<OffreDTO> offres = offreService.getAllOffres(page,size, typeCategorie, ville);
        Map<String, Object> response = new HashMap<>();
        response.put("offres", offres.getContent());
        response.put("currentPage", offres.getNumber());
        response.put("totalItems", offres.getTotalElements());
        response.put("totalPages", offres.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
        offreService.deleteOffre(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OffreDTO> updateOffre(
            @PathVariable Long id,
            @RequestBody OffreDTO offreDTO) {
        OffreDTO updatedOffre = offreService.updateOffre(id, offreDTO);
        return ResponseEntity.ok(updatedOffre);
    }
}
