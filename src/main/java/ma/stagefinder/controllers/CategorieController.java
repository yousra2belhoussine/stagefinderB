package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.CategorieDTO;
import ma.stagefinder.services.CategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

  private final CategorieService categorieService;

  @PostMapping
  public ResponseEntity<CategorieDTO> create(@RequestBody CategorieDTO dto) {
    return ResponseEntity.ok(categorieService.addCategorie(dto));
  }

  @GetMapping
  public ResponseEntity<List<CategorieDTO>> getAll() {
    return ResponseEntity.ok(categorieService.getAllCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategorieDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(categorieService.getCategorieById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategorieDTO> update(@PathVariable Long id, @RequestBody CategorieDTO dto) {
    return ResponseEntity.ok(categorieService.updateCategorie(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categorieService.deleteCategorie(id);
    return ResponseEntity.noContent().build();
  }
}
