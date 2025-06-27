package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.CategorieDTO;
import ma.stagefinder.entities.Categorie;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.CategorieRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

  private final CategorieRepository categorieRepository;
  private final EntityMapper mapper;

  @Override
  public CategorieDTO addCategorie(CategorieDTO dto) {
    Categorie entity = mapper.toCategorie(dto);
    return mapper.toCategorieDTO(categorieRepository.save(entity));
  }

  @Override
  public CategorieDTO getCategorieById(Long id) {
    return categorieRepository.findById(id)
      .map(mapper::toCategorieDTO)
      .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
  }

  @Cacheable("categories")
  @Override
  public List<CategorieDTO> getAllCategories() {
    return categorieRepository.findAll()
      .stream()
      .map(mapper::toCategorieDTO)
      .collect(Collectors.toList());
  }

  @Override
  public CategorieDTO updateCategorie(Long id, CategorieDTO dto) {
    Categorie cat = categorieRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

    cat.setTitre(dto.getTitre());
    cat.setTypeCategorie(dto.getTypeCategorie());

    return mapper.toCategorieDTO(categorieRepository.save(cat));
  }

  @Override
  public void deleteCategorie(Long id) {
    Categorie cat = categorieRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

    if (cat.getOffres() != null && !cat.getOffres().isEmpty()) {
      throw new RuntimeException("Impossible de supprimer une catégorie liée à des offres.");
    }

    categorieRepository.delete(cat);
  }
}
