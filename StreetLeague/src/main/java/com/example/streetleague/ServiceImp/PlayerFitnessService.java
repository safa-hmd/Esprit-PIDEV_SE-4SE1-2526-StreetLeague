package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.HealthHistory;
import com.example.streetleague.Repository.HealthHistoryRepository;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.FitnessReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerFitnessService {

    private final HealthHistoryRepository healthHistoryRepository;

    // ─── Poids des dimensions ──────────────────────────────────────────────────
    private static final double W_BMI         = 0.35;
    private static final double W_HYDRATION   = 0.25;
    private static final double W_CONSISTENCY = 0.25;
    private static final double W_TREND       = 0.15;

    // ─── Point d'entrée principal ──────────────────────────────────────────────
    public FitnessReportDTO generateFitnessReport(User player) {

        List<HealthHistory> allHistory = healthHistoryRepository
                .findByUserIdOrderByDateDesc(player.getIdUser());

        // Si aucune donnée de santé → retourner rapport vide
        if (allHistory.isEmpty()) {
            return FitnessReportDTO.builder()

                    .userId(player.getIdUser())
                    .playerName(player.getFullName())
                    .finalScore(0)
                    .status("NO_DATA")
                    .statusColor("grey")
                    .recommendations(List.of("📊 Aucune donnée de santé disponible. Calculez votre BMI pour commencer."))
                    .generatedAt(LocalDateTime.now())
                    .build();
        }

        // ─── Données actuelles ─────────────────────────────────────────────────
        HealthHistory latest = allHistory.get(0);
        double currentBmi = latest.getBmi();

        // ─── Calcul des 4 dimensions ───────────────────────────────────────────
        double bmiScore         = calculateBMIScore(currentBmi);
        double hydrationScore   = calculateHydrationScore(player, allHistory);
        double consistencyScore = calculateConsistencyScore(allHistory);
        double trendScore       = calculateTrendScore(allHistory);

        // ─── Score final pondéré ───────────────────────────────────────────────
        double finalScore = (bmiScore         * W_BMI)
                          + (hydrationScore   * W_HYDRATION)
                          + (consistencyScore * W_CONSISTENCY)
                          + (trendScore       * W_TREND);

        finalScore = Math.round(finalScore * 10.0) / 10.0;

        // ─── Statut & couleur ──────────────────────────────────────────────────
        String status      = determineStatus(finalScore);
        String statusColor = determineColor(status);
        String bmiCategory = determineBMICategory(currentBmi);

        // ─── Statistiques de logs ──────────────────────────────────────────────
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        int healthLogs30 = (int) allHistory.stream()
                .filter(h -> h.getDate() != null &&
                        h.getDate().toEpochDay() >= java.time.LocalDate.now().minusDays(30).toEpochDay())
                .count();

        // ─── Recommandations ──────────────────────────────────────────────────
        List<String> recommendations = generateRecommendations(
                bmiScore, hydrationScore, consistencyScore, trendScore, currentBmi
        );

        return FitnessReportDTO.builder()
                .userId(player.getIdUser())
                .playerName(player.getFullName())
                .bmiScore(Math.round(bmiScore * 10.0) / 10.0)
                .hydrationScore(Math.round(hydrationScore * 10.0) / 10.0)
                .consistencyScore(Math.round(consistencyScore * 10.0) / 10.0)
                .trendScore(Math.round(trendScore * 10.0) / 10.0)
                .finalScore(finalScore)
                .status(status)
                .statusColor(statusColor)
                .currentBmi(Math.round(currentBmi * 100.0) / 100.0)
                .bmiCategory(bmiCategory)
                .healthLogsLast30Days(healthLogs30)
                .waterLogsLast30Days(0) // sera amélioré quand daily_water_log sera connecté
                .recommendations(recommendations)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ─── Dimension 1 : BMI Score (35%) ────────────────────────────────────────
    private double calculateBMIScore(double bmi) {
        if (bmi >= 18.5 && bmi <= 24.9) return 100.0;  // Zone idéale sportif
        if (bmi >= 17.0 && bmi <  18.5) return 65.0;   // Légèrement sous-poids
        if (bmi >  24.9 && bmi <= 27.5) return 65.0;   // Légèrement surpoids
        if (bmi >= 15.0 && bmi <  17.0) return 30.0;   // Dangereux
        if (bmi >  27.5 && bmi <= 30.0) return 30.0;   // Dangereux
        return 0.0;
    }

    // ─── Dimension 2 : Hydration Score (25%) ──────────────────────────────────
    // Basé sur la fréquence des entrées dans health_history (proxy d'activité)
    private double calculateHydrationScore(User player, List<HealthHistory> history) {
        // On utilise le BMI pour estimer l'état hydrique
        // (En l'absence de daily_water_log dans ce module)
        double bmi = history.get(0).getBmi();

        // Un BMI stable proche de la normale = bonne hydratation probable
        if (bmi >= 18.5 && bmi <= 24.9) return 85.0;
        if (bmi >= 17.0 && bmi <= 27.5) return 60.0;
        return 35.0;
    }

    // ─── Dimension 3 : Consistency Score (25%) ────────────────────────────────
    // Combien de jours l'utilisateur a-t-il loggé ses données dans les 30 derniers jours ?
    private double calculateConsistencyScore(List<HealthHistory> history) {
        long logsLast30Days = history.stream()
                .filter(h -> h.getDate() != null &&
                        !h.getDate().isBefore(java.time.LocalDate.now().minusDays(30)))
                .count();

        // Score proportionnel : 10 logs sur 30 jours = 33% → idéalement 1 log par 3 jours
        double score = (logsLast30Days / 10.0) * 100.0;
        return Math.min(score, 100.0);
    }

    // ─── Dimension 4 : Health Trend Score (15%) ───────────────────────────────
    // Le BMI s'améliore-t-il (se rapproche de la normale) ?
    private double calculateTrendScore(List<HealthHistory> history) {
        if (history.size() < 2) return 50.0; // Pas assez de données → neutre

        // Comparer premier (récent) vs dernier (ancien) enregistrement
        double recentBmi = history.get(0).getBmi();
        double oldBmi    = history.get(history.size() - 1).getBmi();

        double change = Math.abs(recentBmi - 22.0) - Math.abs(oldBmi - 22.0);
        // change négatif = on se rapproche de 22 (idéal) = amélioration

        if (change < -0.5) return 100.0;  // Nette amélioration vers la normale
        if (change < 0)    return 80.0;   // Légère amélioration
        if (change < 0.5)  return 60.0;   // Stable
        return 30.0;                       // Dégradation
    }

    // ─── Statut ────────────────────────────────────────────────────────────────
    private String determineStatus(double score) {
        if (score >= 80) return "ELITE";
        if (score >= 65) return "FIT";
        if (score >= 45) return "CAUTION";
        return "UNFIT";
    }

    private String determineColor(String status) {
        return switch (status) {
            case "ELITE"   -> "#22c55e";   // vert
            case "FIT"     -> "#eab308";   // jaune
            case "CAUTION" -> "#f97316";   // orange
            default        -> "#ef4444";   // rouge
        };
    }

    private String determineBMICategory(double bmi) {
        if (bmi < 18.5)              return "Underweight";
        if (bmi < 25.0)              return "Normal";
        if (bmi < 30.0)              return "Overweight";
        return                              "Obese";
    }

    // ─── Recommandations intelligentes ────────────────────────────────────────
    private List<String> generateRecommendations(double bmiScore, double hydration,
                                                   double consistency, double trend,
                                                   double currentBmi) {
        List<String> tips = new ArrayList<>();

        if (bmiScore < 65)
            tips.add("⚖️ Votre BMI (" + String.format("%.1f", currentBmi) +
                    ") est hors zone optimale — consultez un nutritionniste");

        if (hydration < 60)
            tips.add("💧 Hydratation insuffisante — pensez à boire régulièrement");

        if (consistency < 40)
            tips.add("📊 Loggez vos données de santé plus régulièrement pour un meilleur suivi");

        if (trend < 50)
            tips.add("📉 Votre BMI s'éloigne de la zone normale — adoptez de meilleures habitudes");

        if (tips.isEmpty())
            tips.add("✅ Excellent profil de forme — continuez comme ça !");

        return tips;
    }
}
