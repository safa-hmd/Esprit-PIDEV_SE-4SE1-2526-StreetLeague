package com.example.streetleague.dto;

import lombok.*;
import java.util.List;

/**
 * DTO universel — remplace FatigueAlertDto, MomentumScoreDto,
 * PlayerPerformanceDto, StreakLeaderboardDto, SynergyScoreDto.
 *
 * Les champs sont tous optionnels selon l'endpoint appelé.
 * Les champs non remplis restent null — le front ignore ce qu'il n'utilise pas.
 */
@Data @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class PlayerStatsDto {
    private Long playerId;
    private String playerName;
    private Integer currentStreak;
    private Integer bestStreak;
    private Integer totalPoints;
    private String badge;
    private String status;
    private Integer rank;
    private Double momentumScore;
    private String trend;
    private Double fatigueRisk;
    private String riskLevel;
    private String recommendation;
    private Integer recommendedRestDays;
    private Double synergyContribution;
}