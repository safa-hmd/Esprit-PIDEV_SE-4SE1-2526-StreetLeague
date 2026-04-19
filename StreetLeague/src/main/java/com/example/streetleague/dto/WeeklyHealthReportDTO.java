package com.example.streetleague.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WeeklyHealthReportDTO {

    private String fullName;
    private LocalDate weekStart;
    private LocalDate weekEnd;

    // Section BMI & poids
    private double currentWeight;
    private double currentHeight;
    private double currentBmi;
    private double weightChangeThisWeek; // différence entre premier et dernier enregistrement

    // Section hydratation
    private int totalWaterConsumedMl;
    private int daysGoalReached;         // nombre de jours où l'objectif eau est atteint
    private double avgDailyWaterMl;

    // Section objectifs
    private List<GoalSummary> goals;

    // Section historique BMI de la semaine
    private List<BmiEntry> bmiHistory;

    @Data
    @Builder
    public static class GoalSummary {
        private String goalType;
        private double targetValue;
        private boolean achieved;
    }

    @Data
    @Builder
    public static class BmiEntry {
        private LocalDate date;
        private double bmi;
        private double weight;
    }
}