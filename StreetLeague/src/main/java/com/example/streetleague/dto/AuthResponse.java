package com.example.streetleague.dto;


public record AuthResponse(
        String token,
        String email,
        String role
) {}
