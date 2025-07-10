package ma.stagefinder.services;

import ma.stagefinder.repositories.TokenRepository;
import ma.stagefinder.entities.Token;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

  private final TokenRepository tokenRepository;

  public void logout(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

    String jwt = authHeader.substring(7); // Retire "Bearer "
    Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

    if (storedToken != null) {
      storedToken.setRevoked(true);
      storedToken.setExpired(true);
      tokenRepository.save(storedToken);
    }
  }
}
