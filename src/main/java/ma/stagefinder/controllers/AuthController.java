package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.services.AuthenticationService;
import ma.stagefinder.dtos.AuthRequest;
import ma.stagefinder.dtos.AuthResponse;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.security.JwtUtil;
import ma.stagefinder.services.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationService authenticationService;
  private final FileStorageService fileStorageService;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  //private final PasswordEncoder passwordEncoder; // 👈 AJOUT ICI


  // ✅ Enregistrement avec CV et logo facultatifs
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AuthResponse> register(
    @RequestPart("user") User user,
    @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
    @RequestPart(value = "image", required = false) MultipartFile logoFile

    ) {
    try {
      System.out.println("📥 Fichier CV reçu côté backend ? " + (cvFile != null));

      if (cvFile != null && !cvFile.isEmpty()) {
        String storedCV = fileStorageService.storeFile(cvFile, "cv");
        user.setCvFile(storedCV);
      }


      if (logoFile != null && !logoFile.isEmpty()) {
        String storedLogo = fileStorageService.storeFile(logoFile, "image");
        user.setImage(storedLogo);
      }
      System.out.println("✅ Image reçue côté backend (user.getImage()) : " + user.getImage());
      System.out.println("✅ CV reçu côté backend (user.getCvFile()) : " + user.getCvFile());
       //hna
      //user.setPassword(passwordEncoder.encode(user.getPassword()));
      return authenticationService.register(user);

    } catch (Exception e) {
      e.printStackTrace(); // Affiche l’erreur dans la console backend
      return ResponseEntity.internalServerError()
        .body(new AuthResponse(null, null, "Erreur lors de l'inscription avec fichier: " + e.getMessage()));
    }
  }
  //jou temporaire de cette methode
  @PostMapping(value = "/register-admin", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> registerAdmin(@RequestBody User admin) {
    // 👇 On impose ici que le rôle soit ADMINISTRATEUR
    admin.setRole(Role.ADMINISTRATEUR);
    return authenticationService.register(admin);
  }
  @GetMapping("/me")
  public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().build();
    }

    try {
      String token = authHeader.substring(7);
      String email = jwtUtil.extractEmail(token);
      return userRepository.findByEmail(email)
              .map(ResponseEntity::ok)
              .orElseGet(() -> ResponseEntity.notFound().build());
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }



  // ✅ Login classique
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return authenticationService.authenticate(request);
  }

  // ✅ Refresh du token JWT
  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
    try {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.badRequest()
          .body(new AuthResponse(null, null, "Token manquant ou mal formé"));
      }

      String refreshToken = authHeader.substring(7);
      String email = jwtUtil.extractEmail(refreshToken);

      if (!jwtUtil.isTokenValid(refreshToken, email)) {
        return ResponseEntity.status(401)
          .body(new AuthResponse(null, null, "Refresh token invalide ou expiré"));
      }

      User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

      String newAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
      String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

      return ResponseEntity.ok(
        new AuthResponse(newAccessToken, newRefreshToken, "Token régénéré avec succès")
      );

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500)
        .body(new AuthResponse(null, null, "Erreur lors du rafraîchissement du token"));
    }
  }
}
