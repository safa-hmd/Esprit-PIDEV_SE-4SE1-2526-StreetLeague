package com.example.streetleague.algorithm;

import com.example.streetleague.Entity.PlayerAttendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AnomalyDetector — Détection d'anomalies de performance par Z-Score + EWMA
 *
 * Deux algorithmes complémentaires :
 *
 * 1. Z-SCORE (méthode statistique classique)
 *    Mesure combien d'écarts-types le score actuel s'éloigne de la moyenne historique.
 *    z = (x - μ) / σ
 *    Si z < -2.0 → anomalie (chute significative)
 *    Si z > +2.0 → pic positif
 *
 * 2. EWMA — Exponentially Weighted Moving Average
 *    Donne plus de poids aux données récentes pour détecter les tendances rapidement.
 *    ewma_t = α × x_t + (1 - α) × ewma_{t-1}
 *    α = 0.3 (compromis : réactif sans être trop sensible au bruit)
 *
 * Combinaison : une anomalie est confirmée si Z-Score ET EWMA signalent un problème.
 */
public final class AnomalyDetector {

    // Seuil Z-Score pour déclencher une alerte (valeur absolue)
    // 1.5 = 87% confiance — plus sensible que 2.0 (95%), adapté aux petits datasets
    public static final double Z_SCORE_THRESHOLD     = 1.5;

    // Seuil EWMA : si EWMA chute de plus de 40% par rapport à la moyenne → alerte
    public static final double EWMA_DROP_THRESHOLD   = 0.40;

    // Facteur de lissage EWMA (α)
    public static final double EWMA_ALPHA            = 0.3;

    // Nombre minimum de points pour calculer des statistiques fiables
    public static final int    MIN_DATA_POINTS       = 3;

    private AnomalyDetector() {}

    /**
     * Analyse les présences d'un joueur et détecte les anomalies.
     *
     * @param attendances historique des présences (triées par date décroissante)
     * @param today       date de référence
     * @return AnomalyResult avec tous les détails
     */
    public static AnomalyResult analyze(List<PlayerAttendance> attendances, LocalDate today) {

        // ── Construire la série temporelle journalière (28 derniers jours) ─
        List<Double> dailySeries = buildDailySeries(attendances, today, 28);

        // [P1-FIX] Compter les jours réellement actifs (non-zéro) au lieu de la taille fixe.
        // buildDailySeries retourne toujours 28 points (zéros inclus), donc l'ancien test
        // dailySeries.size() < MIN_DATA_POINTS était pratiquement toujours faux.
        long activeDaysCount = dailySeries.stream().filter(v -> v > 0.0).count();
        if (activeDaysCount < MIN_DATA_POINTS) {
            return AnomalyResult.insufficient((int) activeDaysCount);
        }

        // ── Statistiques de base ─────────────────────────────────────────
        double mean   = computeMean(dailySeries);
        double stdDev = computeStdDev(dailySeries, mean);

        // Score actuel = moyenne des 3 derniers jours (lisse le bruit quotidien)
        double currentScore = computeRecentAverage(dailySeries, 3);

        // ── Z-Score ──────────────────────────────────────────────────────
        double zScore = (stdDev < 0.001) ? 0.0 : (currentScore - mean) / stdDev;

        // ── EWMA ─────────────────────────────────────────────────────────
        double ewma     = computeEwma(dailySeries);
        double ewmaDrop = (mean < 0.001) ? 0.0 : (mean - ewma) / mean;

        // ── Détection ────────────────────────────────────────────────────
        boolean zScoreAlert = zScore < -Z_SCORE_THRESHOLD;
        boolean ewmaAlert   = ewmaDrop > EWMA_DROP_THRESHOLD && ewma < mean;

        AnomalyType type = classifyAnomaly(zScore, ewmaDrop, zScoreAlert, ewmaAlert);

        // ── Sévérité ─────────────────────────────────────────────────────
        Severity severity = computeSeverity(zScore, ewmaDrop, type);

        double signalConfidence = computeSignalConfidence((int) activeDaysCount, zScoreAlert, ewmaAlert);

        return new AnomalyResult(
                type,
                severity,
                zScore,
                ewma,
                mean,
                stdDev,
                currentScore,
                ewmaDrop,
                zScoreAlert,
                ewmaAlert,
                (int) activeDaysCount,
                false,
                signalConfidence,
                buildMessage(type, severity, zScore, ewmaDrop, currentScore, mean)
        );
    }

    // ── Série temporelle ─────────────────────────────────────────────────

    /**
     * Construit une série journalière de charge (0 si absent, weight si présent).
     * Ordre chronologique (J-n → J).
     */
    static List<Double> buildDailySeries(List<PlayerAttendance> attendances,
                                         LocalDate today, int windowDays) {
        // Index des jours présents avec leur charge
        var presentMap = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .filter(a -> !a.getAttendanceDate().isBefore(today.minusDays(windowDays - 1)))
                .filter(a -> !a.getAttendanceDate().isAfter(today))
                .collect(Collectors.toMap(
                        PlayerAttendance::getAttendanceDate,
                        a -> AcwrCalculator.sessionWeight(a.getAttendanceType()),
                        (a, b) -> a // si doublon (ne devrait pas arriver), prendre le premier
                ));

        List<Double> series = new ArrayList<>();
        for (int i = windowDays - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            series.add(presentMap.getOrDefault(day, 0.0));
        }
        return series;
    }

