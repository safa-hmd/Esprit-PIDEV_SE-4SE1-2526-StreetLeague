package com.example.streetleague.dto;

import java.util.List;
import java.util.Objects;

/**
 * PlayerStatsDto — étendu avec ACWR + Anomalie.
 */
public class PlayerStatsDto {

    // ── Identité ─────────────────────────────────────────────────────────
    private Long   playerId;
    private String playerName;

    // ── Streak & Points ──────────────────────────────────────────────────
    private Integer currentStreak;
    private Integer bestStreak;
    private Integer totalPoints;
    private String  badge;
    private String  status;
    private Integer rank;

    // ── Momentum ─────────────────────────────────────────────────────────
    private Double  momentumScore;
    private String  trend;

    // ── Fatigue ──────────────────────────────────────────────────────────
    private Double  fatigueRisk;
    private String  riskLevel;
    private String  recommendation;
    private Integer recommendedRestDays;

    // ── Synergie ─────────────────────────────────────────────────────────
    private Double  synergyContribution;

    // ── Métriques avancées ───────────────────────────────────────────────
    private Double  consistencyScore;
    private String  performanceLevel;
    private Double  weeklyAverage;
    private Integer activeDaysLast30;
    private Double  attendanceRate;
    private Double  recoveryScore;
    private Integer predictedStreakIn7Days;
    private Double  streakContinuationProbability;
    private Double  injuryRiskScore;
    private String  injuryRiskLevel;
    private Double  workloadFactor;
    private List<Integer> weeklyHistory;
    private Integer pointsThisWeek;
    private Integer pointsThisMonth;

    // ── ACWR (Acute:Chronic Workload Ratio) ──────────────────────────────
    private Double  acwr;
    private Double  acuteLoad;
    private Double  chronicLoad;
    private String  acwrZone;
    private String  acwrRecommendation;

    // ── Anomalie ─────────────────────────────────────────────────────────
    private String  anomalyType;
    private String  anomalySeverity;
    private Double  zScore;
    private Double  ewmaScore;
    private Double  ewmaDrop;
    private String  anomalyMessage;
    private Boolean coachAlertSent;

    public PlayerStatsDto() {
    }

