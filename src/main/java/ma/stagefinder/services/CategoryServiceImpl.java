package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.CategorieDTO;
import ma.stagefinder.entities.Categorie;
import ma.stagefinder.exceptions.ResourceNotFoundException;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.CategorieRepository;
import ma.stagefinder.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategorieRepository categorieRepository;

    // Le constructeur injecte CategoryRepository
    // Cette ligne peut être supprimée car @AllArgsConstructor génère automatiquement un constructeur
    // public CategorieServiceImpl(CategoryRepository categoryRepository) {
    //     this.categoryRepository = categoryRepository;
    // }

    @Override
    public List<CategorieDTO> getAllCategories() {
        // Récupérer toutes les catégories de la base de données
        List<Categorie> categories = categorieRepository.findAll();

        // Mapper les entités en DTOs et retourner la liste
        return categories.stream()
                .map(EntityMapper.INSTANCE::toCategorieDTO)
                .collect(Collectors.toList());
    }

    public CategorieDTO getCategoryById(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie avec id " + id + " introuvable"));
        return EntityMapper.INSTANCE.toCategorieDTO(categorie); // ou autre méthode de mapping
    }

    // Méthode pour ajouter une catégorie
    @Override
    public CategorieDTO addCategory(CategorieDTO categorieDTO) {
        // Mapper le CategorieDTO en entité Categorie
        Categorie categorie = EntityMapper.INSTANCE.toCategorie(categorieDTO);

        // Sauvegarder la catégorie dans la base de données
        Categorie savedCategory = categorieRepository.save(categorie);

        // Mapper l'entité sauvegardée en CategorieDTO et la retourner
        return EntityMapper.INSTANCE.toCategorieDTO(savedCategory);
    }

    @Override
    public CategorieDTO updateCategory(Long id, CategorieDTO categorieDTO) {
        // Trouver la catégorie par ID
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));

        // Mettre à jour les informations de la catégorie
        categorie.setTitre(categorieDTO.getTitre());
        categorie.setTypeCategorie(categorieDTO.getTypeCategorie());

        // Sauvegarder la catégorie mise à jour
        Categorie updatedCategorie = categorieRepository.save(categorie);

        // Retourner le DTO mis à jour
        return EntityMapper.INSTANCE.toCategorieDTO(updatedCategorie);
    }

    @Override
    public void deleteCategory(Long id) {
        // Vérifier si la catégorie existe
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie avec id " + id + " introuvable"));

        // Supprimer la catégorie
        categorieRepository.delete(categorie);
    }



}
