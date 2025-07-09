package ma.stagefinder.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable; // <-- 1. ZEDNA L'IMPORT
import java.time.LocalDateTime;

@Data
public class FavorisDTO implements Serializable { // <-- 2. ZEDNA HADI
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateAjout;
    private Long userId;
    private Long offreId;
}
