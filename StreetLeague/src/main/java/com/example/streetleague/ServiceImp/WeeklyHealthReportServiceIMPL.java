package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.WaterStreak;
import com.example.streetleague.Repository.*;
import com.example.streetleague.dto.WeeklyHealthReportDTO;
import com.example.streetleague.Entity.DailyWaterLog;
import com.example.streetleague.Entity.HealthHistory;
import com.example.streetleague.Entity.UserGoal;
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
    private final WaterStreakRepository waterStreakRepo;

    public WeeklyHealthReportDTO generateReport(Long userId) {


        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);


        List<HealthHistory> weeklyHistory =
                healthHistoryRepo.findByUserIdAndDateBetweenOrderByDateDesc(
                        userId, weekStart, today);

        // Changement de poids: dernier - premier de la semaine
        double weightChange = 0.0;
        if (weeklyHistory.size() >= 2) {
            double newest = weeklyHistory.get(0).getWeight();
            double oldest = weeklyHistory.get(weeklyHistory.size() - 1).getWeight();
            weightChange = Math.round((newest - oldest) * 10.0) / 10.0;
        }

        // Valeurs actuelles depuis User (plus récentes)
        double currentBmi    = user.getBmi()    != null ? user.getBmi()    : 0.0;
        double currentWeight = user.getWeight() != null ? user.getWeight() : 0.0;
        double currentHeight = user.getHeight() != null ? user.getHeight() : 0.0;

        // BMI history — round à 2 décimales
        List<WeeklyHealthReportDTO.BmiEntry> bmiHistory = weeklyHistory.stream()
                .map(h -> WeeklyHealthReportDTO.BmiEntry.builder()
                        .date(h.getDate())
                        .bmi(Math.round(h.getBmi() * 100.0) / 100.0)  // ✅ 2 décimales
                        .weight(h.getWeight())
                        .build())
                .collect(Collectors.toList());


        List<DailyWaterLog> waterLogs =
                dailyWaterLogRepo.findByUserIdAndDateBetween(userId, weekStart, today);

        int totalWater = waterLogs.stream()
                .mapToInt(DailyWaterLog::getTotalMl)
                .sum();

        long daysGoalReached =
                dailyWaterLogRepo.countByUserIdAndDateBetweenAndGoalReachedTrue(
                        userId, weekStart, today);

        // Moyenne sur 7 jours (pas seulement les jours avec logs)
        double avgDailyWater = Math.round((double) totalWater / 7.0 * 10.0) / 10.0;


        List<UserGoal> goals = userGoalRepo.findByUserId(userId);

        List<WeeklyHealthReportDTO.GoalSummary> goalSummaries = goals.stream()
                .map(g -> WeeklyHealthReportDTO.GoalSummary.builder()
                        .goalType(g.getGoalType())
                        .targetValue(g.getTargetValue())
                        .achieved(g.isAchieved())
                        .build())
                .collect(Collectors.toList());


        WaterStreak streak = waterStreakRepo.findByUserId(userId).orElse(null);

        int currentStreak = 0;
        int longestStreak = 0;

        if (streak != null) {
            longestStreak = streak.getLongestStreak();

            // Vérifier si le streak est encore actif
            // Si lastGoalDate < hier → streak cassé
            if (streak.getLastGoalDate() != null) {
                boolean streakActive = !streak.getLastGoalDate()
                        .isBefore(today.minusDays(1));

                if (streakActive) {
                    currentStreak = streak.getCurrentStreak();
                } else {
                    // Streak cassé → reset en DB
                    streak.setCurrentStreak(0);
                    waterStreakRepo.save(streak);
                    currentStreak = 0;
                }
            }
        }


        return WeeklyHealthReportDTO.builder()
                .fullName(user.getFullName())
                .weekStart(weekStart)
                .weekEnd(today)
                // Body metrics
                .currentWeight(currentWeight)
                .currentHeight(currentHeight)
                .currentBmi(Math.round(currentBmi * 100.0) / 100.0) // ✅ 2 décimales
                .weightChangeThisWeek(weightChange)
                // Hydratation
                .totalWaterConsumedMl(totalWater)
                .daysGoalReached((int) daysGoalReached)
                .avgDailyWaterMl(avgDailyWater)
                // Goals
                .goals(goalSummaries)
                // BMI history
                .bmiHistory(bmiHistory)
                // Streak ✅
                .currentWaterStreak(currentStreak)
                .longestWaterStreak(longestStreak)
                .build();
    }
}