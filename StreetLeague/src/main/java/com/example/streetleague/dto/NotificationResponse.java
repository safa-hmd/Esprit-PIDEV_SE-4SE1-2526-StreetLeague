package com.example.streetleague.dto;

import com.example.streetleague.Entity.Notification;

public record NotificationResponse(
        Long idNotification,
        String message,
        boolean isRead,
        String createdAt
) {
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getIdNotification(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : ""
        );
    }
}
