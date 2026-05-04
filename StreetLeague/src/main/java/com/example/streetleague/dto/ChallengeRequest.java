package com.example.streetleague.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ChallengeRequest {
    private Long challengerTeamId;
    private Long opponentTeamId;
    private String location;
    private LocalDateTime matchDate;

    public ChallengeRequest() {}

    public ChallengeRequest(Long challengerTeamId, Long opponentTeamId, String location, LocalDateTime matchDate) {
        this.challengerTeamId = challengerTeamId;
        this.opponentTeamId = opponentTeamId;
        this.location = location;
        this.matchDate = matchDate;
    }

    public Long getChallengerTeamId() { return challengerTeamId; }
    public void setChallengerTeamId(Long challengerTeamId) { this.challengerTeamId = challengerTeamId; }

    public Long getOpponentTeamId() { return opponentTeamId; }
    public void setOpponentTeamId(Long opponentTeamId) { this.opponentTeamId = opponentTeamId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeRequest that = (ChallengeRequest) o;
        return Objects.equals(challengerTeamId, that.challengerTeamId) && Objects.equals(opponentTeamId, that.opponentTeamId) && Objects.equals(location, that.location) && Objects.equals(matchDate, that.matchDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(challengerTeamId, opponentTeamId, location, matchDate);
    }

    @Override
    public String toString() {
        return "ChallengeRequest{" +
                "challengerTeamId=" + challengerTeamId +
                ", opponentTeamId=" + opponentTeamId +
                ", location='" + location + '\'' +
                ", matchDate=" + matchDate +
                '}';
    }

    public static ChallengeRequestBuilder builder() {
        return new ChallengeRequestBuilder();
    }

    public static class ChallengeRequestBuilder {
        private Long challengerTeamId;
        private Long opponentTeamId;
        private String location;
        private LocalDateTime matchDate;

        public ChallengeRequestBuilder challengerTeamId(Long challengerTeamId) { this.challengerTeamId = challengerTeamId; return this; }
        public ChallengeRequestBuilder opponentTeamId(Long opponentTeamId) { this.opponentTeamId = opponentTeamId; return this; }
        public ChallengeRequestBuilder location(String location) { this.location = location; return this; }
        public ChallengeRequestBuilder matchDate(LocalDateTime matchDate) { this.matchDate = matchDate; return this; }

        public ChallengeRequest build() {
            return new ChallengeRequest(challengerTeamId, opponentTeamId, location, matchDate);
        }
    }
}