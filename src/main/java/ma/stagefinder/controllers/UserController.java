package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.services.FileStorageService;
import ma.stagefinder.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final FileStorageService fileStorageService;

  // 🔹 Création avec données JSON uniquement
  @PostMapping
  public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.create(dto));
  }

  // 🔹 Création avec FormData (CV, Lettre, Image)
  @PostMapping(value = "/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDTO> createWithFiles(
    @RequestPart("user") UserDTO userDto,
    @RequestPart(value = "cv", required = false) MultipartFile cv,
    @RequestPart(value = "lettre", required = false) MultipartFile lettre,
    @RequestPart(value = "image", required = false) MultipartFile image
  ) {
    try {
      if (cv != null && !cv.isEmpty()) {
        String cvFileName = fileStorageService.saveFile(cv, "cv");
        System.out.println("✅ CV enregistré : " + cvFileName);
        userDto.setCvFile(cvFileName);
      }

      if (lettre != null && !lettre.isEmpty()) {
        System.out.println("📨 Lettre reçue : " + lettre.getOriginalFilename());
        String lettreFileName = fileStorageService.saveFile(lettre, "lettre");
        System.out.println("✅ Lettre enregistrée sous : " + lettreFileName);
        userDto.setLettreMotivationFile(lettreFileName);
      }

      if (image != null && !image.isEmpty()) {
        String imageFileName = fileStorageService.saveFile(image, "logo");
        System.out.println("🖼️ Image enregistrée : " + imageFileName);
        userDto.setImage(imageFileName);
      }

      System.out.println("📦 Données envoyées à userService.create : " + userDto);

      UserDTO saved = userService.create(userDto);
      return ResponseEntity.ok(saved);

    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().body(null);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.update(id, dto));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDTO> partialUpdate(@PathVariable Long id, @RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.partialUpdate(id, dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAll() {
    return ResponseEntity.ok(userService.getAll());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAll() {
    userService.deleteAll();
    return ResponseEntity.noContent().build(); // Code HTTP 204
  }
}
