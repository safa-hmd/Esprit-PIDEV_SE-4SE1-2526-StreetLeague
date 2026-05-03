package com.example.streetleague.dto;

import com.example.streetleague.Entity.MatchStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchUpdateRequest {

    private Long idMatch;

    private String equipe1;
    private String equipe2;

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

    private MatchStatus status;

    @JsonProperty("scoreTeamA")
    private Integer scoreTeamA;

    @JsonProperty("scoreTeamB")
    private Integer scoreTeamB;

    // Aliases kept for backwards compat
    public Integer getScore1() { return this.scoreTeamA; }
    public Integer getScore2() { return this.scoreTeamB; }

    public LocalDateTime getMatchDate() { return this.dateMatch; }
    public String getLocation()         { return this.location; }
}
