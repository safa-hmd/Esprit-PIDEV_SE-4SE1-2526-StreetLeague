package com.example.streetleague.dto;

import lombok.*;
import java.util.List;

/**
 * PlayerStatsDto — étendu avec ACWR + Anomalie.
 *
 * NOUVEAUX CHAMPS ajoutés (section ACWR + ANOMALIE) :
 *   acwr, acuteLoad, chronicLoad, acwrZone
 *   anomalyType, anomalySeverity, zScore, ewmaScore, anomalyMessage
 */
@Data @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
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

    // ── Fatigue (ancienne formule conservée pour comparaison) ─────────────
    private Double  fatigueRisk;
    private String  riskLevel;
    private String  recommendation;
    private Integer recommendedRestDays;

    // ── Synergie ─────────────────────────────────────────────────────────
    private Double  synergyContribution;

    // ── Métriques avancées existantes ────────────────────────────────────
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

    // ══════════════════════════════════════════════════════════════════════
    //  NOUVEAUX CHAMPS — ACWR (Acute:Chronic Workload Ratio)
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Ratio ACWR brut.
     * Valeur optimale : 0.8 – 1.3
     * > 1.5 → zone danger
     */
    private Double  acwr;

    /**
     * Charge aiguë absolue sur 7 jours
     * (somme des poids de séance : TRAINING=1, MATCH=2, BOTH=3)
     */
    private Double  acuteLoad;

    /**
     * Charge chronique (moyenne hebdomadaire sur 28 jours)
     */
    private Double  chronicLoad;

    /**
     * Zone ACWR : UNDERLOAD | OPTIMAL | WARNING | DANGER
     */
    private String  acwrZone;

    /**
     * Recommandation basée sur l'ACWR (plus précise que la recommendation classique)
     */
    private String  acwrRecommendation;

    // ══════════════════════════════════════════════════════════════════════
    //  NOUVEAUX CHAMPS — DÉTECTION D'ANOMALIES
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Type d'anomalie : NORMAL | PERFORMANCE_DROP | PERFORMANCE_DROP_SUSPECTED | PERFORMANCE_SPIKE
     */
    private String  anomalyType;

    /**
     * Sévérité de l'anomalie : NONE | MEDIUM | HIGH | CRITICAL
     */
    private String  anomalySeverity;

    /**
     * Z-Score du score actuel par rapport à l'historique 28 jours.
     * < -2.0 → chute significative
     */
    private Double  zScore;

    /**
     * Valeur EWMA courante (Exponentially Weighted Moving Average).
     * Permet de voir la tendance lissée.
     */
    private Double  ewmaScore;

    /**
     * Pourcentage de chute EWMA par rapport à la moyenne historique (0–1).
     */
    private Double  ewmaDrop;

    /**
     * Message explicatif généré automatiquement par AnomalyDetector.
     */
    private String  anomalyMessage;

    /**
     * Indique si une alerte coach a été ou doit être envoyée.
     */
    private Boolean coachAlertSent;
}