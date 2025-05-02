package ma.stagefinder.repositories;


import ma.stagefinder.entities.User;
import ma.stagefinder.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByToken(String token);

  List<Token> findAllByUser(User user);

  List<Token> findAllByUserAndExpiredFalseAndRevokedFalse(User user);
}
