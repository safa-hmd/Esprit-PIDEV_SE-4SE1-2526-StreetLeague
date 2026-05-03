package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitnessReportDTO {

    private Long userId;
    private String playerName;

    // Scores par dimension (0-100)
    private double bmiScore;
    private double hydrationScore;
    private double consistencyScore;
    private double trendScore;

    // Score final pondéré (0-100)
    private double finalScore;

    // Statut : ELITE / FIT / CAUTION / UNFIT
    private String status;
    private String statusColor; // green / yellow / orange / red

    // Détails supplémentaires
    private double currentBmi;
    private String bmiCategory;
    private int healthLogsLast30Days;
    private int waterLogsLast30Days;

    // Recommandations intelligentes
    private List<String> recommendations;

    private LocalDateTime generatedAt;
}
