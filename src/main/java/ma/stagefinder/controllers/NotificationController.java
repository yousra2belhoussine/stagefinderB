package ma.stagefinder.controllers;
import lombok.AllArgsConstructor;
import ma.stagefinder.services.NotificationService;
import ma.stagefinder.dtos.NotificationDTO;

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

    @GetMapping("/notification/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        try {
            NotificationDTO notificationDTO = notificationService.getNotificationById(id);
            return ResponseEntity.ok(notificationDTO);
        } catch (Exception e) {
            // Si l'exception est levée, retourner un 404 avec le message
            return ResponseEntity.status(404).body(e.getMessage());
        }
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

    @PutMapping("/update/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO) {
        NotificationDTO updatedNotif = notificationService.updateNotification(id, notificationDTO);
        if (updatedNotif != null) {
            return ResponseEntity.ok(updatedNotif);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }




}
