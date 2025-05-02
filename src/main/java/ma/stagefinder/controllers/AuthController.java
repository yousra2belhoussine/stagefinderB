package ma.stagefinder.controllers;

import ma.stagefinder.auth.AuthenticationService;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.dtos.AuthRequest;
import ma.stagefinder.dtos.AuthResponse;
import ma.stagefinder.entities.User;
import ma.stagefinder.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // (optionnel) pour permettre au Front Angular d'appeler sans problème
public class AuthController {

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserRepository userRepository;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody User user) {
    return authenticationService.register(user);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return authenticationService.authenticate(request);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
    try {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Token manquant ou mal formé"));
      }

      String refreshToken = authHeader.substring(7);
      String email = jwtUtil.extractEmail(refreshToken);

      if (!jwtUtil.isTokenValid(refreshToken, email)) {
        return ResponseEntity.status(401).body(new AuthResponse(null, null, "Refresh token invalide ou expiré"));
      }

      User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

      // ✅ Génère de nouveaux tokens
      String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
      String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

      return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken, "Token régénéré avec succès"));

    } catch (Exception e) {
      return ResponseEntity.status(500).body(new AuthResponse(null, null, "Erreur lors du rafraîchissement du token"));
    }
  }
}
