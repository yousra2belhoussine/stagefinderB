package ma.stagefinder.controllers;

import ma.stagefinder.dtos.OffreDTO;
import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.enums.TypeCategorie;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.services.OffreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final Logger logger = LoggerFactory.getLogger(OffreController.class);

    @GetMapping("/count")
    public long countOffres() {
        return offreService.countOffres();
    }

    @PostMapping
    public ResponseEntity<OffreDTO> publierOffre(
            @RequestBody Offre offre,
            @RequestParam Long userId,
            @RequestParam Long categorieId) {
        OffreDTO nouvelleOffre = offreService.publierOffre(offre, userId, categorieId);
        return ResponseEntity.ok(nouvelleOffre);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreDTO> getOffreById(@PathVariable Long id) {
        OffreDTO offre = offreService.getOffreById(id);
        return ResponseEntity.ok(offre);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OffreDTO>> getOffresByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
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
        Page<OffreDTO> offres = offreService.getAllOffres(page, size, typeCategorie, ville);
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

    @GetMapping("/ville/{ville}")
    public ResponseEntity<Map<String, Object>> getOffresByVille(
            @PathVariable String ville,
            @PageableDefault(size = 3) Pageable pageable) {
        logger.info("Fetching offers for ville: {}", ville);
        Page<Offre> offrePage = offreRepository.findByVille(ville, pageable);
        List<OffreDTO> offreDTOs = offrePage.getContent().stream()
                .map(entityMapper::toOffreDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("offres", offreDTOs);
        response.put("currentPage", offrePage.getNumber());
        response.put("totalItems", offrePage.getTotalElements());
        response.put("totalPages", offrePage.getTotalPages());
        return ResponseEntity.ok(response);
    }
}