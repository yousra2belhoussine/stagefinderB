package ma.stagefinder.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.stagefinder.dtos.*;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.security.JwtUtil;
import ma.stagefinder.services.ActionTokenService;
import ma.stagefinder.services.AuthenticationService;
import ma.stagefinder.services.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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

  // Constantes pour la validation des fichiers
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
          @RequestParam(value = "role", required = false, defaultValue = "STAGIAIRE") String roleStr,
          @RequestParam(value = "cv", required = false) MultipartFile cvFile,
          @RequestParam(value = "image", required = false) MultipartFile logoFile
  ) {
    try {
      log.info("Tentative d'inscription pour l'email: {}", email);

      Role role;
      try {
        role = Role.valueOf(roleStr.toUpperCase());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, "Rôle invalide. Rôles autorisés: STAGIAIRE, RECRUTEUR"));
      }

      if (role == Role.RECRUTEUR && isBlank(nomEntreprise)) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, "Le nom de l'entreprise est obligatoire pour le rôle RECRUTEUR"));
      }

      String cvValidationError = validateFile(cvFile, "CV", ALLOWED_CV_TYPES);
      if (cvValidationError != null) {
        return ResponseEntity.badRequest().body(new AuthResponse(null, null, cvValidationError));
      }

      String imageValidationError = validateFile(logoFile, "Image", ALLOWED_IMAGE_TYPES);
      if (imageValidationError != null) {
        return ResponseEntity.badRequest().body(new AuthResponse(null, null, imageValidationError));
      }

      User user = buildUser(nom, email, password, tele, nomEntreprise, RC, ICE, adresse, role);

      // ✅ ==========================================================
      // ==      CORRECTION: On envoie le bon type de fichier      ==
      // ==========================================================
      if (cvFile != null && !cvFile.isEmpty()) {
        // On envoie "cvFile" pour correspondre à la condition dans le service
        String storedCV = fileStorageService.storeFile(cvFile, "cvFile");
        user.setCvFile(storedCV);
      }

      if (logoFile != null && !logoFile.isEmpty()) {
        // On envoie "image" pour correspondre à la condition dans le service
        String storedLogo = fileStorageService.storeFile(logoFile, "image");
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

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    log.info("Tentative de connexion pour: {}", request.getEmail());
    return authenticationService.authenticate(request);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody EmailVerificationRequestDTO request) {
    log.info("Tentative de vérification d'email avec token...");
    Optional<User> userOpt = actionTokenService.getUserByEmailVerificationToken(request.getToken());

    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new AuthResponse(null, null, "Token de vérification invalide ou expiré"));
    }

    User user = userOpt.get();
    if (user.getVerifiedAt() != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new AuthResponse(null, null, "Email déjà vérifié"));
    }

    user.setVerifiedAt(LocalDateTime.now());
    userRepository.save(user);

    actionTokenService.verifyEmailToken(request.getToken());
    log.info("Email vérifié avec succès pour: {}", user.getEmail());
    return ResponseEntity.ok(new AuthResponse(null, null, "Email vérifié avec succès! Vous pouvez maintenant vous connecter."));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
    log.info("Demande de réinitialisation de mot de passe pour l'email: {}", request.getEmail());
    authenticationService.handleForgotPassword(request.getEmail());
    return ResponseEntity.ok(new AuthResponse(null, null,
            "Si un compte est associé à cet email, un lien de réinitialisation a été envoyé."));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
    log.info("Tentative de réinitialisation de mot de passe avec le token...");
    Optional<User> userOpt = actionTokenService.getUserByPasswordResetToken(request.getToken());

    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new AuthResponse(null, null, "Le lien de réinitialisation est invalide ou a expiré."));
    }

    authenticationService.resetPassword(userOpt.get(), request.getNewPassword());
    log.info("Mot de passe réinitialisé avec succès pour l'utilisateur: {}", userOpt.get().getEmail());
    return ResponseEntity.ok(new AuthResponse(null, null, "Votre mot de passe a été mis à jour avec succès."));
  }

  // Helper methods
  private User buildUser(String nom, String email, String password, String tele,
                         String nomEntreprise, String RC, String ICE, String adresse, Role role) {
    User user = new User();
    user.setNom(nom);
    user.setEmail(email);
    user.setPassword(password);
    user.setTel(tele);
    user.setNomEntreprise(nomEntreprise);
    user.setRC(RC);
    user.setICE(ICE);
    user.setAdresse(adresse);
    user.setRole(role);
    return user;
  }

  private String validateFile(MultipartFile file, String fileType, List<String> allowedTypes) {
    if (file == null || file.isEmpty()) return null;
    if (file.getSize() > MAX_FILE_SIZE) return fileType + " trop volumineux. Taille maximale: 5MB";
    String contentType = file.getContentType();
    if (contentType == null || !allowedTypes.contains(contentType)) return fileType + " format non autorisé.";
    return null;
  }

  private boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }
}
