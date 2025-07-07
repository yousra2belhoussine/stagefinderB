package ma.stagefinder.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavorisDTO {
    private Long id;
    private LocalDateTime dateAjout;
    private Long userId;
    private Long offreId;
}
