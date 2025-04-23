package ma.stagefinder.repositories;

import ma.stagefinder.entities.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByAuteurId(Long auteurId);         // Avis laissés par un utilisateur
    List<Avis> findByDestinataireId(Long destinataireId); // Avis reçus par un utilisateur
    List<Avis> findByOffreId(Long offreId);           // Avis sur une offre
}
