package ma.stagefinder.controllers;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.CategorieDTO;
import ma.stagefinder.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController // ← Nécessaire
@RequestMapping("/api/category/")
public class CategorieController {

    private final CategoryService categorieService;

    @GetMapping("/categories")
    public List<CategorieDTO> getAllCategories() {

        return categorieService.getAllCategories();
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategorieDTO> getCategoryById(@PathVariable Long id) {
        CategorieDTO categorieDTO = categorieService.getCategoryById(id);
        return ResponseEntity.ok(categorieDTO);
    }

    @PostMapping("/add")
    public ResponseEntity<CategorieDTO> addCategory(@RequestBody CategorieDTO categorieDTO) {
        try {
            CategorieDTO createdCategory = categorieService.addCategory(categorieDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint pour mettre à jour une catégorie
    @PutMapping("update/{id}")
    public ResponseEntity<CategorieDTO> updateCategory(@PathVariable Long id, @RequestBody CategorieDTO categorieDTO) {
        CategorieDTO updatedCategory = categorieService.updateCategory(id, categorieDTO);
        return ResponseEntity.ok(updatedCategory);
    }


    // Endpoint pour supprimer une catégorie
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        // Appeler le service pour supprimer la catégorie
        categorieService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // Retourner 204 No Content
    }


}
