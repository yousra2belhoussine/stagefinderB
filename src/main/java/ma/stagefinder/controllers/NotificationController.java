package ma.stagefinder.controllers;
import lombok.AllArgsConstructor;
import ma.stagefinder.services.NotificationService;
import ma.stagefinder.dtos.NotificationDTO;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public List<NotificationDTO> getAllNotifications() {
        return notificationService.getAllNotifications();
    }



    @PostMapping("/add")
    public ResponseEntity<?> addNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            NotificationDTO newNotification = notificationService.addNotification(notificationDTO);
            return ResponseEntity.ok(newNotification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout de la notification : " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification supprimée avec succès");
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(
            @PathVariable Long userId) {

        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count/{userId}")
    public ResponseEntity<Long> getUnreadNotificationsCount(@PathVariable Long userId) {
        long count = notificationService.countByUserIdAndIsReadFalse(userId);
        return ResponseEntity.ok(count);
    }

    // Endpoint باش تحدث جميع التنبيهات المقروءة ديال المستخدم
    @PutMapping("/mark-all-read/{userId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsReadByUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-old")
    public ResponseEntity<String> deleteOldNotifications() {
        notificationService.deleteOldNotifications();
        return ResponseEntity.ok("Old notifications deleted successfully.");
    }

}
