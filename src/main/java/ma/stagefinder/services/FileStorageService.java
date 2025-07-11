package ma.stagefinder.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
//import jakarta.annotation.PostConstruct;


@Service
public class FileStorageService {

  @Value("${file.images-dir}")
  private String imagesDir;

  @Value("${file.documents-dir}")
  private String documentsDir;

  private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif",".jfif");
  private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(".pdf", ".docx");

  public String storeFile(MultipartFile file, String fileType) throws IOException {
    if (file.isEmpty()) {
      throw new IOException("Le fichier est vide");
    }

    // Générer un nom de fichier unique
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new IOException("Nom de fichier non valide");
    }
    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    String newFilename = UUID.randomUUID().toString() + fileExtension;

    // Déterminer le dossier de destination
    String targetDir;
    if (IMAGE_EXTENSIONS.contains(fileExtension) && fileType.equals("image")) {
      targetDir = imagesDir;
    } else if (DOCUMENT_EXTENSIONS.contains(fileExtension) &&
      (fileType.equals("cvFile") || fileType.equals("cvChoisi") || fileType.equals("lettreMotivation"))) {
      targetDir = documentsDir;
    } else {
      throw new IOException("Type de fichier non supporté ou type incorrect : " + fileExtension);
    }

    // Sauvegarder le fichier
    Path filePath = Paths.get(targetDir, newFilename);
    Files.write(filePath, file.getBytes()); // Correction ici : filePath en premier, file.getBytes() en second

    // Retourner le nom du fichier
    return newFilename;
  }

  public Path getFilePath(String filename, String fileType) {
    String targetDir = fileType.equals("image") ? imagesDir : documentsDir;
    return Paths.get(targetDir, filename);
  }

}
