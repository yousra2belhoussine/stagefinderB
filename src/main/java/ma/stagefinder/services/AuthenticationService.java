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

  @Autowired
  private ActionTokenService actionTokenService;

  @Autowired
  private EmailService emailService;

  public ResponseEntity<AuthResponse> register(User request) {
    // 1. Vérifier si l'email existe déjà
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
    if (existingUser.isPresent()) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Email déjà utilisé"));
    }

    // 2. VALIDATION DU PASSWORD AVANT HACHAGE
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Le mot de passe est obligatoire"));
    }

    // Validation avec regex
    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$";
    if (!request.getPassword().matches(passwordRegex)) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null,
              "doza doza"));
    }

    // 3. Vérifier le rôle
    if (request.getRole() == null) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Le rôle est obligatoire pour l'inscription"));
    }

    // 4. MAINTENANT on peut encoder le mot de passe
    request.setPassword(passwordEncoder.encode(request.getPassword()));

    // 5. Set verifiedAt to null (user not verified yet)
    request.setVerifiedAt(null);

    // 6. Sauvegarde utilisateur (NOT VERIFIED)
    User savedUser = userRepository.save(request);

    try {
      // 7. Generate email verification token
      String verificationToken = actionTokenService.generateEmailVerificationToken(savedUser);

      // 8. Send verification email
      emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

      // 9. Return success message WITHOUT tokens
      return ResponseEntity.ok(new AuthResponse(null, null,
              "Inscription réussie! Veuillez vérifier votre email pour activer votre compte."));

    } catch (Exception e) {
      // If email sending fails, still return success but mention the issue
      return ResponseEntity.ok(new AuthResponse(null, null,
              "Inscription réussie! Erreur d'envoi d'email - contactez le support."));
    }
  }

  public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );

      User user = userRepository.findByEmail(request.getEmail())
              .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

      // Check if user email is verified
      if (user.getVerifiedAt() == null) {
        return ResponseEntity.status(403)
                .body(new AuthResponse(null, null, "Veuillez vérifier votre email avant de vous connecter"));
      }

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