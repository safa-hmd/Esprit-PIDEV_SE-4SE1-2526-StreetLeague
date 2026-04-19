package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Notification;
import com.example.streetleague.Repository.NotificationRepository;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.NotificationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements InotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotificationForUsers(List<User> users, String message) {
        if (users == null || users.isEmpty()) return;

        List<Notification> notifications = users.stream()
                .filter(u -> u != null)
                .map(user -> Notification.builder()
                        .message(message)
                        .user(user)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByUser_IdUserOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Override
    public void markAsRead(Long idNotification, String email) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("You do not have permission to modify this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long idNotification, String email) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("You do not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }
}