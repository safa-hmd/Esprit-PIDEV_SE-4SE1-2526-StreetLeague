package com.example.streetleague.dto;

// dto/ChallengeRequest.java


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChallengeRequest {
    private Long challengerTeamId;
    private Long opponentTeamId;
    private String location;
    private LocalDateTime matchDate;

    // Constructeur par défaut
    public ChallengeRequest() {}

    // Getters et Setters
    public Long getChallengerTeamId() { return challengerTeamId; }
    public void setChallengerTeamId(Long challengerTeamId) { this.challengerTeamId = challengerTeamId; }

    public Long getOpponentTeamId() { return opponentTeamId; }
    public void setOpponentTeamId(Long opponentTeamId) { this.opponentTeamId = opponentTeamId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
}