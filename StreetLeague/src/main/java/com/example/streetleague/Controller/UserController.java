package com.example.streetleague.Controller;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<?>> getUsersByTeam(@PathVariable Long teamId) {
        List<User> users = userRepository.findByTeamId(teamId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        try {
            System.out.println("==> Recherche par email: " + email);

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            System.out.println("==> Trouvé: id=" + user.getIdUser()
                    + " teamId=" + user.getTeamId());

            Map<String, Object> result = new HashMap<>();
            result.put("idUser",   user.getIdUser());
            result.put("id",       user.getIdUser());
            result.put("teamId",   user.getTeamId() != null ? user.getTeamId() : 0);
            result.put("role",     user.getRole());
            result.put("fullName", user.getFullName());
            result.put("email",    user.getEmail());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("Erreur getUserByEmail: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}