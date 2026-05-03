package com.example.streetleague.dto;

import java.time.LocalDateTime;

public record MatchRequest(
        LocalDateTime matchDate,
        String location
) {}