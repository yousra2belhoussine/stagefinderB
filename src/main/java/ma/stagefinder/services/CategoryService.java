package ma.stagefinder.services;

import ma.stagefinder.dtos.CategorieDTO;

import java.util.List;

public interface CategoryService {

    List<CategorieDTO> getAllCategories();

    CategorieDTO getCategoryById(Long id);

    // Méthode pour créer une catégorie
    CategorieDTO addCategory(CategorieDTO categorieDTO);

    CategorieDTO updateCategory(Long id, CategorieDTO categorieDTO);

    // Méthode pour supprimer une catégorie par son ID
    void deleteCategory(Long id);


}
