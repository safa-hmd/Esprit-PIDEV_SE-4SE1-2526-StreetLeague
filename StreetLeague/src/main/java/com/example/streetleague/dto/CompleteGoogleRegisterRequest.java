package com.example.streetleague.dto;

import com.example.streetleague.domain.Role;

public record CompleteGoogleRegisterRequest(
        String email,
        String fullName,
        Role role
) {}