    public PlayerStatsDto(Long playerId, String playerName, Integer currentStreak, Integer bestStreak, Integer totalPoints, String badge, String status, Integer rank, Double momentumScore, String trend, Double fatigueRisk, String riskLevel, String recommendation, Integer recommendedRestDays, Double synergyContribution, Double consistencyScore, String performanceLevel, Double weeklyAverage, Integer activeDaysLast30, Double attendanceRate, Double recoveryScore, Integer predictedStreakIn7Days, Double streakContinuationProbability, Double injuryRiskScore, String injuryRiskLevel, Double workloadFactor, List<Integer> weeklyHistory, Integer pointsThisWeek, Integer pointsThisMonth, Double acwr, Double acuteLoad, Double chronicLoad, String acwrZone, String acwrRecommendation, String anomalyType, String anomalySeverity, Double zScore, Double ewmaScore, Double ewmaDrop, String anomalyMessage, Boolean coachAlertSent) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.totalPoints = totalPoints;
        this.badge = badge;
        this.status = status;
        this.rank = rank;
        this.momentumScore = momentumScore;
        this.trend = trend;
        this.fatigueRisk = fatigueRisk;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
        this.recommendedRestDays = recommendedRestDays;
        this.synergyContribution = synergyContribution;
        this.consistencyScore = consistencyScore;
        this.performanceLevel = performanceLevel;
        this.weeklyAverage = weeklyAverage;
        this.activeDaysLast30 = activeDaysLast30;
        this.attendanceRate = attendanceRate;
        this.recoveryScore = recoveryScore;
        this.predictedStreakIn7Days = predictedStreakIn7Days;
        this.streakContinuationProbability = streakContinuationProbability;
        this.injuryRiskScore = injuryRiskScore;
        this.injuryRiskLevel = injuryRiskLevel;
        this.workloadFactor = workloadFactor;
        this.weeklyHistory = weeklyHistory;
        this.pointsThisWeek = pointsThisWeek;
        this.pointsThisMonth = pointsThisMonth;
        this.acwr = acwr;
        this.acuteLoad = acuteLoad;
        this.chronicLoad = chronicLoad;
        this.acwrZone = acwrZone;
        this.acwrRecommendation = acwrRecommendation;
        this.anomalyType = anomalyType;
        this.anomalySeverity = anomalySeverity;
        this.zScore = zScore;
        this.ewmaScore = ewmaScore;
        this.ewmaDrop = ewmaDrop;
        this.anomalyMessage = anomalyMessage;
        this.coachAlertSent = coachAlertSent;
    }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getBestStreak() { return bestStreak; }
    public void setBestStreak(Integer bestStreak) { this.bestStreak = bestStreak; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public Double getMomentumScore() { return momentumScore; }
    public void setMomentumScore(Double momentumScore) { this.momentumScore = momentumScore; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public Double getFatigueRisk() { return fatigueRisk; }
    public void setFatigueRisk(Double fatigueRisk) { this.fatigueRisk = fatigueRisk; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public Integer getRecommendedRestDays() { return recommendedRestDays; }
    public void setRecommendedRestDays(Integer recommendedRestDays) { this.recommendedRestDays = recommendedRestDays; }

    public Double getSynergyContribution() { return synergyContribution; }
    public void setSynergyContribution(Double synergyContribution) { this.synergyContribution = synergyContribution; }

    public Double getConsistencyScore() { return consistencyScore; }
    public void setConsistencyScore(Double consistencyScore) { this.consistencyScore = consistencyScore; }

    public String getPerformanceLevel() { return performanceLevel; }
    public void setPerformanceLevel(String performanceLevel) { this.performanceLevel = performanceLevel; }

    public Double getWeeklyAverage() { return weeklyAverage; }
    public void setWeeklyAverage(Double weeklyAverage) { this.weeklyAverage = weeklyAverage; }

    public Integer getActiveDaysLast30() { return activeDaysLast30; }
    public void setActiveDaysLast30(Integer activeDaysLast30) { this.activeDaysLast30 = activeDaysLast30; }

    public Double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; }

    public Double getRecoveryScore() { return recoveryScore; }
    public void setRecoveryScore(Double recoveryScore) { this.recoveryScore = recoveryScore; }

    public Integer getPredictedStreakIn7Days() { return predictedStreakIn7Days; }
    public void setPredictedStreakIn7Days(Integer predictedStreakIn7Days) { this.predictedStreakIn7Days = predictedStreakIn7Days; }

    public Double getStreakContinuationProbability() { return streakContinuationProbability; }
    public void setStreakContinuationProbability(Double streakContinuationProbability) { this.streakContinuationProbability = streakContinuationProbability; }

    public Double getInjuryRiskScore() { return injuryRiskScore; }
    public void setInjuryRiskScore(Double injuryRiskScore) { this.injuryRiskScore = injuryRiskScore; }

    public String getInjuryRiskLevel() { return injuryRiskLevel; }
    public void setInjuryRiskLevel(String injuryRiskLevel) { this.injuryRiskLevel = injuryRiskLevel; }

    public Double getWorkloadFactor() { return workloadFactor; }
    public void setWorkloadFactor(Double workloadFactor) { this.workloadFactor = workloadFactor; }

    public List<Integer> getWeeklyHistory() { return weeklyHistory; }
    public void setWeeklyHistory(List<Integer> weeklyHistory) { this.weeklyHistory = weeklyHistory; }

    public Integer getPointsThisWeek() { return pointsThisWeek; }
    public void setPointsThisWeek(Integer pointsThisWeek) { this.pointsThisWeek = pointsThisWeek; }

    public Integer getPointsThisMonth() { return pointsThisMonth; }
    public void setPointsThisMonth(Integer pointsThisMonth) { this.pointsThisMonth = pointsThisMonth; }

    public Double getAcwr() { return acwr; }
    public void setAcwr(Double acwr) { this.acwr = acwr; }

    public Double getAcuteLoad() { return acuteLoad; }
    public void setAcuteLoad(Double acuteLoad) { this.acuteLoad = acuteLoad; }

    public Double getChronicLoad() { return chronicLoad; }
    public void setChronicLoad(Double chronicLoad) { this.chronicLoad = chronicLoad; }

    public String getAcwrZone() { return acwrZone; }
    public void setAcwrZone(String acwrZone) { this.acwrZone = acwrZone; }

    public String getAcwrRecommendation() { return acwrRecommendation; }
    public void setAcwrRecommendation(String acwrRecommendation) { this.acwrRecommendation = acwrRecommendation; }

    public String getAnomalyType() { return anomalyType; }
    public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }

    public String getAnomalySeverity() { return anomalySeverity; }
    public void setAnomalySeverity(String anomalySeverity) { this.anomalySeverity = anomalySeverity; }

    public Double getZScore() { return zScore; }
    public void setZScore(Double zScore) { this.zScore = zScore; }

    public Double getEwmaScore() { return ewmaScore; }
    public void setEwmaScore(Double ewmaScore) { this.ewmaScore = ewmaScore; }

    public Double getEwmaDrop() { return ewmaDrop; }
    public void setEwmaDrop(Double ewmaDrop) { this.ewmaDrop = ewmaDrop; }

    public String getAnomalyMessage() { return anomalyMessage; }
    public void setAnomalyMessage(String anomalyMessage) { this.anomalyMessage = anomalyMessage; }

    public Boolean getCoachAlertSent() { return coachAlertSent; }
    public void setCoachAlertSent(Boolean coachAlertSent) { this.coachAlertSent = coachAlertSent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerStatsDto that = (PlayerStatsDto) o;
        return Objects.equals(playerId, that.playerId) && Objects.equals(playerName, that.playerName) && Objects.equals(totalPoints, that.totalPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, playerName, totalPoints);
    }

    @Override
    public String toString() {
        return "PlayerStatsDto{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", totalPoints=" + totalPoints +
                ", performanceLevel='" + performanceLevel + '\'' +
                '}';
    }

    public static PlayerStatsDtoBuilder builder() {
        return new PlayerStatsDtoBuilder();
    }

    public PlayerStatsDtoBuilder toBuilder() {
        return builder()
                .playerId(playerId)
                .playerName(playerName)
                .currentStreak(currentStreak)
                .bestStreak(bestStreak)
                .totalPoints(totalPoints)
                .badge(badge)
                .status(status)
                .rank(rank)
                .momentumScore(momentumScore)
                .trend(trend)
                .fatigueRisk(fatigueRisk)
                .riskLevel(riskLevel)
                .recommendation(recommendation)
                .recommendedRestDays(recommendedRestDays)
                .synergyContribution(synergyContribution)
                .consistencyScore(consistencyScore)
                .performanceLevel(performanceLevel)
                .weeklyAverage(weeklyAverage)
                .activeDaysLast30(activeDaysLast30)
                .attendanceRate(attendanceRate)
                .recoveryScore(recoveryScore)
                .predictedStreakIn7Days(predictedStreakIn7Days)
                .streakContinuationProbability(streakContinuationProbability)
                .injuryRiskScore(injuryRiskScore)
                .injuryRiskLevel(injuryRiskLevel)
                .workloadFactor(workloadFactor)
                .weeklyHistory(weeklyHistory)
                .pointsThisWeek(pointsThisWeek)
                .pointsThisMonth(pointsThisMonth)
                .acwr(acwr)
                .acuteLoad(acuteLoad)
                .chronicLoad(chronicLoad)
                .acwrZone(acwrZone)
                .acwrRecommendation(acwrRecommendation)
                .anomalyType(anomalyType)
                .anomalySeverity(anomalySeverity)
                .zScore(zScore)
                .ewmaScore(ewmaScore)
                .ewmaDrop(ewmaDrop)
                .anomalyMessage(anomalyMessage)
                .coachAlertSent(coachAlertSent);
    }

    public static class PlayerStatsDtoBuilder {
        private Long   playerId;
        private String playerName;
        private Integer currentStreak;
        private Integer bestStreak;
        private Integer totalPoints;
        private String  badge;
        private String  status;
        private Integer rank;
        private Double  momentumScore;
        private String  trend;
        private Double  fatigueRisk;
        private String  riskLevel;
        private String  recommendation;
        private Integer recommendedRestDays;
        private Double  synergyContribution;
        private Double  consistencyScore;
        private String  performanceLevel;
        private Double  weeklyAverage;
        private Integer activeDaysLast30;
        private Double  attendanceRate;
        private Double  recoveryScore;
        private Integer predictedStreakIn7Days;
        private Double  streakContinuationProbability;
        private Double  injuryRiskScore;
        private String  injuryRiskLevel;
        private Double  workloadFactor;
        private List<Integer> weeklyHistory;
        private Integer pointsThisWeek;
        private Integer pointsThisMonth;
        private Double  acwr;
        private Double  acuteLoad;
        private Double  chronicLoad;
        private String  acwrZone;
        private String  acwrRecommendation;
        private String  anomalyType;
        private String  anomalySeverity;
        private Double  zScore;
        private Double  ewmaScore;
        private Double  ewmaDrop;
        private String  anomalyMessage;
        private Boolean coachAlertSent;

        public PlayerStatsDtoBuilder playerId(Long playerId) { this.playerId = playerId; return this; }
        public PlayerStatsDtoBuilder playerName(String playerName) { this.playerName = playerName; return this; }
        public PlayerStatsDtoBuilder currentStreak(Integer currentStreak) { this.currentStreak = currentStreak; return this; }
        public PlayerStatsDtoBuilder bestStreak(Integer bestStreak) { this.bestStreak = bestStreak; return this; }
        public PlayerStatsDtoBuilder totalPoints(Integer totalPoints) { this.totalPoints = totalPoints; return this; }
        public PlayerStatsDtoBuilder badge(String badge) { this.badge = badge; return this; }
        public PlayerStatsDtoBuilder status(String status) { this.status = status; return this; }
        public PlayerStatsDtoBuilder rank(Integer rank) { this.rank = rank; return this; }
        public PlayerStatsDtoBuilder momentumScore(Double momentumScore) { this.momentumScore = momentumScore; return this; }
        public PlayerStatsDtoBuilder trend(String trend) { this.trend = trend; return this; }
        public PlayerStatsDtoBuilder fatigueRisk(Double fatigueRisk) { this.fatigueRisk = fatigueRisk; return this; }
        public PlayerStatsDtoBuilder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public PlayerStatsDtoBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public PlayerStatsDtoBuilder recommendedRestDays(Integer recommendedRestDays) { this.recommendedRestDays = recommendedRestDays; return this; }
        public PlayerStatsDtoBuilder synergyContribution(Double synergyContribution) { this.synergyContribution = synergyContribution; return this; }
        public PlayerStatsDtoBuilder consistencyScore(Double consistencyScore) { this.consistencyScore = consistencyScore; return this; }
        public PlayerStatsDtoBuilder performanceLevel(String performanceLevel) { this.performanceLevel = performanceLevel; return this; }
        public PlayerStatsDtoBuilder weeklyAverage(Double weeklyAverage) { this.weeklyAverage = weeklyAverage; return this; }
        public PlayerStatsDtoBuilder activeDaysLast30(Integer activeDaysLast30) { this.activeDaysLast30 = activeDaysLast30; return this; }
        public PlayerStatsDtoBuilder attendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; return this; }
        public PlayerStatsDtoBuilder recoveryScore(Double recoveryScore) { this.recoveryScore = recoveryScore; return this; }
        public PlayerStatsDtoBuilder predictedStreakIn7Days(Integer predictedStreakIn7Days) { this.predictedStreakIn7Days = predictedStreakIn7Days; return this; }
        public PlayerStatsDtoBuilder streakContinuationProbability(Double streakContinuationProbability) { this.streakContinuationProbability = streakContinuationProbability; return this; }
        public PlayerStatsDtoBuilder injuryRiskScore(Double injuryRiskScore) { this.injuryRiskScore = injuryRiskScore; return this; }
        public PlayerStatsDtoBuilder injuryRiskLevel(String injuryRiskLevel) { this.injuryRiskLevel = injuryRiskLevel; return this; }
        public PlayerStatsDtoBuilder workloadFactor(Double workloadFactor) { this.workloadFactor = workloadFactor; return this; }
        public PlayerStatsDtoBuilder weeklyHistory(List<Integer> weeklyHistory) { this.weeklyHistory = weeklyHistory; return this; }
        public PlayerStatsDtoBuilder pointsThisWeek(Integer pointsThisWeek) { this.pointsThisWeek = pointsThisWeek; return this; }
        public PlayerStatsDtoBuilder pointsThisMonth(Integer pointsThisMonth) { this.pointsThisMonth = pointsThisMonth; return this; }
        public PlayerStatsDtoBuilder acwr(Double acwr) { this.acwr = acwr; return this; }
        public PlayerStatsDtoBuilder acuteLoad(Double acuteLoad) { this.acuteLoad = acuteLoad; return this; }
        public PlayerStatsDtoBuilder chronicLoad(Double chronicLoad) { this.chronicLoad = chronicLoad; return this; }
        public PlayerStatsDtoBuilder acwrZone(String acwrZone) { this.acwrZone = acwrZone; return this; }
        public PlayerStatsDtoBuilder acwrRecommendation(String acwrRecommendation) { this.acwrRecommendation = acwrRecommendation; return this; }
        public PlayerStatsDtoBuilder anomalyType(String anomalyType) { this.anomalyType = anomalyType; return this; }
        public PlayerStatsDtoBuilder anomalySeverity(String anomalySeverity) { this.anomalySeverity = anomalySeverity; return this; }
        public PlayerStatsDtoBuilder zScore(Double zScore) { this.zScore = zScore; return this; }
        public PlayerStatsDtoBuilder ewmaScore(Double ewmaScore) { this.ewmaScore = ewmaScore; return this; }
        public PlayerStatsDtoBuilder ewmaDrop(Double ewmaDrop) { this.ewmaDrop = ewmaDrop; return this; }
        public PlayerStatsDtoBuilder anomalyMessage(String anomalyMessage) { this.anomalyMessage = anomalyMessage; return this; }
        public PlayerStatsDtoBuilder coachAlertSent(Boolean coachAlertSent) { this.coachAlertSent = coachAlertSent; return this; }

        public PlayerStatsDto build() {
            return new PlayerStatsDto(playerId, playerName, currentStreak, bestStreak, totalPoints, badge, status, rank, momentumScore, trend, fatigueRisk, riskLevel, recommendation, recommendedRestDays, synergyContribution, consistencyScore, performanceLevel, weeklyAverage, activeDaysLast30, attendanceRate, recoveryScore, predictedStreakIn7Days, streakContinuationProbability, injuryRiskScore, injuryRiskLevel, workloadFactor, weeklyHistory, pointsThisWeek, pointsThisMonth, acwr, acuteLoad, chronicLoad, acwrZone, acwrRecommendation, anomalyType, anomalySeverity, zScore, ewmaScore, ewmaDrop, anomalyMessage, coachAlertSent);
        }
    }
}