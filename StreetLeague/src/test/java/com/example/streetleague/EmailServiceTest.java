package com.example.streetleague;

import com.example.streetleague.ServiceImp.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks private EmailService emailService;
    @Mock private JavaMailSender mailSender;

    // ── sendResetEmail ────────────────────────────────────────────────────

    @Test
    void sendResetEmailTest() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendResetEmail(
                        "user@test.com",
                        "http://localhost:4200/reset-password?token=abc123"
                ));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetEmailTest_correctRecipientAndSubject() {
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendResetEmail("user@test.com", "http://reset-link");

        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertNotNull(sent.getTo());
        assertEquals("user@test.com", sent.getTo()[0]);
        assertEquals("StreetLeague — Réinitialisation de mot de passe", sent.getSubject());
        assertTrue(sent.getText().contains("http://reset-link"));
    }

    @Test
    void sendResetEmailTest_mailSenderThrows() {
        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () ->
                emailService.sendResetEmail("user@test.com", "http://reset-link"));
    }
}