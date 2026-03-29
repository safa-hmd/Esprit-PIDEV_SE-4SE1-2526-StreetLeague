package com.example.streetleague.dto;

import com.example.streetleague.domain.Role;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        Role role
) {}
