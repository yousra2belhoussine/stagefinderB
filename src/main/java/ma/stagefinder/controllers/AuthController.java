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

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AuthResponse> register(
          @RequestParam("nom") String nom,
          @RequestParam("email") String email,
          @RequestParam("password") String password,
          @RequestParam("tele") String tele,
          @RequestParam(value = "nomEntreprise", required = false) String nomEntreprise,
          @RequestParam(value = "RC", required = false) String RC,
          @RequestParam(value = "ICE", required = false) String ICE,
          @RequestParam(value = "adresse", required = false) String adresse,
          @RequestParam(value = "estValide", required = false, defaultValue = "true") boolean estValide,
          @RequestParam(value = "role", required = false, defaultValue = "STAGIAIRE") String roleStr,
          @RequestParam(value = "cv", required = false) MultipartFile cvFile,
          @RequestParam(value = "image", required = false) MultipartFile logoFile
  ) {
    try {
      // Créer l'objet User
      User user = new User();
      user.setNom(nom);
      user.setEmail(email);
      user.setPassword(password); // Ajouter le mot de passe
      user.setTel(tele);
      user.setNomEntreprise(nomEntreprise);
      user.setRC(RC);
      user.setICE(ICE);
      user.setAdresse(adresse);
      user.setEstValide(estValide);

      // Convertir le role string en enum
      try {
        user.setRole(Role.valueOf(roleStr.toUpperCase()));
      } catch (IllegalArgumentException e) {
        user.setRole(Role.STAGIAIRE); // Valeur par défaut
      }

      // Gestion des fichiers
      if (cvFile != null && !cvFile.isEmpty()) {
        String storedCV = fileStorageService.saveFile(cvFile, "cv");
        user.setCvFile(storedCV);
      }

      if (logoFile != null && !logoFile.isEmpty()) {
        String storedLogo = fileStorageService.saveFile(logoFile, "logo");
        user.setImage(storedLogo);
      }

      System.out.println("✅ Données reçues:");
      System.out.println("   - Nom: " + nom);
      System.out.println("   - Email: " + email);
      System.out.println("   - Password: " + (password != null ? "***" : "null"));
      System.out.println("   - Téléphone: " + tele);
      System.out.println("   - Nom Entreprise: " + nomEntreprise);
      System.out.println("   - RC: " + RC);
      System.out.println("   - ICE: " + ICE);
      System.out.println("   - Adresse: " + adresse);
      System.out.println("   - Est Validé: " + estValide);
      System.out.println("   - Rôle: " + user.getRole());
      System.out.println("   - CV: " + user.getCvFile());
      System.out.println("   - Image: " + user.getImage());

      return authenticationService.register(user);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError()
              .body(new AuthResponse(null, null, "Erreur lors de l'inscription: " + e.getMessage()));
    }
  }
  //jou temporaire de cette methode
  @PostMapping(value = "/register-admin", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> registerAdmin(@RequestBody User admin) {
    // 👇 On impose ici que le rôle soit ADMINISTRATEUR
    admin.setRole(Role.ADMINISTRATEUR);
    return authenticationService.register(admin);
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
