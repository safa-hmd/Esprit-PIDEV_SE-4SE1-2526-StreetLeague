package com.example.streetleague.dto;

import com.example.streetleague.Entity.Notification;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long idNotification,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getIdNotification(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
