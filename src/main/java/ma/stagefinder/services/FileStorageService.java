package ma.stagefinder.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

  private final Path cvStorageLocation;
  private final Path logoStorageLocation;
  private final Path lettreStorageLocation;

  // ✅ Liste des extensions autorisées, plus propre et facile à maintenir
  private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png");
  private static final List<String> ALLOWED_DOCUMENT_EXTENSIONS = Arrays.asList(".pdf", ".doc", ".docx");

  public FileStorageService(
          @Value("${app.upload.cv-dir:uploads/cvs}") String cvDir,
          @Value("${app.upload.logo-dir:uploads/logos}") String logoDir,
          @Value("${app.upload.lettre-dir:uploads/lettres}") String lettreDir) {

    // On normalise les chemins pour éviter les problèmes entre les systèmes d'exploitation
    this.cvStorageLocation = Paths.get(cvDir).toAbsolutePath().normalize();
    this.logoStorageLocation = Paths.get(logoDir).toAbsolutePath().normalize();
    this.lettreStorageLocation = Paths.get(lettreDir).toAbsolutePath().normalize();

    try {
      // On crée les dossiers s'ils n'existent pas
      Files.createDirectories(this.cvStorageLocation);
      Files.createDirectories(this.logoStorageLocation);
      Files.createDirectories(this.lettreStorageLocation);
    } catch (Exception ex) {
      throw new RuntimeException("Impossible de créer les répertoires de stockage des fichiers.", ex);
    }
  }

  /**
   * ✅ Méthode unique et corrigée pour enregistrer tous les types de fichiers.
   *
   * @param file Le fichier envoyé par l'utilisateur.
   * @param fileType Le type de fichier ('cv', 'logo', 'lettreMotivation', etc.) pour savoir où le stocker.
   * @return Le nom unique du fichier enregistré.
   * @throws IOException Si le fichier est vide ou si son type n'est pas supporté.
   */
  public String storeFile(MultipartFile file, String fileType) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IOException("Le fichier ne peut pas être vide.");
    }

    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
    String extension = "";
    int i = originalFilename.lastIndexOf('.');
    if (i > 0) {
      extension = originalFilename.substring(i).toLowerCase();
    }

    // 1. Vérification de l'extension
    if (!isExtensionSupported(extension, fileType)) {
      throw new IOException("Type de fichier non supporté: " + extension + " pour le type " + fileType);
    }

    // 2. Création d'un nom de fichier unique pour éviter les conflits
    String newFilename = UUID.randomUUID().toString() + extension;

    // 3. Détermination du dossier de destination
    Path targetLocation = getTargetLocation(fileType).resolve(newFilename);

    // 4. Copie du fichier vers la destination
    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

    return newFilename;
  }

  /**
   * ✅ Méthode privée pour vérifier si une extension est autorisée pour un type de fichier donné.
   */
  private boolean isExtensionSupported(String extension, String fileType) {
    switch (fileType.toLowerCase()) {
      case "cv":
      case "cvchoisi":
      case "lettremotivation":
        return ALLOWED_DOCUMENT_EXTENSIONS.contains(extension);

      case "logo":
      case "image":
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension);

      default:
        return false; // Par sécurité, on refuse tout ce qu'on ne connaît pas
    }
  }

  /**
   * Retourne le chemin du dossier de destination en fonction du type de fichier.
   */
  private Path getTargetLocation(String type) {
    switch (type.toLowerCase()) {
      case "cv":
      case "cvchoisi":
        return this.cvStorageLocation;
      case "logo":
      case "image":
        return this.logoStorageLocation;
      case "lettremotivation":
        return this.lettreStorageLocation;
      default:
        throw new IllegalArgumentException("Type de fichier non pris en charge pour le stockage : " + type);
    }
  }

  /**
   * Retourne le chemin complet d'un fichier stocké.
   */
  public Path getFilePath(String fileName, String type) {
    return getTargetLocation(type).resolve(fileName).normalize();
  }
}
