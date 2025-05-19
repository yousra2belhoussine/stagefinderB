package ma.stagefinder.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private LocalDateTime dateEnvoie;
    private boolean read;
    private Long userId;
}
