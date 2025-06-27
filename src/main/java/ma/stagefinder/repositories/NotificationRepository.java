package ma.stagefinder.repositories;

import jakarta.transaction.Transactional;
import ma.stagefinder.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <-- Zedt had l'import bach l'code ikoun kter wadeh
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // IḌAFA: Zedt OrderByDateEnvoieDesc bach dima tjib lik les notifs men l'jdida l l'qdima. A7ssan l'l'utilisateur.
    List<Notification> findByUserIdOrderByDateEnvoieDesc(Long userId);

    // TAṢ7I7 1: Smiya dyal l'champ hiya 'isRead', ماشي 'read'. W khass l'7arf l'wel ikon majuscule: IsRead
    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Transactional
    // TAṢ7I7 2: Hna hta howa, sta3mel 'n.isRead' blast 'n.read'.
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllNotificationsAsReadByUserId(@Param("userId") Long userId);

    // Had la méthode li konti derti qdima kanet mezyana, rje3tha lik.
    @Modifying
     @Transactional
     @Query("DELETE FROM Notification n WHERE n.dateEnvoie <= :dateLimit")
     void deleteOldNotifications(@Param("dateLimit") LocalDateTime dateLimit);
}