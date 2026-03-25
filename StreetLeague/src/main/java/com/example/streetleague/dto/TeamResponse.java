package com.example.streetleague.dto;

import com.example.streetleague.Entity.Level;
import com.example.streetleague.Entity.Team;

import java.time.LocalDate;

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
        int playerCount
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
                team.getPlayers() == null ? 0 : team.getPlayers().size()
        );
    }
}