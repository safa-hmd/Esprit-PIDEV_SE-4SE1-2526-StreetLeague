package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String message);
    List<Notification> getMyNotifications(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
