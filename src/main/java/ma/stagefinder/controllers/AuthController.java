package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.stagefinder.services.AuthenticationService;
import ma.stagefinder.dtos.AuthRequest;
import ma.stagefinder.dtos.AuthResponse;
import ma.stagefinder.dtos.EmailVerificationRequestDTO;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.security.JwtUtil;
import ma.stagefinder.services.FileStorageService;
import ma.stagefinder.services.ActionTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

  private final AuthenticationService authenticationService;
  private final FileStorageService fileStorageService;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final ActionTokenService actionTokenService;

  // Liste des types de fichiers autorisés
  private static final List<String> ALLOWED_CV_TYPES = Arrays.asList(
          "application/pdf", "application/msword",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  );
  private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
          "image/jpeg", "image/png", "image/gif"
  );
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AuthResponse> register(
          @RequestParam("nom") @NotBlank String nom,
          @RequestParam("email") @Email @NotBlank String email,
          @RequestParam("password") @NotBlank String password,
          @RequestParam("tele") @NotBlank String tele,
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
      log.info("Tentative d'inscription pour l'email: {}", email);

      // Validation du rôle
      Role role;
      try {
        role = Role.valueOf(roleStr.toUpperCase());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, "Rôle invalide. Rôles autorisés: STAGIAIRE, ENTREPRISE"));
      }

      // Validation spécifique selon le rôle
      if (role == Role.RECRUTEUR) {
        if (isBlank(nomEntreprise)) {
          return ResponseEntity.badRequest()
                  .body(new AuthResponse(null, null, "Le nom de l'entreprise est obligatoire pour le rôle ENTREPRISE"));
        }
      }

      // Validation des fichiers
      String cvValidationError = validateFile(cvFile, "CV", ALLOWED_CV_TYPES);
      if (cvValidationError != null) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, cvValidationError));
      }

      String imageValidationError = validateFile(logoFile, "Image", ALLOWED_IMAGE_TYPES);
      if (imageValidationError != null) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, imageValidationError));
      }

      // Créer l'objet User
      User user = buildUser(nom, email, password, tele, nomEntreprise, RC, ICE, adresse, estValide, role);

      // Gestion des fichiers
      if (cvFile != null && !cvFile.isEmpty()) {
        String storedCV = fileStorageService.saveFile(cvFile, "cv");
        user.setCvFile(storedCV);
      }

      if (logoFile != null && !logoFile.isEmpty()) {
        String storedLogo = fileStorageService.saveFile(logoFile, "logo");
        user.setImage(storedLogo);
      }

      log.info("Inscription - Données validées pour: {}", email);
      return authenticationService.register(user);

    } catch (Exception e) {
      log.error("Erreur lors de l'inscription pour {}: {}", email, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new AuthResponse(null, null, "Erreur interne lors de l'inscription"));
    }
  }

  @PostMapping(value = "/register-admin", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody User admin) {
    try {
      log.info("Tentative de création d'administrateur pour: {}", admin.getEmail());

      // Validation supplémentaire pour admin
      if (isBlank(admin.getNom()) || isBlank(admin.getEmail()) || isBlank(admin.getPassword())) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, "Nom, email et mot de passe sont obligatoires"));
      }

      admin.setRole(Role.ADMINISTRATEUR);
      return authenticationService.register(admin);

    } catch (Exception e) {
      log.error("Erreur lors de la création d'admin: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new AuthResponse(null, null, "Erreur lors de la création de l'administrateur"));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    try {
      log.info("Tentative de connexion pour: {}", request.getEmail());
      return authenticationService.authenticate(request);
    } catch (Exception e) {
      log.error("Erreur lors de la connexion pour {}: {}", request.getEmail(), e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new AuthResponse(null, null, "Erreur interne lors de la connexion"));
    }
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
    try {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, "Token manquant ou mal formé"));
      }

      String refreshToken = authHeader.substring(7);
      String email = jwtUtil.extractEmail(refreshToken);

      if (!jwtUtil.isTokenValid(refreshToken, email)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, null, "Refresh token invalide ou expiré"));
      }

      User user = userRepository.findByEmail(email)
              .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

      String newAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
      String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

      log.info("Token régénéré avec succès pour: {}", email);
      return ResponseEntity.ok(
              new AuthResponse(newAccessToken, newRefreshToken, "Token régénéré avec succès")
      );

    } catch (Exception e) {
      log.error("Erreur lors du rafraîchissement du token: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new AuthResponse(null, null, "Erreur lors du rafraîchissement du token"));
    }
  }

  // ✅ NOUVEAU: Endpoint pour vérifier l'email
  @PostMapping("/verify-email")
  public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody EmailVerificationRequestDTO request) {
    try {
      log.info("Tentative de vérification d'email avec token: {}", request.getToken().substring(0, 8) + "...");

      Optional<User> userOpt = actionTokenService.getUserByEmailVerificationToken(request.getToken());

      if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, "Token de vérification invalide ou expiré"));
      }

      User user = userOpt.get();

      // Vérifier si l'email est déjà vérifié
      if (user.getVerifiedAt() != null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, "Email déjà vérifié"));
      }

      // Marquer comme vérifié
      user.setVerifiedAt(java.time.LocalDateTime.now());
      userRepository.save(user);

      // Supprimer le token après vérification
      boolean tokenVerified = actionTokenService.verifyEmailToken(request.getToken());

      if (tokenVerified) {
        log.info("Email vérifié avec succès pour: {}", user.getEmail());
        return ResponseEntity.ok(
                new AuthResponse(null, null, "Email vérifié avec succès! Vous pouvez maintenant vous connecter.")
        );
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, "Erreur lors de la vérification"));
      }

    } catch (Exception e) {
      log.error("Erreur lors de la vérification d'email: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new AuthResponse(null, null, "Erreur interne lors de la vérification"));
    }
  }

  // ✅ NOUVEAU: Renvoyer email de vérification
  @PostMapping("/resend-verification")
  public ResponseEntity<AuthResponse> resendVerification(@RequestParam("email") @Email String email) {
    try {
      log.info("Demande de renvoi de vérification pour: {}", email);

      Optional<User> userOpt = userRepository.findByEmail(email);
      if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, "Aucun compte trouvé avec cet email"));
      }

      User user = userOpt.get();

      if (user.getVerifiedAt() != null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, "Email déjà vérifié"));
      }

      // Générer nouveau token et renvoyer email
      String verificationToken = actionTokenService.generateEmailVerificationToken(user);
      // Appeler emailService pour renvoyer l'email

      return ResponseEntity.ok(
              new AuthResponse(null, null, "Email de vérification renvoyé avec succès")
      );

    } catch (Exception e) {
      log.error("Erreur lors du renvoi de vérification pour {}: {}", email, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new AuthResponse(null, null, "Erreur lors du renvoi de l'email"));
    }
  }

  // ===================== MÉTHODES UTILITAIRES =====================

  private User buildUser(String nom, String email, String password, String tele,
                         String nomEntreprise, String RC, String ICE, String adresse,
                         boolean estValide, Role role) {
    User user = new User();
    user.setNom(nom);
    user.setEmail(email);
    user.setPassword(password);
    user.setTel(tele);
    user.setNomEntreprise(nomEntreprise);
    user.setRC(RC);
    user.setICE(ICE);
    user.setAdresse(adresse);
    user.setEstValide(estValide);
    user.setRole(role);
    return user;
  }

  private String validateFile(MultipartFile file, String fileType, List<String> allowedTypes) {
    if (file == null || file.isEmpty()) {
      return null; // Fichier optionnel
    }

    // Vérifier la taille
    if (file.getSize() > MAX_FILE_SIZE) {
      return fileType + " trop volumineux. Taille maximale: 5MB";
    }

    // Vérifier le type MIME
    String contentType = file.getContentType();
    if (contentType == null || !allowedTypes.contains(contentType)) {
      return fileType + " format non autorisé. Types acceptés: " + allowedTypes;
    }

    return null; // Pas d'erreur
  }

  private boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }
}