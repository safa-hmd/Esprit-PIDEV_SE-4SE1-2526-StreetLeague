package com.example.streetleague.dto;

import java.time.LocalDateTime;

public record LeaderboardDto(
        Long teamId,
        String teamName,
        String sport,
        String captainFullName,   // JOIN Team → User (captain)
        String captainEmail,
        int victories,
        int defeats,
        int matches,
        int points,               // (victories × 3) + matches - defeats
        LocalDateTime lastMatchDate,  // JOIN Team → Match
        String lastOpponentName       // nom du dernier adversaire
) {}
