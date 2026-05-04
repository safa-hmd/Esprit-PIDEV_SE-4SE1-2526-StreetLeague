package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.IUserService;
import com.example.streetleague.dto.ChangePasswordRequest;
import com.example.streetleague.dto.UpdateProfileRequest;
import com.example.streetleague.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // GET /user/profile  → retourne le profil de l'utilisateur connecté
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    // PUT /user/profile  → modifie le nom
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Principal principal,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Principal principal,
            @RequestBody  ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteAccount(Principal principal) {
        userService.deleteAccount(principal.getName());
        return ResponseEntity.ok("Compte supprimé");
    }
}