package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Notification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_IdUserOrderByCreatedAtDesc(Long idUser);

    // Trouve une notification par son id avec le user (pour vérifier ownership)
    Optional<Notification> findByIdNotificationAndUser_Email(Long idNotification, String email);

    // Trouve une notification par id avec son user chargé
    @EntityGraph(attributePaths = {"user"})
    Optional<Notification> findByIdNotification(Long idNotification);
}