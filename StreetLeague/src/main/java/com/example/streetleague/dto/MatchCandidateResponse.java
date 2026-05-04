package com.example.streetleague.dto;

import java.util.Objects;

public class MatchCandidateResponse {
    private Long    teamId;
    private String  teamName;
    private String  sport;
    private Integer eloScore;
    private Double  eloFitScore;             // composante ELO  (0–100)
    private Double  h2hScore;                // composante H2H  (0–100)
    private Double  matchCompatibilityScore; // score final pondéré

    public MatchCandidateResponse() {
    }

    public MatchCandidateResponse(Long teamId, String teamName, String sport, Integer eloScore, Double eloFitScore, Double h2hScore, Double matchCompatibilityScore) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.sport = sport;
        this.eloScore = eloScore;
        this.eloFitScore = eloFitScore;
        this.h2hScore = h2hScore;
        this.matchCompatibilityScore = matchCompatibilityScore;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public Integer getEloScore() {
        return eloScore;
    }

    public void setEloScore(Integer eloScore) {
        this.eloScore = eloScore;
    }

    public Double getEloFitScore() {
        return eloFitScore;
    }

    public void setEloFitScore(Double eloFitScore) {
        this.eloFitScore = eloFitScore;
    }

    public Double getH2hScore() {
        return h2hScore;
    }

    public void setH2hScore(Double h2hScore) {
        this.h2hScore = h2hScore;
    }

    public Double getMatchCompatibilityScore() {
        return matchCompatibilityScore;
    }

    public void setMatchCompatibilityScore(Double matchCompatibilityScore) {
        this.matchCompatibilityScore = matchCompatibilityScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchCandidateResponse that = (MatchCandidateResponse) o;
        return Objects.equals(teamId, that.teamId) && Objects.equals(teamName, that.teamName) && Objects.equals(sport, that.sport) && Objects.equals(eloScore, that.eloScore) && Objects.equals(eloFitScore, that.eloFitScore) && Objects.equals(h2hScore, that.h2hScore) && Objects.equals(matchCompatibilityScore, that.matchCompatibilityScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, teamName, sport, eloScore, eloFitScore, h2hScore, matchCompatibilityScore);
    }

    @Override
    public String toString() {
        return "MatchCandidateResponse{" +
                "teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", matchCompatibilityScore=" + matchCompatibilityScore +
                '}';
    }

    public static MatchCandidateResponseBuilder builder() {
        return new MatchCandidateResponseBuilder();
    }

    public static class MatchCandidateResponseBuilder {
        private Long    teamId;
        private String  teamName;
        private String  sport;
        private Integer eloScore;
        private Double  eloFitScore;
        private Double  h2hScore;
        private Double  matchCompatibilityScore;

        public MatchCandidateResponseBuilder teamId(Long teamId) { this.teamId = teamId; return this; }
        public MatchCandidateResponseBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public MatchCandidateResponseBuilder sport(String sport) { this.sport = sport; return this; }
        public MatchCandidateResponseBuilder eloScore(Integer eloScore) { this.eloScore = eloScore; return this; }
        public MatchCandidateResponseBuilder eloFitScore(Double eloFitScore) { this.eloFitScore = eloFitScore; return this; }
        public MatchCandidateResponseBuilder h2hScore(Double h2hScore) { this.h2hScore = h2hScore; return this; }
        public MatchCandidateResponseBuilder matchCompatibilityScore(Double matchCompatibilityScore) { this.matchCompatibilityScore = matchCompatibilityScore; return this; }

        public MatchCandidateResponse build() {
            return new MatchCandidateResponse(teamId, teamName, sport, eloScore, eloFitScore, h2hScore, matchCompatibilityScore);
        }
    }
}