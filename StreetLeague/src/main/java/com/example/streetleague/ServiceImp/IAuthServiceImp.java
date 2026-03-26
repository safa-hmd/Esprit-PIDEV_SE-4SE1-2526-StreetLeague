package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.IAuthService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.AuthResponse;
import com.example.streetleague.dto.CompleteGoogleRegisterRequest;
import com.example.streetleague.dto.LoginRequest;
import com.example.streetleague.dto.RegisterRequest;
import com.example.streetleague.security.CustomUserDetailsService;
import com.example.streetleague.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
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

        User user = userRepository.findByEmail(req.email()).orElseThrow();


        return new AuthResponse(token, userDetails.getUsername(), role, (Long) user.getIdUser());
    }



    @Override
    public AuthResponse completeGoogleRegister(CompleteGoogleRegisterRequest req) {

        // Vérifier que l'utilisateur n'existe pas déjà
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }

        // Créer l'utilisateur avec le rôle choisi
        User user = User.builder()
                .email(req.email())
                .fullName(req.fullName())
                .password("GOOGLE_OAUTH2_NO_PASSWORD")
                .role(req.role())
                .enabled(true)
                .build();

        userRepository.save(user);

        // Générer le JWT
        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(), "ROLE_" + user.getRole().name(), user.getIdUser());
    }


}