package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.services.AuthenticationService;
import ma.stagefinder.dtos.AuthRequest;
import ma.stagefinder.dtos.AuthResponse;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.Role;
import ma.stagefinder.repositories.UserRepository;
import ma.stagefinder.security.JwtUtil;
import ma.stagefinder.services.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationService authenticationService;
  private final FileStorageService fileStorageService;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final EntityMapper entityMapper;
  private final PasswordEncoder passwordEncoder; // 👈 AJOUT ICI


  // ✅ Enregistrement avec CV et logo facultatifs
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AuthResponse> register(
    @RequestPart("user") User user,
    @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
    @RequestPart(value = "image", required = false) MultipartFile logoFile
  ) {
    try {
      // ✅ Vérification des doublons
      if (userRepository.existsByEmail(user.getEmail())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new AuthResponse(null, null, "L'email est déjà utilisé."));
      }

      if (userRepository.existsByNom(user.getNom())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new AuthResponse(null, null, "Le nom est déjà pris."));
      }

      if (user.getRc() != null && userRepository.existsByRc(user.getRc())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new AuthResponse(null, null, "Ce RC est déjà utilisé."));
      }

      if (user.getIce() != null && userRepository.existsByIce(user.getIce())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new AuthResponse(null, null, "Cet ICE est déjà utilisé."));
      }

      // ✅ Traitement des fichiers
      if (cvFile != null && !cvFile.isEmpty()) {
        String storedCV = fileStorageService.storeFile(cvFile, "cvFile");
        user.setCvFile(storedCV);
      }

      if (logoFile != null && !logoFile.isEmpty()) {
        String storedLogo = fileStorageService.storeFile(logoFile, "image");
        user.setImage(storedLogo);
      }

      System.out.println("✅ Image reçue : " + user.getImage());
      System.out.println("✅ CV reçu : " + user.getCvFile());

      // ⛏️ Encodage du mot de passe (si non fait ailleurs)
     // user.setPassword(passwordEncoder.encode(user.getPassword()));

      // 👉 Appel au service pour finaliser l'enregistrement
      return authenticationService.register(user);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError()
        .body(new AuthResponse(null, null, "Erreur serveur : " + e.getMessage()));
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
  public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = authHeader.substring(7); // Supprimer "Bearer "
    String email = jwtUtil.extractEmail(token);

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

    UserDTO dto = entityMapper.toUserDTO(user); // ✅ tu utilises ton mapper ici
    return ResponseEntity.ok(dto);
  }


  @PutMapping("/me")
  public ResponseEntity<UserDTO> updateCurrentUser(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody UserDTO userDto) {

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = authHeader.substring(7);
    String email = jwtUtil.extractEmail(token);

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

    // 🔁 Mise à jour des champs autorisés
    user.setNom(userDto.getNom());
    user.setAdresse(userDto.getAdresse());
    user.setTel(userDto.getTel());
    user.setImage(userDto.getImage());
    user.setCvFile(userDto.getCvFile());

    User updated = userRepository.save(user);
    return ResponseEntity.ok(entityMapper.toUserDTO(updated));
  }
  @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDTO> updateCurrentUserMultipart(
    @RequestHeader("Authorization") String authHeader,
    @RequestPart("user") UserDTO userDto,
    @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
    @RequestPart(value = "image", required = false) MultipartFile imageFile
  ) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = authHeader.substring(7);
    String email = jwtUtil.extractEmail(token);

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

    user.setNom(userDto.getNom());
    user.setAdresse(userDto.getAdresse());
    user.setTel(userDto.getTel());

    // ✅ Mise à jour du mot de passe si fourni
    if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
      String oldHash = user.getPassword();
      String newHash = passwordEncoder.encode(userDto.getPassword());
      user.setPassword(newHash);

      System.out.println("🔐 Mot de passe mis à jour (multipart) pour l'utilisateur : " + user.getEmail());
      System.out.println("Ancien hash : " + oldHash);
      System.out.println("Nouveau hash : " + newHash);
    } else {
      System.out.println("ℹ️ Aucun mot de passe fourni (multipart) pour l'utilisateur : " + user.getEmail());
    }

    try {
      System.out.println("imageFile == null ? " + (imageFile == null));
      if (imageFile != null) {
        System.out.println("imageFile.getOriginalFilename() = " + imageFile.getOriginalFilename());
        System.out.println("imageFile.isEmpty() = " + imageFile.isEmpty());
        System.out.println("imageFile.getSize() = " + imageFile.getSize());
      }
      if (imageFile != null && !imageFile.isEmpty()) {
        fileStorageService.deleteFile(user.getImage(), "image");
        String newImage = fileStorageService.storeFile(imageFile, "image");
        user.setImage(newImage);
        System.out.println("📷 Image mise à jour pour " + user.getEmail());
      }

      if (cvFile != null && !cvFile.isEmpty()) {
        fileStorageService.deleteFile(user.getCvFile(), "cvFile");
        String newCv = fileStorageService.storeFile(cvFile, "cvFile");
        user.setCvFile(newCv);
        System.out.println("📄 CV mis à jour pour " + user.getEmail());
      }
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(500).build();
    }

    User updated = userRepository.save(user);
    System.out.println("✅ Profil multipart enregistré en base pour : " + user.getEmail());

    return ResponseEntity.ok(entityMapper.toUserDTO(updated));
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
