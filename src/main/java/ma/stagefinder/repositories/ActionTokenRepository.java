package ma.stagefinder.repositories;

import ma.stagefinder.entities.ActionToken;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.ActionTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ActionTokenRepository extends JpaRepository<ActionToken, Long> {

    // Find token by token string and type
    Optional<ActionToken> findByTokenAndType(String token, ActionTokenType type);

    // Find token for a user and type
    Optional<ActionToken> findByUserAndType(User user, ActionTokenType type);

    // Delete expired tokens
    @Modifying
    @Transactional
    @Query("DELETE FROM ActionToken a WHERE a.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);

    // Delete all tokens for a user and type
    @Modifying
    @Transactional
    void deleteByUserAndType(User user, ActionTokenType type);
}