package ma.stagefinder.services;

import ma.stagefinder.dtos.CategorieDTO;
import ma.stagefinder.entities.Categorie;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Tactive l'khadma b' Mockito
class CategorieServiceImplTest {

    @Mock // Katgoul l'Mockito: "Sawb lia version mzowra (fake) dial had l'objet"
    private CategorieRepository categorieRepository;

    @Mock // Hta l'mapper ghadi n'mockiwh bach n'3ezlo l'service bo7do
    private EntityMapper mapper;

    @InjectMocks // Katgoul: "Kriyi instance 7a9i9ia dial CategorieServiceImpl, w injecter fiha l'mocks li l'fo9"
    private CategorieServiceImpl categorieService;


    @Test
    void testGetCategorieById_WhenCategorieExists() {
        // === ARRANGE: On prépare nos "faux" objets ===
        Long categoryId = 1L;
        Categorie fakeCategorieEntity = new Categorie();
        fakeCategorieEntity.setId(categoryId);
        fakeCategorieEntity.setTitre("Informatique");

        CategorieDTO fakeCategorieDTO = new CategorieDTO();
        fakeCategorieDTO.setId(categoryId);
        fakeCategorieDTO.setTitre("Informatique");

        // >> L'Magie de Mockito <<
        // "Melli chi 7ed y'3iyyet l' findById(1L), matmchich l'database, rje3 lina had l'objet l'mzower"
        when(categorieRepository.findById(categoryId)).thenReturn(Optional.of(fakeCategorieEntity));

        // "Melli chi 7ed y'3iyyet l' toCategorieDTO, rje3 lina had l'DTO l'mzower"
        when(mapper.toCategorieDTO(fakeCategorieEntity)).thenReturn(fakeCategorieDTO);

        // === ACT: On exécute la vraie méthode du service ===
        CategorieDTO result = categorieService.getCategorieById(categoryId);

        // === ASSERT: On vérifie le résultat ===
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Informatique", result.getTitre());

        // T2ekked anaho 3eyyet l' findById merra we7da
        verify(categorieRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategorieById_WhenCategorieNotFound() {
        // === ARRANGE: On prépare le cas où la catégorie n'existe pas ===
        Long categoryId = 99L;

        // "Melli n'3yt lik b' 99, rje3 ليا walou (Optional.empty())"
        when(categorieRepository.findById(categoryId)).thenReturn(Optional.empty());

        // === ACT & ASSERT: On s'attend à ce qu'une exception soit levée ===
        assertThrows(RuntimeException.class, () -> {
            categorieService.getCategorieById(categoryId);
        });

        // T2ekked anaho ma3eyyetch l'l'mapper, 7it l9a l'objet khawi
        verify(mapper, never()).toCategorieDTO(any());
    }
}