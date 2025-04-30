package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.NotificationDTO;
import ma.stagefinder.entities.Notification;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.NotificationRepository;
import ma.stagefinder.repositories.UserRepository;

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
    public NotificationDTO getNotificationById(Long id) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new Exception("Notification avec id " + id + " introuvable"));

            return EntityMapper.INSTANCE.toNotificationDTO(notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO) {
        try {
            Optional<Notification> optionalNotification = notificationRepository.findById(id);

            if (optionalNotification.isPresent()) {
                Notification notification = optionalNotification.get();

                // mise à jour des champs
                notification.setMessage(notificationDTO.getMessage());
                notification.setDateEnvoie(notificationDTO.getDateEnvoie());

                // on pourrait aussi updater user si بغيتي
                // par exemple :
                // User user = userRepository.findById(notificationDTO.getUserId()).orElse(null);
                // notification.setUser(user);

                // save
                Notification updatedNotification = notificationRepository.save(notification);

                return EntityMapper.INSTANCE.toNotificationDTO(updatedNotification);

            } else {
                System.out.println("Notification avec id " + id + " introuvable.");
                return null;
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            throw new RuntimeException("Erreur inattendue lors de l'update.");
        }
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        try {
            List<Notification> notifications = notificationRepository.findByUserId(userId);

            return notifications.stream()
                    .map(EntityMapper.INSTANCE::toNotificationDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des notifications : " + e.getMessage());
            throw new RuntimeException("Impossible de récupérer les notifications.");
        }
    }




}
