package com.example.streetleague.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

//@Data
public class SubmitResultDTO {

    @NotNull
    private Long winnerId;       // id de la Team ou du User vainqueur

    @NotNull
    private Boolean winnerIsTeam; // true = TEAM, false = PLAYER

    // Optionnel : scores pour le Match
    private Integer scoreA;
    private Integer scoreB;

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public Boolean getWinnerIsTeam() {
        return winnerIsTeam;
    }

    public void setWinnerIsTeam(Boolean winnerIsTeam) {
        this.winnerIsTeam = winnerIsTeam;
    }

    public Integer getScoreA() {
        return scoreA;
    }

    public void setScoreA(Integer scoreA) {
        this.scoreA = scoreA;
    }

    public Integer getScoreB() {
        return scoreB;
    }

    public void setScoreB(Integer scoreB) {
        this.scoreB = scoreB;
    }
}
