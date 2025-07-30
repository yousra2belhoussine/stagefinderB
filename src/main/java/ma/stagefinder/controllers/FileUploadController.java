package ma.stagefinder.controllers;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//@Service
public class FileUploadController {
  private static final List<String> ALLOWED_CV_TYPES = Arrays.asList(
          "application/pdf",
          "application/msword",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  );
  private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
          "image/jpeg",
          "image/png"
  );

  public String storeFile(MultipartFile file, String type) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IOException("Le fichier est vide ou null");
    }

    String contentType = file.getContentType();
    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename != null
            ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
            : "";

    // Log pour débogage
    System.out.println("Type MIME détecté : " + contentType);
    System.out.println("Extension : " + extension);
    System.out.println("Type demandé : " + type);

    // Validation des types de fichiers
    if (type.equals("cvFile") || type.equals("lettreMotivation") || type.equals("cvChoisi")) {
      if (contentType == null || !ALLOWED_CV_TYPES.contains(contentType)) {
        throw new IOException("Type de fichier non supporté ou type incorrect : " + extension);
      }
    } else if (type.equals("image") && (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType))) {
      throw new IOException("Type de fichier non supporté ou type incorrect : " + extension);
    }

    // Générer un nom de fichier unique et sauvegarder
    String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
    Path targetPath = Paths.get("uploads/" + type).resolve(fileName);
    Files.createDirectories(targetPath.getParent());
    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

    return fileName;
  }

  public Path getFilePath(String filename, String type) {
    return Paths.get("uploads/" + type).resolve(filename);
  }
}