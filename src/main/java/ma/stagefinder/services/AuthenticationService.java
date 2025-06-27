package ma.stagefinder.services;

import lombok.RequiredArgsConstructor; // ✅ Ziyada jdida
import ma.stagefinder.dtos.AuthRequest;
import ma.stagefinder.dtos.AuthResponse;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.Token;
import ma.stagefinder.entities.enums.ActionTokenType;
import ma.stagefinder.entities.enums.Role; // ✅ Ziyada jdida
import ma.stagefinder.entities.enums.TokenType;
import ma.stagefinder.repositories.ActionTokenRepository;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.repositories.TokenRepository;
import ma.stagefinder.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor // ✅ B'blasset les @Autowired
public class AuthenticationService {

  // On utilise "final" pour que RequiredArgsConstructor les injecte
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final ActionTokenRepository actionTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final ActionTokenService actionTokenService;
  private final EmailService emailService;
  private final AbonnementService abonnementService; // ✅ Injection n9iya


  @Transactional
  public ResponseEntity<AuthResponse> register(User request) {
    // 1. Vérifier si l'email existe déjà
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
    if (existingUser.isPresent()) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Email déjà utilisé"));
    }

    // 2. Validation du mot de passe...
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Le mot de passe est obligatoire"));
    }
    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,}$";
    if (!request.getPassword().matches(passwordRegex)) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null,
              "Le mot de passe doit contenir au moins 6 caractères, une majuscule, une minuscule et un chiffre."));
    }

    // 3. Vérifier le rôle
    if (request.getRole() == null) {
      return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Le rôle est obligatoire pour l'inscription"));
    }

    // 4. Encoder le mot de passe
    request.setPassword(passwordEncoder.encode(request.getPassword()));

    // 5. Mettre verifiedAt à null
    request.setVerifiedAt(null);

    // 6. Sauvegarder l'utilisateur
    User savedUser = userRepository.save(request);

    // ✅ ======================================================================
    // ==     7. CORRECTION: On vérifie le rôle avant de créer l'abonnement    ==
    // ======================================================================
    if (savedUser.getRole() == Role.STAGIAIRE) {
      abonnementService.createInitialAbonnementForUser(savedUser);
    }


    try {
      // 8. Générer le token de vérification d'email
      String verificationToken = actionTokenService.generateEmailVerificationToken(savedUser);

      // 9. Envoyer l'email de vérification
      emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

      // 10. Renvoyer le message de succès
      return ResponseEntity.ok(new AuthResponse(null, null,
              "Inscription réussie! Veuillez vérifier votre email pour activer votre compte."));

    } catch (Exception e) {
      return ResponseEntity.ok(new AuthResponse(null, null,
              "Inscription réussie! Erreur d'envoi d'email - contactez le support."));
    }
  }

  public ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );

      User user = userRepository.findByEmail(request.getEmail())
              .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

      if (user.getVerifiedAt() == null) {
        return ResponseEntity.status(403)
                .body(new AuthResponse(null, null, "Veuillez vérifier votre email avant de vous connecter"));
      }

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

  // ... (le reste des méthodes handleForgotPassword, etc. ne changent pas)

  @Transactional
  public void handleForgotPassword(String email) {
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      String resetToken = actionTokenService.generatePasswordResetToken(user);
      emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }
  }

  @Transactional
  public void resetPassword(User user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    actionTokenRepository.deleteByUserAndType(user, ActionTokenType.PASSWORD_RESET);
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
