package com.example.streetleague.algorithm;

import com.example.streetleague.Entity.PlayerAttendance;

import java.time.LocalDate;
import java.util.List;

/**
 * ACWR — Acute:Chronic Workload Ratio
 *
 * Formule sport-science standard :
 *   ACWR = charge_aigue (7j) / charge_chronique (28j)
 *
 * Interprétation :
 *   < 0.8  → sous-entraînement (risque de blessure par déconditionnement)
 *   0.8 – 1.3 → zone optimale (sweet spot)
 *   1.3 – 1.5 → zone de surveillance
 *   > 1.5  → zone rouge (risque élevé de blessure / surentraînement)
 *
 * Chaque séance a un poids selon son type :
 *   TRAINING = 1.0
 *   MATCH    = 2.0  (intensité plus élevée)
 *   BOTH     = 3.0  (match + entraînement le même jour)
 *
 * Référence : Hulin et al. (2016) — British Journal of Sports Medicine
 */
public final class AcwrCalculator {

    // Fenêtres temporelles (jours)
    public static final int ACUTE_WINDOW   = 7;
    public static final int CHRONIC_WINDOW = 28;

    // Charge max théorique sur 7j (7j × poids BOTH 3.0 = 21)
    private static final double MAX_ACUTE_LOAD  = 21.0;

    // Poids par type de séance
    private static final double WEIGHT_TRAINING = 1.0;
    private static final double WEIGHT_MATCH     = 2.0;
    private static final double WEIGHT_BOTH      = 3.0;

    // Seuils ACWR
    public static final double THRESHOLD_UNDERLOAD   = 0.8;
    public static final double THRESHOLD_OPTIMAL_MAX = 1.3;
    public static final double THRESHOLD_WARNING      = 1.5;

    private AcwrCalculator() {}

    /**
     * Calcule l'ACWR d'un joueur à partir de ses présences sur 28 jours.
     *
     * @param attendances liste des présences (peut couvrir > 28j, on filtre ici)
     * @param today       date de référence
     * @return AcwrResult contenant tous les détails du calcul
     */
    public static AcwrResult compute(List<PlayerAttendance> attendances, LocalDate today) {

        LocalDate acuteFrom   = today.minusDays(ACUTE_WINDOW - 1);   // J-6 → J
        LocalDate chronicFrom = today.minusDays(CHRONIC_WINDOW - 1); // J-27 → J

        // ── Charge aiguë (7 derniers jours) ─────────────────────────────
        double acuteLoad = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .filter(a -> !a.getAttendanceDate().isBefore(acuteFrom)
                        && !a.getAttendanceDate().isAfter(today))
                .mapToDouble(a -> sessionWeight(a.getAttendanceType()))
                .sum();

        // ── Charge chronique (28 derniers jours, moyenne dynamique) ──────
        double totalChronicLoad = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .filter(a -> !a.getAttendanceDate().isBefore(chronicFrom)
                        && !a.getAttendanceDate().isAfter(today))
                .mapToDouble(a -> sessionWeight(a.getAttendanceType()))
                .sum();

        // Nombre de jours actifs dans la fenêtre de 28j
        long activeDays = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .filter(a -> !a.getAttendanceDate().isBefore(chronicFrom)
                        && !a.getAttendanceDate().isAfter(today))
                .map(PlayerAttendance::getAttendanceDate)
                .distinct()
                .count();

        // Minimum History Gate : moins de 7 jours d'historique → ACWR non fiable
        // On retourne un résultat marqué insufficientHistory=true au lieu de le masquer en "neutre"
        if (activeDays < 7) {
            double fatigueRiskEarly = 0.15; // valeur neutre basse, pas 0 pour ne pas cacher
            double normalizedEarly  = Math.min(1.0, acuteLoad / MAX_ACUTE_LOAD);
            return new AcwrResult(acuteLoad, acuteLoad, 1.0, fatigueRiskEarly, normalizedEarly, true);
        }

        // Dynamic Chronic Window : on divise par le nombre réel de semaines couvertes
        // → évite de pénaliser un joueur qui a peu d'historique
        double actualWeeks  = Math.max(1.0, activeDays / 7.0);
        double chronicLoad  = totalChronicLoad / actualWeeks;

        // ── Calcul du ratio ──────────────────────────────────────────────
        double acwr = (chronicLoad < 0.01) ? 1.0 : (acuteLoad / chronicLoad);

        // ── Clamp pour éviter des valeurs aberrantes ─────────────────────
        acwr = Math.max(0.0, Math.min(acwr, 4.0));

        // ── Fatigue risk normalisée (0–1) pour compatibilité avec PlayerStreak ─
        double fatigueRisk = computeFatigueRisk(acwr);

        // ── Charge normalisée (0–1) ──────────────────────────────────────
        double normalizedAcuteLoad = Math.min(1.0, acuteLoad / MAX_ACUTE_LOAD);

        return new AcwrResult(acuteLoad, chronicLoad, acwr, fatigueRisk, normalizedAcuteLoad, false);
    }

