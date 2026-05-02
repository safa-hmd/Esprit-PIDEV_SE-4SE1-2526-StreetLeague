package com.example.streetleague.Controller;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.User;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.dto.NotificationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("notification")
public class NotificationController {

    private final InotificationService notificationService;
    private final UserRepository userRepository;

    // GET /notification/my?email=player@mail.com
    @GetMapping("my")
    public List<NotificationResponse> getMyNotifications(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();
        return notificationService.getMyNotifications(user.getIdUser());
    }

    // PUT /notification/1/read?email=player@mail.com
    @PutMapping("{idNotification}/read")
    public void markAsRead(@PathVariable Long idNotification, @RequestParam String email) {
        notificationService.markAsRead(idNotification, email);
    }

    // DELETE /notification/1/delete?email=player@mail.com
    @DeleteMapping("{idNotification}/delete")
    public void deleteNotification(@PathVariable Long idNotification, @RequestParam String email) {
        notificationService.deleteNotification(idNotification, email);
    }


    // GET /notifications/{id}/redirect?email=user@mail.com
    @GetMapping("/{id}/redirect")
    public ResponseEntity<Map<String, String>> getRedirectTarget(
            @PathVariable Long id,
            @RequestParam String email) {

        // Marquer comme lu
        notificationService.markAsRead(id, email);

        // Récupérer la notification
        NotificationResponse notif = notificationService.getNotificationById(id);

        // Déterminer la page cible selon le contenu du message
        String target = resolveTarget(notif.message());

        return ResponseEntity.ok(Map.of(
                "redirectTo", target,
                "message", notif.message()
        ));
    }

    private String resolveTarget(String message) {
        if (message == null) return "/client/teams";

        String msg = message.toLowerCase();

        if (msg.contains("match accepted") || msg.contains("match rejected") ||
                msg.contains("match updated") || msg.contains("match cancelled") ||
                msg.contains("new match"))
            return "/client/teams?tab=matches";

        if (msg.contains("training"))
            return "/client/trainings";

        if (msg.contains("team"))
            return "/client/teams";

        return "/client/home";
    }
}