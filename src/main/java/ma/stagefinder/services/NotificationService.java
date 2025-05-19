package ma.stagefinder.services;

import ma.stagefinder.dtos.NotificationDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getAllNotifications();
    NotificationDTO addNotification(NotificationDTO notificationDTO) throws Exception;
    void deleteNotification(Long id);
    List<NotificationDTO> getNotificationsByUserId(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    void markAllAsReadByUser(Long userId);
}
