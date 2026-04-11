package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.dto.NotificationResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("notification")
public class NotificationController {

    private final InotificationService notificationService;

    // GET /notification/my?email=player@mail.com
    @GetMapping("my")
    public List<NotificationResponse> getMyNotifications(@RequestParam String email) {
        return notificationService.getMyNotifications(email);
    }

    // PUT /notification/1/read?email=player@mail.com
    @PutMapping("{idNotification}/read")
    public void markAsRead(@PathVariable Long idNotification, @RequestParam String email) {
        notificationService.markAsRead(idNotification, email);
    }
}
