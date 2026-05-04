package com.example.streetleague.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class SubmitResultDTO {

    @NotNull
    private Long winnerId;       // id de la Team ou du User vainqueur

    @NotNull
    private Boolean winnerIsTeam; // true = TEAM, false = PLAYER

    // Optionnel : scores pour le Match
    private Integer scoreA;
    private Integer scoreB;

    public SubmitResultDTO() {
    }

    public SubmitResultDTO(Long winnerId, Boolean winnerIsTeam, Integer scoreA, Integer scoreB) {
        this.winnerId = winnerId;
        this.winnerIsTeam = winnerIsTeam;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmitResultDTO that = (SubmitResultDTO) o;
        return Objects.equals(winnerId, that.winnerId) && Objects.equals(winnerIsTeam, that.winnerIsTeam) && Objects.equals(scoreA, that.scoreA) && Objects.equals(scoreB, that.scoreB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winnerId, winnerIsTeam, scoreA, scoreB);
    }

    @Override
    public String toString() {
        return "SubmitResultDTO{" +
                "winnerId=" + winnerId +
                ", winnerIsTeam=" + winnerIsTeam +
                ", scoreA=" + scoreA +
                ", scoreB=" + scoreB +
                '}';
    }

    public static SubmitResultDTOBuilder builder() {
        return new SubmitResultDTOBuilder();
    }

    public static class SubmitResultDTOBuilder {
        private Long winnerId;
        private Boolean winnerIsTeam;
        private Integer scoreA;
        private Integer scoreB;

        public SubmitResultDTOBuilder winnerId(Long winnerId) { this.winnerId = winnerId; return this; }
        public SubmitResultDTOBuilder winnerIsTeam(Boolean winnerIsTeam) { this.winnerIsTeam = winnerIsTeam; return this; }
        public SubmitResultDTOBuilder scoreA(Integer scoreA) { this.scoreA = scoreA; return this; }
        public SubmitResultDTOBuilder scoreB(Integer scoreB) { this.scoreB = scoreB; return this; }

        public SubmitResultDTO build() {
            return new SubmitResultDTO(winnerId, winnerIsTeam, scoreA, scoreB);
        }
    }
}
