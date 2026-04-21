package com.example.streetleague.dto;

import com.example.streetleague.Entity.MatchStatus;

import java.time.LocalDateTime;

public record MatchUpdateRequest(
        Long idMatch,
        LocalDateTime matchDate,
        String location,
        MatchStatus status,
        Integer scoreTeamA,
        Integer scoreTeamB
) {}