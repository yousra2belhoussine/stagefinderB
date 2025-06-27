package ma.stagefinder.services;

import jakarta.transaction.Transactional;
import ma.stagefinder.dtos.NotificationDTO;
import ma.stagefinder.entities.Notification;
import ma.stagefinder.entities.User;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.NotificationRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository; // Kan7tajoh bach njebdo l'user f addNotification
    private final EntityMapper entityMapper = EntityMapper.INSTANCE;

    // Constructeur bach n'injectiw les dépendances
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(entityMapper::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO addNotification(NotificationDTO notificationDTO) throws Exception {
        try {
            // L'7el dyal mochkil "user_id = null"
            if (notificationDTO.getUserId() == null) {
                throw new Exception("Impossible de créer une notification sans userId.");
            }
            User user = userRepository.findById(notificationDTO.getUserId())
                    .orElseThrow(() -> new Exception("Utilisateur introuvable avec l'ID : " + notificationDTO.getUserId()));

            Notification notification = entityMapper.toNotification(notificationDTO);
            notification.setUser(user); // L'khotwa l'mohimma

            Notification savedNotification = notificationRepository.save(notification);
            return entityMapper.toNotificationDTO(savedNotification);

        } catch (Exception e) {
            throw new Exception("Erreur lors de l'ajout de la notification : " + e.getMessage());
        }
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        // Hna kansta3mlo la méthode b l'ordering
        List<Notification> notifications = notificationRepository.findByUserIdOrderByDateEnvoieDesc(userId);
        return notifications.stream()
                .map(entityMapper::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserIdAndIsReadFalse(Long userId) {
        // Hna kansta3mlo la méthode b smiya s7i7a
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsReadByUser(Long userId) {
        // Hna kansta3mlo la méthode b smiya s7i7a
        notificationRepository.markAllNotificationsAsReadByUserId(userId);
    }

    @Override
    @Scheduled(fixedRate = 300000) // Kol 5 min
    @Transactional
    public void deleteOldNotifications() {
        // Hna kansta3mlo la méthode jdida li zedti f l'repository
// L'CODE L'JDID
        LocalDateTime dateLimit = LocalDateTime.now().minusMinutes(5);
        System.out.println("Nettoyage des anciennes notifications avant : " + dateLimit);
        notificationRepository.deleteOldNotifications(dateLimit);
    }
}