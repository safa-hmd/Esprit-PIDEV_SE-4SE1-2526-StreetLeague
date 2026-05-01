package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Notification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Toutes les notifs d'un user, triées du plus récent au plus ancien */
    List<Notification> findByUser_IdUserOrderByCreatedAtDesc(Long idUser);

    /** Compte les notifs non lues d'un user */
    long countByUser_IdUserAndIsReadFalse(Long idUser);

    /** Cherche une notif par son id + vérifie l'ownership par email */
    Optional<Notification> findByIdNotificationAndUser_Email(Long idNotification, String email);

    /** Charge une notif par son id avec le user associé (évite LazyInitializationException) */
    @EntityGraph(attributePaths = {"user"})
    Optional<Notification> findByIdNotification(Long idNotification);
}
