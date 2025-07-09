package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.CategorieDTO;
import ma.stagefinder.entities.Categorie;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.CategorieRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable; // <-- 1. L'IMPORT L'S7I7
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

  private final CategorieRepository categorieRepository;
  private final EntityMapper mapper;

  @Override
  @Transactional
  // ✅ CacheEvict: Melli n'zid chi categorie, kan mes7o l'cache dyal "categories" kamel.
  @CacheEvict(value = "categories", allEntries = true)
  public CategorieDTO addCategorie(CategorieDTO dto) {
    Categorie entity = mapper.toCategorie(dto);
    return mapper.toCategorieDTO(categorieRepository.save(entity));
  }

  @Override
  @Transactional(readOnly = true)
  public CategorieDTO getCategorieById(Long id) {
    return categorieRepository.findById(id)
            .map(mapper::toCategorieDTO)
            .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
  }

  @Override
  @Transactional(readOnly = true)
  // ✅ Cacheable: L'merra l'wla ghadi tjib la liste men PostgreSQL o t'khebbiha f Redis.
  // L'merrat jaya, ghadi tjibha direct men Redis.
  @Cacheable("categories") // <-- 2. KAN 3TIW SMIYA L L'CACHE
  public List<CategorieDTO> getAllCategories() {
    return categorieRepository.findAll()
            .stream()
            .map(mapper::toCategorieDTO)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  // ✅ CacheEvict: Melli n'beddlo chi categorie, kan mes7o l'cache.
  @CacheEvict(value = "categories", allEntries = true)
  public CategorieDTO updateCategorie(Long id, CategorieDTO dto) {
    Categorie cat = categorieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

    cat.setTitre(dto.getTitre());
    cat.setTypeCategorie(dto.getTypeCategorie());

    return mapper.toCategorieDTO(categorieRepository.save(cat));
  }

  @Override
  @Transactional
  // ✅ CacheEvict: Melli n'supprimiw chi categorie, kan mes7o l'cache.
  @CacheEvict(value = "categories", allEntries = true)
  public void deleteCategorie(Long id) {
    Categorie cat = categorieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

    // Cette vérification est bonne, mais elle devrait probablement être dans le service
    if (cat.getOffres() != null && !cat.getOffres().isEmpty()) {
      throw new RuntimeException("Impossible de supprimer une catégorie liée à des offres.");
    }

    categorieRepository.delete(cat);
  }
}
