package com.example.streetleague.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public FitnessReportDTO() {
    }

    public FitnessReportDTO(Long userId, String playerName, double bmiScore, double hydrationScore, double consistencyScore, double trendScore, double finalScore, String status, String statusColor, double currentBmi, String bmiCategory, int healthLogsLast30Days, int waterLogsLast30Days, List<String> recommendations, LocalDateTime generatedAt) {
        this.userId = userId;
        this.playerName = playerName;
        this.bmiScore = bmiScore;
        this.hydrationScore = hydrationScore;
        this.consistencyScore = consistencyScore;
        this.trendScore = trendScore;
        this.finalScore = finalScore;
        this.status = status;
        this.statusColor = statusColor;
        this.currentBmi = currentBmi;
        this.bmiCategory = bmiCategory;
        this.healthLogsLast30Days = healthLogsLast30Days;
        this.waterLogsLast30Days = waterLogsLast30Days;
        this.recommendations = recommendations;
        this.generatedAt = generatedAt;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public double getBmiScore() { return bmiScore; }
    public void setBmiScore(double bmiScore) { this.bmiScore = bmiScore; }

    public double getHydrationScore() { return hydrationScore; }
    public void setHydrationScore(double hydrationScore) { this.hydrationScore = hydrationScore; }

    public double getConsistencyScore() { return consistencyScore; }
    public void setConsistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; }

    public double getTrendScore() { return trendScore; }
    public void setTrendScore(double trendScore) { this.trendScore = trendScore; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

    public double getCurrentBmi() { return currentBmi; }
    public void setCurrentBmi(double currentBmi) { this.currentBmi = currentBmi; }

    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }

    public int getHealthLogsLast30Days() { return healthLogsLast30Days; }
    public void setHealthLogsLast30Days(int healthLogsLast30Days) { this.healthLogsLast30Days = healthLogsLast30Days; }

    public int getWaterLogsLast30Days() { return waterLogsLast30Days; }
    public void setWaterLogsLast30Days(int waterLogsLast30Days) { this.waterLogsLast30Days = waterLogsLast30Days; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FitnessReportDTO that = (FitnessReportDTO) o;
        return Double.compare(that.bmiScore, bmiScore) == 0 && Double.compare(that.hydrationScore, hydrationScore) == 0 && Double.compare(that.consistencyScore, consistencyScore) == 0 && Double.compare(that.trendScore, trendScore) == 0 && Double.compare(that.finalScore, finalScore) == 0 && Double.compare(that.currentBmi, currentBmi) == 0 && healthLogsLast30Days == that.healthLogsLast30Days && waterLogsLast30Days == that.waterLogsLast30Days && Objects.equals(userId, that.userId) && Objects.equals(playerName, that.playerName) && Objects.equals(status, that.status) && Objects.equals(statusColor, that.statusColor) && Objects.equals(bmiCategory, that.bmiCategory) && Objects.equals(recommendations, that.recommendations) && Objects.equals(generatedAt, that.generatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, playerName, bmiScore, hydrationScore, consistencyScore, trendScore, finalScore, status, statusColor, currentBmi, bmiCategory, healthLogsLast30Days, waterLogsLast30Days, recommendations, generatedAt);
    }

    @Override
    public String toString() {
        return "FitnessReportDTO{" +
                "userId=" + userId +
                ", playerName='" + playerName + '\'' +
                ", finalScore=" + finalScore +
                ", status='" + status + '\'' +
                '}';
    }

    public static FitnessReportDTOBuilder builder() {
        return new FitnessReportDTOBuilder();
    }

    public static class FitnessReportDTOBuilder {
        private Long userId;
        private String playerName;
        private double bmiScore;
        private double hydrationScore;
        private double consistencyScore;
        private double trendScore;
        private double finalScore;
        private String status;
        private String statusColor;
        private double currentBmi;
        private String bmiCategory;
        private int healthLogsLast30Days;
        private int waterLogsLast30Days;
        private List<String> recommendations;
        private LocalDateTime generatedAt;

        public FitnessReportDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public FitnessReportDTOBuilder playerName(String playerName) { this.playerName = playerName; return this; }
        public FitnessReportDTOBuilder bmiScore(double bmiScore) { this.bmiScore = bmiScore; return this; }
        public FitnessReportDTOBuilder hydrationScore(double hydrationScore) { this.hydrationScore = hydrationScore; return this; }
        public FitnessReportDTOBuilder consistencyScore(double consistencyScore) { this.consistencyScore = consistencyScore; return this; }
        public FitnessReportDTOBuilder trendScore(double trendScore) { this.trendScore = trendScore; return this; }
        public FitnessReportDTOBuilder finalScore(double finalScore) { this.finalScore = finalScore; return this; }
        public FitnessReportDTOBuilder status(String status) { this.status = status; return this; }
        public FitnessReportDTOBuilder statusColor(String statusColor) { this.statusColor = statusColor; return this; }
        public FitnessReportDTOBuilder currentBmi(double currentBmi) { this.currentBmi = currentBmi; return this; }
        public FitnessReportDTOBuilder bmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; return this; }
        public FitnessReportDTOBuilder healthLogsLast30Days(int healthLogsLast30Days) { this.healthLogsLast30Days = healthLogsLast30Days; return this; }
        public FitnessReportDTOBuilder waterLogsLast30Days(int waterLogsLast30Days) { this.waterLogsLast30Days = waterLogsLast30Days; return this; }
        public FitnessReportDTOBuilder recommendations(List<String> recommendations) { this.recommendations = recommendations; return this; }
        public FitnessReportDTOBuilder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }

        public FitnessReportDTO build() {
            return new FitnessReportDTO(userId, playerName, bmiScore, hydrationScore, consistencyScore, trendScore, finalScore, status, statusColor, currentBmi, bmiCategory, healthLogsLast30Days, waterLogsLast30Days, recommendations, generatedAt);
        }
    }
}
