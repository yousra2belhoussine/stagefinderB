package ma.stagefinder.repositories;

import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    long countByRole(Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    Optional<User> findByNom(String nom);

    @Query(value = "SELECT TO_CHAR(created_at, 'YYYY-MM') AS month, COUNT(*) AS count " +
            "FROM public.user WHERE role IN ('STAGIAIRE', 'RECRUTEUR') " +
            "GROUP BY TO_CHAR(created_at, 'YYYY-MM') " +
            "ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> findUsersRegisteredPerMonth();
}
