package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.StatutCandidature;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Data
public class CandidatureDTO {
  private Long id;
  private LocalDateTime dateCandidature;
  private StatutCandidature statutCandidature;
  private MultipartFile lettreMotivation;
  private MultipartFile cvChoisi;
  private Long userId;
  private Long offreId;
  private OffreDTO offre;
  private String userNom; // Nom du stagiaire
  private String userEmail; // Email du stagiaire
  private String cvFileName; // Nom du fichier CV stocké
  private String lettreMotivationFileName; // Nom du fichier lettre de motivation stocké
}
