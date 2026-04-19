package com.example.streetleague.dto;

import com.example.streetleague.Entity.MatchStatus;
import java.time.LocalDateTime;

public record MatchHistoryDto(
        Long idMatch,
        LocalDateTime matchDate,
        String location,
        MatchStatus status,
        Integer scoreTeamA,
        Integer scoreTeamB,
        String teamAName,
        String teamBName,
        String captainAName,
        String captainBName,
        String sport,
        Integer eloTeamA,
        Integer eloTeamB
) {}