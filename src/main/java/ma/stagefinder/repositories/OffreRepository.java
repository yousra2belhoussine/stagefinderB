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

  //  Page<Offre> findBytypeCategorieAndVille(String typeCategorie, String ville, Pageable pageable);
}
