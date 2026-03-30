package com.example.streetleague;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceImp.EmailService;
import com.example.streetleague.ServiceImp.IAuthServiceImp;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.*;
import com.example.streetleague.security.CustomUserDetailsService;
import com.example.streetleague.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IAuthServiceImpTest {

    @InjectMocks private IAuthServiceImp authService;

    @Mock private UserRepository           userRepository;
    @Mock private PasswordEncoder          passwordEncoder;
    @Mock private AuthenticationManager    authenticationManager;
    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private JwtService               jwtService;
    @Mock private EmailService             emailService;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setIdUser(1L);
        savedUser.setEmail("test@test.com");
        savedUser.setFullName("Test User");
        savedUser.setPassword("encoded_password");
        savedUser.setRole(Role.PLAYER);
        savedUser.setEnabled(true);
    }

    // ── register ──────────────────────────────────────────────────────────

    @Test
    void registerTest() {
        RegisterRequest req = new RegisterRequest("Test User", "test@test.com", "password123", Role.PLAYER);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(savedUser);

        User result = authService.register(req);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void registerTest_emailAlreadyUsed() {
        RegisterRequest req = new RegisterRequest("Test User", "test@test.com", "password123", Role.PLAYER);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(savedUser));

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerTest_emailNull() {
        RegisterRequest req = new RegisterRequest("Test User", null, "password123", Role.PLAYER);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void registerTest_passwordTooShort() {
        RegisterRequest req = new RegisterRequest("Test User", "test@test.com", "abc", Role.PLAYER);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void registerTest_roleNull() {
        RegisterRequest req = new RegisterRequest("Test User", "test@test.com", "password123", null);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    // ── login ─────────────────────────────────────────────────────────────

    @Test
    void loginTest() {
        LoginRequest req = new LoginRequest("test@test.com", "password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@test.com")
                .password("encoded_password")
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"))
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt_token");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(savedUser));

        AuthResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("jwt_token",     res.token());
        assertEquals("test@test.com", res.email());
        assertEquals("ROLE_PLAYER",   res.role());
        assertEquals(1L,              res.idUser());
    }

    // ── completeGoogleRegister ────────────────────────────────────────────

    @Test
    void completeGoogleRegisterTest() {
        CompleteGoogleRegisterRequest req =
                new CompleteGoogleRegisterRequest("google@test.com", "Google User", Role.PLAYER);

        when(userRepository.findByEmail("google@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("google_jwt");

        AuthResponse res = authService.completeGoogleRegister(req);

        assertNotNull(res);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void completeGoogleRegisterTest_emailAlreadyUsed() {
        CompleteGoogleRegisterRequest req =
                new CompleteGoogleRegisterRequest("test@test.com", "Google User", Role.PLAYER);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(savedUser));

        assertThrows(IllegalArgumentException.class,
                () -> authService.completeGoogleRegister(req));
    }

    // ── forgotPassword ────────────────────────────────────────────────────

    @Test
    void forgotPasswordTest() {
        ForgotPasswordRequest req = new ForgotPasswordRequest("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any())).thenReturn(savedUser);
        doNothing().when(emailService).sendResetEmail(anyString(), anyString());

        authService.forgotPassword(req);

        verify(userRepository, times(1)).save(any());
        verify(emailService, times(1)).sendResetEmail(eq("test@test.com"), anyString());
        assertNotNull(savedUser.getResetToken());
        assertNotNull(savedUser.getResetTokenExpiry());
    }

    @Test
    void forgotPasswordTest_userNotFound() {
        ForgotPasswordRequest req = new ForgotPasswordRequest("unknown@test.com");
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> authService.forgotPassword(req));
        verify(userRepository, never()).save(any());
    }

    // ── resetPassword ─────────────────────────────────────────────────────

    @Test
    void resetPasswordTest() {
        savedUser.setResetToken("valid_token");
        savedUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequest req = new ResetPasswordRequest("valid_token", "newpassword123");

        when(userRepository.findByResetToken("valid_token")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new");
        when(userRepository.save(any())).thenReturn(savedUser);

        assertDoesNotThrow(() -> authService.resetPassword(req));

        assertNull(savedUser.getResetToken());
        assertNull(savedUser.getResetTokenExpiry());
        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    void resetPasswordTest_tokenInvalid() {
        ResetPasswordRequest req = new ResetPasswordRequest("bad_token", "newpassword123");
        when(userRepository.findByResetToken("bad_token")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword(req));
    }

    @Test
    void resetPasswordTest_tokenExpired() {
        savedUser.setResetToken("expired_token");
        savedUser.setResetTokenExpiry(LocalDateTime.now().minusMinutes(5));

        ResetPasswordRequest req = new ResetPasswordRequest("expired_token", "newpassword123");
        when(userRepository.findByResetToken("expired_token")).thenReturn(Optional.of(savedUser));

        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword(req));
    }

    @Test
    void resetPasswordTest_passwordTooShort() {
        savedUser.setResetToken("valid_token");
        savedUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

        ResetPasswordRequest req = new ResetPasswordRequest("valid_token", "abc");
        when(userRepository.findByResetToken("valid_token")).thenReturn(Optional.of(savedUser));

        assertThrows(IllegalArgumentException.class, () -> authService.resetPassword(req));
    }
}