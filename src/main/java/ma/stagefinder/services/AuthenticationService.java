package ma.stagefinder.services;

import ma.stagefinder.dtos.AuthRequest;
import ma.stagefinder.dtos.AuthResponse;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.Token;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.entities.enums.TokenType;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.repositories.TokenRepository;
import ma.stagefinder.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  public ResponseEntity<AuthResponse> register(User request) {
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
    if (existingUser.isPresent()) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Email déjà utilisé"));
    }

    // Encode le mot de passe
   request.setPassword(passwordEncoder.encode(request.getPassword()));

    // Par défaut, rôle = STAGIAIRE
    // if (request.getRole() == null) {
    //request.setRole(Role.STAGIAIRE);
    //}
    //request.setEstValide(true);
    if (request.getRole() == null) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Le rôle est obligatoire pour l'inscription"));
    }

    // Sauvegarde utilisateur
    User savedUser = userRepository.save(request);

    // ✅ Génère les tokens avec email + rôle
    String accessToken = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(),savedUser.getRole());
    String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

    // Sauvegarde en base
    saveUserTokens(savedUser, accessToken, TokenType.ACCESS);
    saveUserTokens(savedUser, refreshToken, TokenType.REFRESH);

    return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Inscription réussie"));
  }

  public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );

      User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

      // ✅ Inclut le rôle dans le accessToken
      String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
      String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

      saveUserTokens(user, accessToken, TokenType.ACCESS);
      saveUserTokens(user, refreshToken, TokenType.REFRESH);

      return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Connexion réussie"));

    } catch (Exception e) {
      return ResponseEntity.status(401)
        .body(new AuthResponse(null, null, "Email ou mot de passe invalide"));
    }
  }

  private void saveUserTokens(User user, String jwt, TokenType type) {
    Token token = Token.builder()
      .token(jwt)
      .tokenType(type)
      .user(user)
      .expired(false)
      .revoked(false)
      .createdAt(LocalDateTime.now())
      .build();
    tokenRepository.save(token);
  }
}
