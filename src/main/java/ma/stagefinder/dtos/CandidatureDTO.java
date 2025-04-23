package ma.stagefinder.dtos;

import lombok.Data;
import ma.stagefinder.entities.enums.StatutCandidature;

import java.time.LocalDateTime;

@Data
public class CandidatureDTO {
    private Long id;
    private LocalDateTime dateCandidature;
    private StatutCandidature statutCandidature;
    private String lettreMotivation;
    private String cvChoisi;
    private Long userId;
    private Long offreId;
}
