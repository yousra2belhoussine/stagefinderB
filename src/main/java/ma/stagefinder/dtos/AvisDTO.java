package ma.stagefinder.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvisDTO {
    private Long id;
    private String commentaire;
    private LocalDateTime datePublication;
    private Long auteurId;
    private Long destinataireId;
    private Long offreId;
    private int note;
}
