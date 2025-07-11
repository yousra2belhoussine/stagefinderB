package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.services.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FileUploadController {

  private final FileStorageService fileStorageService;

  // ✅ Endpoint pour uploader un fichier
  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(
          @RequestParam("file") MultipartFile file,
          @RequestParam("type") String type // "image", "cvFile", "lettreMotivation", "cvChoisi"
  ) {
    try {
      String filename = fileStorageService.storeFile(file, type);
      return ResponseEntity.ok(filename);
    } catch (Exception e) {
      return ResponseEntity
              .badRequest()
              .body("❌ Erreur lors de l’upload : " + e.getMessage());
    }
  }

  // ✅ Endpoint pour récupérer un fichier
  @GetMapping("/get")
  @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('RECRUTEUR') or hasRole('STAGIAIRE')")
  public ResponseEntity<Resource> getFile(
          @RequestParam("filename") String filename,
          @RequestParam("type") String fileType
  ) {
    try {
      Path filePath = fileStorageService.getFilePath(filename, fileType);

      if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
        return ResponseEntity.notFound().build();
      }

      Resource resource = new UrlResource(filePath.toUri());

      String contentType = Files.probeContentType(filePath);
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      return ResponseEntity.ok()
              .contentType(MediaType.parseMediaType(contentType))
              .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
              .body(resource);

    } catch (IOException e) {
      return ResponseEntity.internalServerError().build();
    }
  }
  // ✅ Endpoint public pour afficher une image sans sécurité
  @GetMapping("/view")
  public ResponseEntity<Resource> viewImage(@RequestParam String filename) {
    try {
      Path filePath = fileStorageService.getFilePath(filename, "image");

      if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
        return ResponseEntity.notFound().build();
      }

      Resource resource = new UrlResource(filePath.toUri());
      String contentType = Files.probeContentType(filePath);
      if (contentType == null) {
        contentType = "image/jpeg"; // par défaut
      }

      return ResponseEntity.ok()
              .contentType(MediaType.parseMediaType(contentType))
              .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
              .body(resource);

    } catch (IOException e) {
      return ResponseEntity.internalServerError().build();
    }
  }

}