    // ── Statistiques ─────────────────────────────────────────────────────

    static double computeMean(List<Double> series) {
        return series.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    static double computeStdDev(List<Double> series, double mean) {
        double variance = series.stream()
                .mapToDouble(x -> (x - mean) * (x - mean))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    /** Moyenne des N derniers éléments de la série. */
    static double computeRecentAverage(List<Double> series, int n) {
        int size  = series.size();
        int start = Math.max(0, size - n);
        return series.subList(start, size).stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * EWMA sur toute la série.
     * Parcourt la série du plus ancien au plus récent (ordre naturel).
     */
    static double computeEwma(List<Double> series) {
        if (series.isEmpty()) return 0.0;
        double ewma = series.get(0); // initialisation avec le premier point
        for (int i = 1; i < series.size(); i++) {
            ewma = EWMA_ALPHA * series.get(i) + (1 - EWMA_ALPHA) * ewma;
        }
        return ewma;
    }

    // ── Classification ────────────────────────────────────────────────────

    static AnomalyType classifyAnomaly(double zScore, double ewmaDrop,
                                       boolean zAlert, boolean ewmaAlert) {
        // Chute confirmée par les deux méthodes → anomalie certaine
        if (zAlert && ewmaAlert)  return AnomalyType.PERFORMANCE_DROP;
        // Chute détectée uniquement par Z-Score → anomalie probable
        if (zAlert)               return AnomalyType.PERFORMANCE_DROP_SUSPECTED;
        // Pic de performance
        if (zScore > Z_SCORE_THRESHOLD) return AnomalyType.PERFORMANCE_SPIKE;
        // Aucune anomalie
        return AnomalyType.NORMAL;
    }

    static Severity computeSeverity(double zScore, double ewmaDrop, AnomalyType type) {
        if (type == AnomalyType.NORMAL || type == AnomalyType.PERFORMANCE_SPIKE) {
            return Severity.NONE;
        }
        // Plus le Z-Score est négatif, plus la sévérité est haute
        double absZ = Math.abs(zScore);
        if (absZ >= 3.0 || ewmaDrop >= 0.60) return Severity.CRITICAL;
        if (absZ >= 2.5 || ewmaDrop >= 0.50) return Severity.HIGH;
        return Severity.MEDIUM;
    }

    static double computeSignalConfidence(int activeDays, boolean zAlert, boolean ewmaAlert) {
        double historyFactor = Math.min(1.0, activeDays / 14.0);
        double agreementFactor = (zAlert && ewmaAlert) ? 1.0 : (zAlert || ewmaAlert) ? 0.75 : 0.5;
        return Math.round(historyFactor * agreementFactor * 1000.0) / 1000.0;
    }

    static String buildMessage(AnomalyType type, Severity severity,
                               double zScore, double ewmaDrop,
                               double currentScore, double mean) {
        return switch (type) {
            case PERFORMANCE_DROP -> String.format(
                    "⚠️ ANOMALIE CONFIRMÉE : performance actuelle %.1f (moyenne %.1f, Z=%.2f, chute EWMA %.0f%%). Intervention recommandée.",
                    currentScore, mean, zScore, ewmaDrop * 100);
            case PERFORMANCE_DROP_SUSPECTED -> String.format(
                    "🔍 Chute suspecte détectée : Z-Score=%.2f (seuil -2.0). Surveillance renforcée conseillée.",
                    zScore);
            case PERFORMANCE_SPIKE -> String.format(
                    "📈 Pic de performance : Z-Score=+%.2f. Vérifiez que ce n'est pas du surmenage.", zScore);
            case NORMAL -> "✅ Performance dans la plage normale.";
        };
    }

    // ── Types et enums ────────────────────────────────────────────────────

    public enum AnomalyType {
        NORMAL,
        PERFORMANCE_DROP,           // Chute confirmée (Z-Score + EWMA)
        PERFORMANCE_DROP_SUSPECTED, // Chute probable (Z-Score seul)
        PERFORMANCE_SPIKE           // Pic positif
    }

    public enum Severity {
        NONE, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Résultat complet de la détection d'anomalies.
     */
    public record AnomalyResult(
            AnomalyType type,
            Severity    severity,
            double      zScore,
            double      ewma,
            double      mean,
            double      stdDev,
            double      currentScore,
            double      ewmaDrop,      // fraction : 0.4 = chute de 40%
            boolean     zScoreAlert,
            boolean     ewmaAlert,
            int         dataPoints,
            boolean     insufficientHistory,
            double      signalConfidence,
            String      message
    ) {
        public boolean isAnomaly() {
            return type == AnomalyType.PERFORMANCE_DROP
                    || type == AnomalyType.PERFORMANCE_DROP_SUSPECTED;
        }

        public boolean requiresCoachAlert() {
            return type == AnomalyType.PERFORMANCE_DROP
                    && (severity == Severity.HIGH || severity == Severity.CRITICAL);
        }

        /** Construit un résultat vide quand il n'y a pas assez de données. */
        static AnomalyResult insufficient(int dataPoints) {
            return new AnomalyResult(
                    AnomalyType.NORMAL, Severity.NONE,
                    0, 0, 0, 0, 0, 0,
                    false, false, dataPoints, true, 0.2,
                    "Données insuffisantes (minimum " + MIN_DATA_POINTS + " jours requis)."
            );
        }
    }
}