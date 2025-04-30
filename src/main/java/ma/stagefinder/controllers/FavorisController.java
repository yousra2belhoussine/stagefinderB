package ma.stagefinder.controllers;


import ma.stagefinder.dtos.FavorisDTO;
import ma.stagefinder.entities.Favoris;
import ma.stagefinder.services.FavorisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/favoris")
public class FavorisController {

    private final FavorisService favorisService;

    public FavorisController(FavorisService favorisService) {
        this.favorisService = favorisService;
    }



    @PostMapping("/add")
    public ResponseEntity<FavorisDTO> addFavoris(@RequestBody FavorisDTO favorisDTO) {
        try {
            FavorisDTO createdFavoris = favorisService.addFavoris(favorisDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFavoris);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFavoris(@PathVariable Long id) {
        try {
            favorisService.deleteFavoris(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavorisDTO>> getFavorisByUserId(@PathVariable Long userId) {
        try {
            List<FavorisDTO> favoris = favorisService.getFavorisByUserId(userId);
            return ResponseEntity.ok(favoris);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<FavorisDTO>> getFavorisByOffreId(@PathVariable Long offreId) {
        try {
            List<FavorisDTO> favoris = favorisService.getFavorisByOffreId(offreId);
            return ResponseEntity.ok(favoris);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
