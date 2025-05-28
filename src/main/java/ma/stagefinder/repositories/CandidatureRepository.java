package ma.stagefinder.repositories;

import ma.stagefinder.entities.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
  List<Candidature> findByUserId(Long userId);    // Candidatures d'un utilisateur
  List<Candidature> findByOffreId(Long offreId);  // Candidatures pour une offre
  Page<Candidature> findByOffreId(Long offreId, Pageable pageable);
}
