package com.example.streetleague.dto;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;

import java.time.LocalDateTime;

public record MatchResponse(
        Long idMatch,
        LocalDateTime matchDate,
        String location,
        MatchStatus status,
        Integer scoreTeamA,
        Integer scoreTeamB,
        String teamAName,
        String teamBName,
        String createdByEmail,
        String captainAName,    // ← Ajout
        String captainBName, // ← Ajout
        String captainAEmail,   // ← ajouter
        String captainBEmail
) {
    public static MatchResponse fromEntity(Match match) {
        return new MatchResponse(
                match.getIdMatch(),
                match.getMatchDate(),
                match.getLocation(),
                match.getStatus(),
                match.getScoreTeamA(),
                match.getScoreTeamB(),
                match.getTeamA().getName(),
                match.getTeamB().getName(),
                match.getCreatedBy().getEmail(),
                match.getTeamA().getCaptain() != null ? match.getTeamA().getCaptain().getFullName() : "N/A",  // ← Ajout
                match.getTeamB().getCaptain() != null ? match.getTeamB().getCaptain().getFullName() : "N/A" ,  // ← Ajout
                match.getTeamA().getCaptain() != null ? match.getTeamA().getCaptain().getEmail() : null,  // ← ajouter
                match.getTeamB().getCaptain() != null ? match.getTeamB().getCaptain().getEmail() : null   // ← ajoute
        );
    }

}