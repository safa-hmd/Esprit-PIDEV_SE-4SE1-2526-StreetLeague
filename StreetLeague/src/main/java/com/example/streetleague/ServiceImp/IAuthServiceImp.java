package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.IAuthService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.AuthResponse;
import com.example.streetleague.dto.LoginRequest;
import com.example.streetleague.dto.RegisterRequest;
import com.example.streetleague.security.CustomUserDetailsService;
import com.example.streetleague.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Étape 10 : Implémentation du service d'authentification
 * Contient toute la logique métier : register, login, forgotPassword, resetPassword, editProfile
 */
@Service
@RequiredArgsConstructor
public class IAuthServiceImp implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;



    @Override
    public User register(RegisterRequest req) {
        // Validation des champs
        if (req.email() == null || req.email().isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }
        if (req.password() == null || req.password().length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters");
        }
        if (req.role() == null) {
            throw new IllegalArgumentException("Role required");
        }

        // Construire l'utilisateur avec mot de passe chiffré
        User u = User.builder()
                .fullName(req.fullName() == null ? "Not Available" : req.fullName())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .enabled(true)
                .build();

        return userRepository.save(u);
    }

   /* @Override
    public AuthResponse login(LoginRequest req) {
        // Spring Security vérifie email + mot de passe (lève une exception si invalide)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No roles found"))
                .getAuthority();

        return new AuthResponse(token, userDetails.getUsername(), role);
    }*/

   /* @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No roles found"))
                .getAuthority();

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        return new AuthResponse(user.getId(), token, userDetails.getUsername(), role);
    }*/

    @Override
    public AuthResponse login(LoginRequest req) {

        // 1️⃣ authentifier avec Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),
                        req.password()
                )
        );

        // 2️⃣ récupérer user depuis DB
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ charger UserDetails pour JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // 4️⃣ générer token
        String token = jwtService.generateToken(userDetails);

        // ⭐ 5️⃣ EXTRAIRE LE ROLE DEPUIS USER (IMPORTANT !!!)
        String role = user.getRole().name();   // ou user.getRole().toString()

        // 6️⃣ retourner réponse COMPLETE
        return new AuthResponse(
                user.getId(),
                token,
                user.getEmail(),
                role
        );
    }

}