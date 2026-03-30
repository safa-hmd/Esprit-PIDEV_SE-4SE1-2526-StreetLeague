package com.example.streetleague;

import com.example.streetleague.Entity.Notification;
import com.example.streetleague.Repository.NotificationRepository;
import com.example.streetleague.ServiceImp.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Test
    void sendNotification_SavesNotification() {
        notificationService.sendNotification(1L, "New Notification");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getMyNotifications_ReturnsList() {
        Notification n = new Notification();
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(n));

        List<Notification> result = notificationService.getMyNotifications(1L);
        assertEquals(1, result.size());
    }

    @Test
    void markAsRead_UpdatesIsRead() {
        Notification n = new Notification();
        n.setId(1L);
        n.setRead(false);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L);

        assertTrue(n.isRead());
        verify(notificationRepository).save(any(Notification.class));
    }
}
