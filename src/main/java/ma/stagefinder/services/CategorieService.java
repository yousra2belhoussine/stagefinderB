package ma.stagefinder.services;

import ma.stagefinder.dtos.CategorieDTO;

import java.util.List;

public interface CategorieService {
  CategorieDTO addCategorie(CategorieDTO dto);
  CategorieDTO getCategorieById(Long id);
  List<CategorieDTO> getAllCategories();
  CategorieDTO updateCategorie(Long id, CategorieDTO dto);
  void deleteCategorie(Long id);
}
