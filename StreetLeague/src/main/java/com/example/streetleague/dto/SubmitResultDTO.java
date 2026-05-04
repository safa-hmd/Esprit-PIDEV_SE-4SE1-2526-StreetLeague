package com.example.streetleague.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitResultDTO {

    @NotNull
    private Long winnerId;       // id de la Team ou du User vainqueur

    @NotNull
    private Boolean winnerIsTeam; // true = TEAM, false = PLAYER

    // Optionnel : scores pour le Match
    private Integer scoreA;
    private Integer scoreB;
}