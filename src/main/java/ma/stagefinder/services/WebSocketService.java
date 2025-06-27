package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import ma.stagefinder.dtos.NotificationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Hada dyal Lombok, kay'créer lina l'constructeur automatiquement
public class WebSocketService {

    // Hada howa l'outil l'asassi dyal Spring bach nsifto les messages WebSocket
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Had la méthode kat'sift notification l user we7d b'ضبط.
     * @param username L'identifiant l'unique dyal l'user (ghadi nsta3mlo l'email dyalo).
     * @param notification L'objet Notification li ghansifto.
     */
    public void sendNotificationToUser(String username, NotificationDTO notification) {
        try {
            System.out.println("Envoi de la notification WebSocket à l'utilisateur: " + username);

            // Hna kangolo: Sift had l'objet "notification" l l'utilisateur "username"
            // f l'9anat (channel) l'khassa dyalo li smitha "/queue/notifications".
            // L'frontend khasso ikoun m'abonné (kaytṣnet) l had l'9anat.
            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);

            System.out.println("Notification WebSocket envoyée avec succès.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification WebSocket: " + e.getMessage());
        }
    }
}