package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.IAuthService;
import com.example.streetleague.dto.AuthResponse;
import com.example.streetleague.dto.LoginRequest;
import com.example.streetleague.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        var saved = authService.register(req);
        return ResponseEntity.ok("User created: " + saved.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }


}

