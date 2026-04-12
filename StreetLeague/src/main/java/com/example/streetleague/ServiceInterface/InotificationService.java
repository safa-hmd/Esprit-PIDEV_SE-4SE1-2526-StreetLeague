package com.example.streetleague.ServiceInterface;

import com.example.streetleague.domain.User;
import com.example.streetleague.dto.NotificationResponse;

import java.util.List;

public interface InotificationService {
    void createNotificationForUsers(List<User> users, String message);
    List<NotificationResponse> getMyNotifications(Long userId);
    void markAsRead(Long idNotification, String email);
}