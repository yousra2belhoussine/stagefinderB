package ma.stagefinder.repositories;

import ma.stagefinder.entities.Abonnement;
import ma.stagefinder.entities.enums.StatutAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {


    Optional<Abonnement> findByUserId(Long userId);

    long countByStatut(StatutAbonnement statut);


}
