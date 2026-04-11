package com.example.streetleague.ServiceInterface;

import com.example.streetleague.domain.User;
import com.example.streetleague.dto.NotificationResponse;

import java.util.List;
import java.util.Set;

public interface InotificationService {
    void createNotificationForUsers(List<User> users, String message);
    List<NotificationResponse> getMyNotifications(String email);
    void markAsRead(Long idNotification, String email);
}
