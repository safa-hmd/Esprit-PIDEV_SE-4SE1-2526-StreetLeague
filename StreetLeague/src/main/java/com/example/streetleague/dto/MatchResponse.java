package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private Long id;
    private String equipe1;
    private String equipe2;
    private Integer score1;
    private Integer score2;
    @JsonProperty("dateMatch")
    private LocalDateTime dateMatch;
    private String statut;
    @JsonProperty("terrainId")
    private Long terrainId;
    @JsonProperty("terrainNom")
    private String terrainNom;
    @JsonProperty("organisateurId")
    private Long organisateurId;
    @JsonProperty("organisateurNom")
    private String organisateurNom;
    private String typeMatch;
    private String description;
    @JsonProperty("dureeMatch")
    private Integer dureeMatch;

    public static MatchResponse fromEntity(com.example.streetleague.Entity.Match match) {
        MatchResponse response = new MatchResponse();
        response.setId(match.getIdMatch());
        response.setEquipe1(match.getTeamA() != null ? match.getTeamA().getName() : null);
        response.setEquipe2(match.getTeamB() != null ? match.getTeamB().getName() : null);
        response.setScore1(match.getScoreTeamA());
        response.setScore2(match.getScoreTeamB());
        response.setDateMatch(match.getMatchDate());
        response.setStatut(match.getStatus() != null ? match.getStatus().toString() : null);
        response.setTerrainId(null);
        response.setOrganisateurId(match.getCreatedBy() != null ? match.getCreatedBy().getIdUser() : null);
        response.setTypeMatch(null);
        response.setDescription(null);
        response.setDureeMatch(90);
        if (match.getCreatedBy() != null) {
            response.setOrganisateurNom(match.getCreatedBy().getFullName());
        }
        return response;
    }
}
