package ma.stagefinder.repositories;

import ma.stagefinder.entities.Offre;
import ma.stagefinder.entities.enums.TypeCategorie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    @Query("SELECT o FROM Offre o WHERE o.categorie.typeCategorie = :typeCategorie")
    Page<Offre> findByTypeCategorie(
            @Param("typeCategorie") TypeCategorie typeCategorie,
            Pageable pageable
    );

    @Query("SELECT o FROM Offre o WHERE o.ville = :ville")
    Page<Offre> findByVille(
            @Param("ville") String ville,
            Pageable pageable
    );

    @Query("SELECT o FROM Offre o WHERE o.categorie.typeCategorie = :typeCategorie OR o.ville = :ville")
    Page<Offre> findByTypeCategorieOrVille(
            @Param("typeCategorie") TypeCategorie typeCategorie,
            @Param("ville") String ville,
            Pageable pageable
    );

    Page<Offre> findByPublieParId(Long userId, Pageable pageable);
  //  Page<Offre> findBytypeCategorieAndVille(String typeCategorie, String ville, Pageable pageable);

    @Query(value = "SELECT TO_CHAR(date_publication, 'YYYY-MM') AS month, COUNT(*) AS count " +
            "FROM public.offre GROUP BY TO_CHAR(date_publication, 'YYYY-MM') " +
            "ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> findJobPostingsPerMonth();

    @Query(value = "SELECT c.type_categorie AS category, COUNT(o.id) AS count " +
            "FROM public.offre o JOIN public.categorie c ON o.categorie_id = c.id " +
            "GROUP BY c.type_categorie ORDER BY count DESC LIMIT 10", nativeQuery = true)
    List<Map<String, Object>> findMostPublishedCategories();
}
