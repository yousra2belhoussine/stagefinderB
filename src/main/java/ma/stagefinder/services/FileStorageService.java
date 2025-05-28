package ma.stagefinder.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

  @Value("${app.upload.cv-dir}")
  private String cvDirectory;

  @Value("${app.upload.logo-dir}")
  private String logoDirectory;

  @Value("${app.upload.lettre-dir}")
  private String lettreMotivationDirectory;

  private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
  private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(".pdf", ".docx");

  /**
   * Enregistre un fichier dans le bon répertoire selon son type.
   *
   * @param file le fichier à sauvegarder
   * @param type peut être "cv", "logo", ou "lettre"
   * @return le nom du fichier enregistré
   * @throws IOException si une erreur survient lors de la sauvegarde
   */
  public String saveFile(MultipartFile file, String type) throws IOException {
    String directory;

    switch (type.toLowerCase()) {
      case "cv":
        directory = cvDirectory;
        break;
      case "logo":
        directory = logoDirectory;
        break;
      case "lettre":
        directory = lettreMotivationDirectory;
        break;
      default:
        throw new IllegalArgumentException("Type de fichier non pris en charge : " + type);
    }

    Path dirPath = Paths.get(directory);
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath);
    }

    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    Path filePath = dirPath.resolve(fileName);
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    return fileName;
  }

  /**
   * Enregistre un fichier en détectant son extension et son type automatiquement.
   */
  public String storeFile(MultipartFile file, String fileType) throws IOException {
    if (file.isEmpty()) {
      throw new IOException("Le fichier est vide");
    }

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      throw new IOException("Nom de fichier non valide");
    }
    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    String newFilename = UUID.randomUUID().toString() + fileExtension;

    String targetDir;
    if (IMAGE_EXTENSIONS.contains(fileExtension) && fileType.equals("image")) {
      targetDir = logoDirectory;
    } else if (DOCUMENT_EXTENSIONS.contains(fileExtension) &&
      (fileType.equals("cv") || fileType.equals("lettre"))) {
      targetDir = fileType.equals("cv") ? cvDirectory : lettreMotivationDirectory;
    } else {
      throw new IOException("Type de fichier non supporté ou type incorrect : " + fileExtension);
    }

    Path filePath = Paths.get(targetDir, newFilename);
    Files.write(filePath, file.getBytes());

    return newFilename;
  }

  public Path getFilePath(String fileName, String type) {
    String directory;

    switch (type.toLowerCase()) {
      case "cv":
        directory = cvDirectory;
        break;
      case "logo":
        directory = logoDirectory;
        break;
      case "lettre":
        directory = lettreMotivationDirectory;
        break;
      default:
        throw new IllegalArgumentException("Type de fichier non pris en charge : " + type);
    }

    return Paths.get(directory).resolve(fileName);
  }
}
