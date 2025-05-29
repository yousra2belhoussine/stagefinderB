package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.UpdateEstValideRequest;
import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.enums.Role;
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
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final FileStorageService fileStorageService;

  @GetMapping("/count")
  public long countUsers(@RequestParam(value = "role", required = false) Role role) {
    return (role != null) ? userService.countByRole(role) : userService.count();
  }

  @PostMapping
  public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
    return ResponseEntity.ok(userService.create(dto));
  }

  @PostMapping(value = "/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDTO> createWithFiles(
    @RequestPart("user") UserDTO userDto,
    @RequestPart(value = "cv", required = false) MultipartFile cv,
    @RequestPart(value = "image", required = false) MultipartFile image
    //  @RequestPart(value = "lettre", required = false) MultipartFile lettre
  ) {
    try {
      if (cv != null && !cv.isEmpty()) {
        String cvFileName = fileStorageService.storeFile(cv, "cv");
        userDto.setCvFile(cvFileName);
      }
      if (image != null && !image.isEmpty()) {
        String imageFileName = fileStorageService.storeFile(image, "image");
        userDto.setImage(imageFileName);
      }
      //  if (lettre != null && !lettre.isEmpty()) {
      //  String lettreFileName = fileStorageService.storeFile(lettre, "lettre");
      //}
      return ResponseEntity.ok(userService.create(userDto));
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

  @PatchMapping("/{id}/validate")
  public ResponseEntity<UserDTO> updateEstValide(@PathVariable Long id, @RequestBody UpdateEstValideRequest request) {
    return ResponseEntity.ok(userService.updateEstValide(id, request.isEstValide()));
  }

 /* @PutMapping("/{userId}/profile")
  public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
    return ResponseEntity.ok(userService.updateUserProfile(userId, userDTO));
  }*/


  @GetMapping("/{userId}/profile")
  public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserProfile(userId));
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
    return ResponseEntity.noContent().build();
  }

  // Endpoint pour tester le stockage de fichiers indépendamment
  @PostMapping("/upload-file")
  public ResponseEntity<String> uploadnFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam("type") String type
  ) {
    try {
      String fileName = userService.storeFile(file, type);
      return ResponseEntity.ok(fileName);
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Erreur lors du chargement du fichier");
    }
  }
}
