package com.example.streetleague.dto;

import com.example.streetleague.Entity.Level;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.domain.User;

import java.time.LocalDate;
import java.util.List;

public record TeamResponse(
        Long idTeam,
        String name,
        String sport,
        String description,
        Level level,
        LocalDate creationDate,
        String captainEmail,
        Long captainId,
        String captainFullName,
        com.example.streetleague.domain.Role captainRole,
        int playerCount,
        List<String> playerEmails,
        int victories,
        int defeats,
        int matches
) {
    public static TeamResponse fromEntity(Team team) {
        return new TeamResponse(
                team.getIdTeam(),
                team.getName(),
                team.getSport(),
                team.getDescription(),
                team.getLevel(),
                team.getCreationDate(),
                team.getCaptain().getEmail(),
                team.getCaptain().getIdUser(),
                team.getCaptain().getFullName(),
                team.getCaptain().getRole(),
                team.getPlayers() == null ? 0 : team.getPlayers().size(),
                team.getPlayers() != null                          // ← null check
                        ? team.getPlayers().stream()
                        .map(User::getEmail)
                        .toList()
                        : List.of(),
                team.getVictories(),
                team.getDefeats(),
                team.getMatches()
        );
    }
}