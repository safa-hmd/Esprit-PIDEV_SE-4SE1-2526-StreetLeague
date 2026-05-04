package com.example.streetleague.Scheduler;

import com.example.streetleague.Entity.DailyWaterLog;
import com.example.streetleague.Entity.UserBadge;
import com.example.streetleague.Entity.UserGoal;
import com.example.streetleague.Entity.waterReminder;
import com.example.streetleague.Repository.DailyWaterLogRepository;
import com.example.streetleague.Repository.UserBadgeRepository;
import com.example.streetleague.Repository.UserGoalRepository;
import com.example.streetleague.Repository.WaterReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j

public class WaterReminderScheduler {

    private final WaterReminderRepository waterReminderRepo;
    private final DailyWaterLogRepository dailyWaterLogRepo;
    private final UserBadgeRepository userBadgeRepo;
    private final UserGoalRepository userGoalRepo;

    /**
     * S'exécute toutes les heures.
     * Pour chaque reminder actif, on vérifie si c'est l'heure d'envoyer
     * un rappel selon la fréquence définie, on met à jour le DailyWaterLog,
     * et on vérifie si l'objectif est atteint pour attribuer un badge.
     */
    @Scheduled(fixedRate = 3600000) // toutes les 1h
    public void processWaterReminders() {
//        log.info("⏰ WaterReminderScheduler démarré à {}", LocalDateTime.now());

        // Récupère tous les reminders actifs
        List<waterReminder> activeReminders = waterReminderRepo.findByActiveTrue();

        for (waterReminder reminder : activeReminders) {
            Long userId = reminder.getUser().getIdUser();
            LocalDate today = LocalDate.now();
            int currentHour = LocalDateTime.now().getHour();

            // ── Business Logic 1 : est-ce l'heure du rappel ? ──────────────
            // On vérifie si l'heure actuelle correspond à un multiple
            // de la fréquence définie par l'utilisateur
            int reminderHour = reminder.getTime().getHour();
            boolean isReminderTime = (currentHour - reminderHour) % reminder.getFrequency() == 0;

            if (!isReminderTime) {
//                log.info("⏭️ Pas encore l'heure pour user {}", userId);
                continue;
            }

//            log.info("💧 Traitement du reminder pour user {}", userId);

            // ── Business Logic 2 : mise à jour du DailyWaterLog ────────────
            // On cherche le log du jour, sinon on en crée un nouveau
            Optional<DailyWaterLog> existingLog =
                    dailyWaterLogRepo.findByUserIdAndDate(userId, today);

            DailyWaterLog log_today;

            if (existingLog.isPresent()) {
                // Le log du jour existe → on ajoute la quantité du rappel
                log_today = existingLog.get();
                log_today.setTotalMl(log_today.getTotalMl() + reminder.getQuantity());
            } else {
                // Premier rappel de la journée → on crée un nouveau log
                log_today = new DailyWaterLog();
                log_today.setUser(reminder.getUser());
                log_today.setDate(today);
                log_today.setTotalMl(reminder.getQuantity());
                log_today.setGoalMl(reminder.getQuantity() * reminder.getFrequency());
                // goalMl estimé = quantité × fréquence par jour
            }

            // ── Business Logic 3 : vérifier si objectif atteint ────────────
            // On cherche l'objectif WATER de l'utilisateur
            Optional<UserGoal> waterGoal =
                    userGoalRepo.findByUserIdAndGoalType(userId, "WATER");

            if (waterGoal.isPresent()) {
                double target = waterGoal.get().getTargetValue();

                if (log_today.getTotalMl() >= target) {
                    // Objectif atteint aujourd'hui
                    log_today.setGoalReached(true);
//                    log.info("🎯 Objectif eau atteint pour user {} : {}ml", userId, log_today.getTotalMl());

                    // ── Business Logic 4 : attribution du badge ─────────────
                    // On vérifie si l'user a atteint l'objectif 7 jours consécutifs
                    LocalDate sevenDaysAgo = today.minusDays(6);
                    List<DailyWaterLog> weekLogs =
                            dailyWaterLogRepo.findByUserIdAndDateBetween(userId, sevenDaysAgo, today);

                    long consecutiveDays = weekLogs.stream()
                            .filter(DailyWaterLog::isGoalReached)
                            .count();

                    if (consecutiveDays >= 7) {
                        boolean alreadyHasBadge = userBadgeRepo
                                .findByUserId(userId)
                                .stream()
                                .anyMatch(b -> b.getBadgeType().equals("WATER_WEEK"));

                        if (!alreadyHasBadge) {
                            UserBadge badge = new UserBadge();
                            badge.setUser(reminder.getUser());
                            badge.setBadgeType("WATER_WEEK");
                            badge.setDescription("7 jours consécutifs d'hydratation atteinte !");
                            badge.setEarnedDate(today);
                            userBadgeRepo.save(badge);
//                            log.info("🏅 Badge WATER_WEEK attribué à user {}", userId);
                        }
                    }

                    // Marquer le goal comme achieved
                    waterGoal.get().setAchieved(true);
                    userGoalRepo.save(waterGoal.get());
                }
            }

            // Sauvegarder le log mis à jour en DB
            dailyWaterLogRepo.save(log_today);
//            log.info("✅ DailyWaterLog sauvegardé pour user {} : {}ml", userId, log_today.getTotalMl());
        }

//        log.info("✔️ WaterReminderScheduler terminé à {}", LocalDateTime.now());
    }
}