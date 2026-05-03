package com.example.streetleague.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {}