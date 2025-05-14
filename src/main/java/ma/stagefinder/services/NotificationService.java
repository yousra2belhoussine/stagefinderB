package ma.stagefinder.services;

import ma.stagefinder.dtos.NotificationDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotifications();

    // Méthode pour récupérer une notification par son id
    NotificationDTO getNotificationById(Long id);

    NotificationDTO addNotification(NotificationDTO notificationDTO) throws Exception;

    void deleteNotification(Long id);

    NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO);


    List<NotificationDTO> getNotificationsByUserId(Long userId);
}
