package com.example.streetleague.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchCandidateResponse {
    private Long    teamId;
    private String  teamName;
    private String  sport;
    private Integer eloScore;
    private Double  eloFitScore;             // composante ELO  (0–100)
    private Double  h2hScore;                // composante H2H  (0–100)
    private Double  matchCompatibilityScore; // score final pondéré
}