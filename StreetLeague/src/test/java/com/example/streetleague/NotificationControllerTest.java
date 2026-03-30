package com.example.streetleague;

import com.example.streetleague.Controller.NotificationController;
import com.example.streetleague.Entity.Notification;
import com.example.streetleague.ServiceInterface.NotificationService;
import com.example.streetleague.security.CustomUserDetailsService;
import com.example.streetleague.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMyNotifications_ReturnsList() throws Exception {
        Notification n = new Notification();
        n.setId(1L);
        n.setMessage("Test Notification");

        when(notificationService.getMyNotifications(1L)).thenReturn(List.of(n));

        mockMvc.perform(get("/api/notifications/my?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("Test Notification"));
    }

    @Test
    void getUnreadCount_ReturnsCount() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/unread-count?userId=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void markAsRead_ReturnsOk() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());
    }

    @Test
    void markAllRead_ReturnsOk() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/api/notifications/mark-all-read?userId=1"))
                .andExpect(status().isOk());
    }
}
