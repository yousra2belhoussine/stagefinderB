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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FileUploadController {

  private final FileStorageService fileStorageService;

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(
          @RequestParam("file") MultipartFile file,
          @RequestParam("type") String type // "cv" ou "logo"
  ) {
    try {
      // ✅ ==========================================================
      // ==     CORRECTION: On utilise storeFile au lieu de saveFile    ==
      // ==========================================================
      String filename = fileStorageService.storeFile(file, type);
      return ResponseEntity.ok(filename);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Erreur lors de l’upload : " + e.getMessage());
    }
  }

  @GetMapping("/{filename:.+}")
  @PreAuthorize("hasRole('ADMINISTRATEUR')")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    try {
      Path[] directories = new Path[] {
              Paths.get("uploads/cvs"),
              Paths.get("uploads/logos"),
              Paths.get("uploads/lettres")
      };

      Path filePath = null;

      for (Path dir : directories) {
        Path candidate = dir.resolve(filename).normalize();
        if (Files.exists(candidate) && Files.isReadable(candidate)) {
          filePath = candidate;
          break;
        }
      }

      if (filePath == null) {
        return ResponseEntity.notFound().build();
      }

      Resource resource = new UrlResource(filePath.toUri());

      String contentType;
      String filenameLower = filePath.getFileName().toString().toLowerCase();
      if (filenameLower.endsWith(".pdf")) {
        contentType = "application/pdf";
      } else if (filenameLower.endsWith(".png")) {
        contentType = "image/png";
      } else if (filenameLower.endsWith(".jpg") || filenameLower.endsWith(".jpeg")) {
        contentType = "image/jpeg";
      } else {
        contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";
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
