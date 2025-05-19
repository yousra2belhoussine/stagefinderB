package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.NotificationDTO;
import ma.stagefinder.entities.Notification;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.NotificationRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements  NotificationService {

    private final NotificationRepository notificationRepository;


    @Override
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(EntityMapper.INSTANCE::toNotificationDTO)
                .collect(Collectors.toList());
    }



    @Override
    public NotificationDTO addNotification(NotificationDTO notificationDTO) throws Exception {
        try {
            // Mapping DTO -> Entity
            Notification notification = EntityMapper.INSTANCE.toNotification(notificationDTO);

            // Sauvegarde f la DB
            Notification savedNotification = notificationRepository.save(notification);

            // Retourner le résultat mapped en DTO
            return EntityMapper.INSTANCE.toNotificationDTO(savedNotification);

        } catch (Exception e) {
            throw new Exception("Erreur lors de l'ajout de la notification : " + e.getMessage());
        }
    }

    @Override
    public void deleteNotification(Long id) {
        try {
            Optional<Notification> notificationOptional = notificationRepository.findById(id);
            if (notificationOptional.isPresent()) {
                notificationRepository.delete(notificationOptional.get());
                System.out.println("Notification supprimée avec succès !");
            } else {
                System.out.println("Notification avec id " + id + " introuvable.");
            }
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de la notification.");
        }
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(EntityMapper.INSTANCE::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserIdAndIsReadFalse(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }


}
