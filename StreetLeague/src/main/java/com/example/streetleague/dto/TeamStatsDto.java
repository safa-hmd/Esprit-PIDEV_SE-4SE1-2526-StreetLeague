package com.example.streetleague.dto;

public record TeamStatsDto(
        int victories,
        int defeats,
        int matches,
        String email
) {}
