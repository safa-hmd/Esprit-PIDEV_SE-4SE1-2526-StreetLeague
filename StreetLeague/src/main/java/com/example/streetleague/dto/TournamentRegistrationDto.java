package com.example.streetleague.dto;

import com.example.streetleague.Entity.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentRegistrationDto {

    private Long id;

    private Long tournamentId;
    private String tournamentName;

    // Renseigné si tournoi INDIVIDUAL
    private Long playerId;
    private String playerUsername;

    // Renseigné si tournoi TEAM
    private Long teamId;
    private String teamName;

    private RegistrationStatus status;

    private LocalDateTime registeredAt;
}
