package com.example.streetleague.dto;

import com.example.streetleague.Entity.Level;

public record TeamRequest(
        String name,
        String sport,
        String description,
        Level level
) {}