    /**
     * Convertit l'ACWR en fatigueRisk (0–1) pour le PlayerStreak.
     *
     * Courbe :
     *   ACWR ≤ 0.8  → fatigueRisk = 0.20 (sous-entraînement, risque modéré)
     *   ACWR 0.8–1.3 → interpolation linéaire 0.10 → 0.30 (zone optimale, risque faible)
     *   ACWR 1.3–1.5 → interpolation linéaire 0.30 → 0.65 (zone surveillance)
     *   ACWR 1.5–2.0 → interpolation linéaire 0.65 → 0.90 (zone rouge)
     *   ACWR > 2.0   → fatigueRisk = 1.0 (risque maximal)
     */
    public static double computeFatigueRisk(double acwr) {
        if (acwr <= 0.8)  return lerp(acwr, 0.0, 0.8, 0.30, 0.20);
        if (acwr <= 1.3)  return lerp(acwr, 0.8, 1.3, 0.10, 0.30);
        if (acwr <= 1.5)  return lerp(acwr, 1.3, 1.5, 0.30, 0.65);
        if (acwr <= 2.0)  return lerp(acwr, 1.5, 2.0, 0.65, 0.90);
        return 1.0;
    }

    /**
     * Détermine la zone ACWR.
     */
    public static AcwrZone computeZone(double acwr) {
        if (acwr < THRESHOLD_UNDERLOAD)    return AcwrZone.UNDERLOAD;
        if (acwr <= THRESHOLD_OPTIMAL_MAX) return AcwrZone.OPTIMAL;
        if (acwr <= THRESHOLD_WARNING)     return AcwrZone.WARNING;
        return AcwrZone.DANGER;
    }

    /**
     * Génère un message de recommandation basé sur l'ACWR.
     */
    public static String buildRecommendation(double acwr, AcwrZone zone) {
        return switch (zone) {
            case UNDERLOAD -> String.format(
                    "ACWR %.2f — Sous-entraînement : augmentez progressivement la charge (+10%% par semaine).", acwr);
            case OPTIMAL -> String.format(
                    "ACWR %.2f — Zone optimale : maintenez ce rythme, risque minimal de blessure.", acwr);
            case WARNING -> String.format(
                    "ACWR %.2f — Zone de surveillance : réduisez l'intensité, 1–2 jours de récupération.", acwr);
            case DANGER -> String.format(
                    "ACWR %.2f — DANGER : repos obligatoire 2–3 jours, risque élevé de blessure !", acwr);
        };
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    /** Poids d'une séance selon son type. */
    public static double sessionWeight(String attendanceType) {
        if (attendanceType == null) return WEIGHT_TRAINING;
        return switch (attendanceType.toUpperCase()) {
            case "BOTH"     -> WEIGHT_BOTH;
            case "MATCH"    -> WEIGHT_MATCH;
            default         -> WEIGHT_TRAINING; // TRAINING
        };
    }

    /** Interpolation linéaire entre deux plages. */
    private static double lerp(double x, double x0, double x1, double y0, double y1) {
        double t = (x - x0) / (x1 - x0);
        return Math.round((y0 + t * (y1 - y0)) * 1000.0) / 1000.0;
    }

    // ── Classes internes ─────────────────────────────────────────────────

    public enum AcwrZone {
        UNDERLOAD, OPTIMAL, WARNING, DANGER
    }

    /**
     * Résultat complet du calcul ACWR.
     */
    public record AcwrResult(
            double acuteLoad,
            double chronicLoad,
            double acwr,
            double fatigueRisk,
            double normalizedAcuteLoad,
            boolean insufficientHistory   // true si < 7 jours actifs → données non fiables
    ) {
        public AcwrZone zone() { return computeZone(acwr); }
        public String recommendation() { return buildRecommendation(acwr, zone()); }
        public String riskLevel() {
            if (insufficientHistory) return "UNKNOWN";
            return switch (zone()) {
                case UNDERLOAD -> "MODERATE";
                case OPTIMAL   -> "LOW";
                case WARNING   -> "HIGH";
                case DANGER    -> "CRITICAL";
            };
        }
    }
}