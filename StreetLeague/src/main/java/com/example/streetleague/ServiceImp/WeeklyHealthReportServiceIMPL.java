package com.example.streetleague.ServiceImp;

import com.example.streetleague.dto.WeeklyHealthReportDTO;
import com.example.streetleague.Entity.DailyWaterLog;
import com.example.streetleague.Entity.HealthHistory;
import com.example.streetleague.Entity.UserGoal;
import com.example.streetleague.Repository.DailyWaterLogRepository;
import com.example.streetleague.Repository.HealthHistoryRepository;
import com.example.streetleague.Repository.UserGoalRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyHealthReportServiceIMPL {

    private final HealthHistoryRepository healthHistoryRepo;
    private final DailyWaterLogRepository dailyWaterLogRepo;
    private final UserGoalRepository userGoalRepo;
    private final UserRepository userRepo;

    public WeeklyHealthReportDTO generateReport(Long userId) {

        // ── Récupérer l'utilisateur ──────────────────────────────────────
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ── Définir la plage de la semaine ───────────────────────────────
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6); // 7 derniers jours

        // ── Keyword 1 : HealthHistory → User + date range (2 tables) ────
        List<HealthHistory> weeklyHistory =
                healthHistoryRepo.findByUserIdAndDateBetweenOrderByDateDesc(
                        userId, weekStart, today
                );

        // Calcul du changement de poids sur la semaine
        double weightChange = 0.0;
        if (weeklyHistory.size() >= 2) {
            double oldest = weeklyHistory.get(weeklyHistory.size() - 1).getWeight();
            double newest = weeklyHistory.get(0).getWeight();
            weightChange = newest - oldest;
        }

        // BMI actuel (depuis User ou dernier HealthHistory)
        double currentBmi = user.getBmi() != null ? user.getBmi() : 0.0;
        double currentWeight = user.getWeight() != null ? user.getWeight() : 0.0;
        double currentHeight = user.getHeight() != null ? user.getHeight() : 0.0;

        // ── Keyword 2 : DailyWaterLog → User + date + goalReached ───────
        List<DailyWaterLog> waterLogs =
                dailyWaterLogRepo.findByUserIdAndDateBetween(userId, weekStart, today);

        int totalWater = waterLogs.stream()
                .mapToInt(DailyWaterLog::getTotalMl)
                .sum();

        long daysGoalReached =
                dailyWaterLogRepo.countByUserIdAndDateBetweenAndGoalReachedTrue(
                        userId, weekStart, today
                );

        double avgDailyWater = waterLogs.isEmpty() ? 0.0 :
                (double) totalWater / waterLogs.size();

        // ── Keyword 3 : UserGoal → User + goalType ───────────────────────
        List<UserGoal> goals = userGoalRepo.findByUserId(userId);

        List<WeeklyHealthReportDTO.GoalSummary> goalSummaries = goals.stream()
                .map(g -> WeeklyHealthReportDTO.GoalSummary.builder()
                        .goalType(g.getGoalType())
                        .targetValue(g.getTargetValue())
                        .achieved(g.isAchieved())
                        .build())
                .collect(Collectors.toList());

        // ── Historique BMI de la semaine ─────────────────────────────────
        List<WeeklyHealthReportDTO.BmiEntry> bmiHistory = weeklyHistory.stream()
                .map(h -> WeeklyHealthReportDTO.BmiEntry.builder()
                        .date(h.getDate())
                        .bmi(h.getBmi())
                        .weight(h.getWeight())
                        .build())
                .collect(Collectors.toList());

        // ── Construire et retourner le rapport ───────────────────────────
        return WeeklyHealthReportDTO.builder()
                .fullName(user.getFullName())
                .weekStart(weekStart)
                .weekEnd(today)
                .currentWeight(currentWeight)
                .currentHeight(currentHeight)
                .currentBmi(currentBmi)
                .weightChangeThisWeek(weightChange)
                .totalWaterConsumedMl(totalWater)
                .daysGoalReached((int) daysGoalReached)
                .avgDailyWaterMl(avgDailyWater)
                .goals(goalSummaries)
                .bmiHistory(bmiHistory)
                .build();
    }
}