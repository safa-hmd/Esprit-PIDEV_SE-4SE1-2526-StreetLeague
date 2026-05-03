package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MatchRequest {
    @NotBlank(message = "Team 1 is required")
    private String equipe1;

    @NotBlank(message = "Team 2 is required")
    private String equipe2;

    @NotNull(message = "Match date is required")
    @JsonProperty("dateMatch")
    private LocalDateTime dateMatch;

    private String location;

    @JsonProperty("terrainId")
    private Long terrainId;

    @JsonProperty("organisateurId")
    private Long organisateurId;

    private String typeMatch;
    private String description;
    @JsonProperty("dureeMatch")
    private Integer dureeMatch;
    private String statut;

    /** Convenience constructor used by ChallengeRequest flow */
    public MatchRequest(LocalDateTime matchDate, String location) {
        this.dateMatch = matchDate;
        this.location = location;
    }

    public LocalDateTime getMatchDate() { return this.dateMatch; }
    public String getLocation() { return this.location; }
}
