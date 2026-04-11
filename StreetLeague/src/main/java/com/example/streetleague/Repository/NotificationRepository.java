package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.user.email = :email ORDER BY n.createdAt DESC")
    List<Notification> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);

}
