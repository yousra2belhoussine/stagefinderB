package ma.stagefinder.repositories;

import ma.stagefinder.entities.Favoris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FavorisRepository extends JpaRepository<Favoris, Long> {
    List<Favoris> findByUserId(Long userId);      // Favoris d'un utilisateur
}
