package ma.stagefinder.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

  @Value("${app.upload.cv-dir}")
  private String cvDirectory;

  @Value("${app.upload.logo-dir}")
  private String logoDirectory;

  @Value("${app.upload.lettre-dir}")
  private String lettreMotivationDirectory;

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
}
