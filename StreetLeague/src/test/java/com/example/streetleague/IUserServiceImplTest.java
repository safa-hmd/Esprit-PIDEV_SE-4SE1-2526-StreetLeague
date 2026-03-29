package com.example.streetleague;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceImp.IUserServiceImpl;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.ChangePasswordRequest;
import com.example.streetleague.dto.UpdateProfileRequest;
import com.example.streetleague.dto.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IUserServiceImplTest {

    @InjectMocks private IUserServiceImpl userService;
    @Mock private UserRepository  userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(1L);
        user.setEmail("player@test.com");
        user.setFullName("Player One");
        user.setPassword("encoded_password");
        user.setRole(Role.PLAYER);
    }

    // ── getProfile ────────────────────────────────────────────────────────

    @Test
    void getProfileTest() {
        when(userRepository.findByEmail("player@test.com")).thenReturn(Optional.of(user));

        UserProfileResponse res = userService.getProfile("player@test.com");

        assertNotNull(res);
        assertEquals(1L,                res.getIdUser());
        assertEquals("Player One",      res.getFullName());
        assertEquals("player@test.com", res.getEmail());
        assertEquals("PLAYER",          res.getRole());
    }

    @Test
    void getProfileTest_userNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getProfile("unknown@test.com"));
    }

    // ── updateProfile ─────────────────────────────────────────────────────

    @Test
    void updateProfileTest() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFullName("New Name");

        when(userRepository.findByEmail("player@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserProfileResponse res = userService.updateProfile("player@test.com", req);

        assertNotNull(res);
        assertEquals("New Name", user.getFullName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateProfileTest_userNotFound() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFullName("New Name");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.updateProfile("unknown@test.com", req));
    }

    // ── changePassword ────────────────────────────────────────────────────

    @Test
    void changePasswordTest() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("current");
        req.setNewPassword("newpassword");

        when(userRepository.findByEmail("player@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current", "encoded_password")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encoded_new");
        when(userRepository.save(any())).thenReturn(user);

        assertDoesNotThrow(() -> userService.changePassword("player@test.com", req));

        assertEquals("encoded_new", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changePasswordTest_wrongCurrentPassword() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("wrong");
        req.setNewPassword("newpassword");

        when(userRepository.findByEmail("player@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded_password")).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> userService.changePassword("player@test.com", req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordTest_userNotFound() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("current");
        req.setNewPassword("newpassword");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.changePassword("unknown@test.com", req));
    }

    // ── deleteAccount ─────────────────────────────────────────────────────

    @Test
    void deleteAccountTest() {
        when(userRepository.findByEmail("player@test.com")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteAccount("player@test.com"));

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteAccountTest_userNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.deleteAccount("unknown@test.com"));
        verify(userRepository, never()).delete(any());
    }
}