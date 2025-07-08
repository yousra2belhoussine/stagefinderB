package ma.stagefinder.dtos;

import lombok.Data;

import java.io.Serializable; // <-- 1. ZEDNA L'IMPORT
import java.time.LocalDateTime;

@Data
public class FavorisDTO implements Serializable { // <-- 2. ZEDNA HADI
    private Long id;
    private LocalDateTime dateAjout;
    private Long userId;
    private Long offreId;
}
