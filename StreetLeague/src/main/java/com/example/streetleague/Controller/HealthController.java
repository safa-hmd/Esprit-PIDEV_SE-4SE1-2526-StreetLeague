package com.example.streetleague.Controller;
import com.example.streetleague.ServiceImp.WeeklyHealthReportServiceIMPL;
import com.example.streetleague.dto.WeeklyHealthReportDTO;
import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.GoalDTO;
import com.example.streetleague.dto.RewardDTO;
import com.example.streetleague.dto.WaterLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final UserRepository userRepository;
    private final HealthHistoryRepository healthHistoryRepository;
    private final DailyWaterLogRepository waterLogRepository;
    private final UserGoalRepository goalRepository;
    private final UserBadgeRepository badgeRepository;
    private final SpinResultRepository spinRepository;
    private final WeeklyHealthReportServiceIMPL weeklyReportService;
    private final WaterStreakRepository streakRepository;


    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateHealth(@PathVariable Long userId,
                                             @RequestBody Map<String, Double> data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double weight = data.get("weight");
        Double height = data.get("height");
        Double bmi    = data.get("bmi");

        // ✅ VALIDATION — rejeter les valeurs aberrantes
        if (weight == null || height == null || bmi == null) {
            return ResponseEntity.badRequest().build();
        }
        if (bmi > 60 || bmi < 10 || weight > 300 || height > 250) {
            return ResponseEntity.badRequest().build();
        }

        user.setWeight(weight);
        user.setHeight(height);
        user.setBmi(bmi);
        userRepository.save(user);

        HealthHistory history = new HealthHistory();
        history.setWeight(weight);
        history.setHeight(height);
        history.setBmi(bmi);
        history.setDate(LocalDate.now());
        history.setUser(user);
        healthHistoryRepository.save(history);

        goalRepository.findByUserIdAndGoalType(userId, "BMI").ifPresent(goal -> {
            double target = goal.getTargetValue();
            boolean inRange = bmi >= (target - 2.4) && bmi <= (target + 2.9);
            if (inRange && !goal.isAchieved()) {
                goal.setAchieved(true);
                goalRepository.save(goal);
                unlockSpin(userId, user);
            }
        });

        return ResponseEntity.ok(user);
    }


    @PostMapping("/water/log/{userId}")
    public ResponseEntity<DailyWaterLog> logWater(@PathVariable Long userId,
                                                  @RequestBody WaterLogDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();

        DailyWaterLog log = waterLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> {
                    DailyWaterLog newLog = new DailyWaterLog();
                    newLog.setUser(user);
                    newLog.setDate(today);
                    newLog.setTotalMl(0);
                    return newLog;
                });

        log.setTotalMl(log.getTotalMl() + dto.getAmount());

        goalRepository.findByUserIdAndGoalType(userId, "WATER").ifPresent(goal -> {
            log.setGoalMl((int) goal.getTargetValue());
            if (log.getTotalMl() >= goal.getTargetValue() && !log.isGoalReached()) {
                log.setGoalReached(true);
                // ✅ Mettre à jour le streak
                updateStreak(userId, user, today);
                checkAndAwardBadge(userId, user);
            }
        });

        waterLogRepository.save(log);
        return ResponseEntity.ok(log);
    }

    // ✅ Nouvelle méthode streak
    private void updateStreak(Long userId, User user, LocalDate today) {
        WaterStreak streak = streakRepository.findByUserId(userId)
                .orElseGet(() -> {
                    WaterStreak s = new WaterStreak();
                    s.setUser(user);
                    s.setCurrentStreak(0);
                    s.setLongestStreak(0);
                    return s;
                });

        // Si hier le goal était atteint → on continue le streak
        // Sinon → on repart de 1
        if (streak.getLastGoalDate() != null &&
                streak.getLastGoalDate().equals(today.minusDays(1))) {
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else if (streak.getLastGoalDate() == null ||
                !streak.getLastGoalDate().equals(today)) {
            // Nouveau streak (pas déjà compté aujourd'hui)
            streak.setCurrentStreak(1);
        }

        // Update le record
        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }

        streak.setLastGoalDate(today);
        streakRepository.save(streak);
    }


    @PostMapping("/goal/{userId}")
    public ResponseEntity<UserGoal> setGoal(@PathVariable Long userId,
                                            @RequestBody GoalDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGoal goal = goalRepository.findByUserIdAndGoalType(userId, dto.getGoalType())
                .orElseGet(() -> {
                    UserGoal g = new UserGoal();
                    g.setUser(user);
                    g.setGoalType(dto.getGoalType());
                    return g;
                });

        goal.setTargetValue(dto.getTargetValue());
        goal.setAchieved(false);
        goalRepository.save(goal);

        return ResponseEntity.ok(goal);
    }


    @GetMapping("/goal/{userId}")
    public ResponseEntity<List<UserGoal>> getGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(goalRepository.findAll().stream()
                .filter(g -> g.getUser().getIdUser().equals(userId))
                .toList());
    }


    @GetMapping("/badges/{userId}")
    public ResponseEntity<List<UserBadge>> getBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(badgeRepository.findByUserId(userId));
    }


    @GetMapping("/spin/status/{userId}")
    public ResponseEntity<Map<String, Boolean>> spinStatus(@PathVariable Long userId) {
        SpinResult spin = spinRepository.findByUserId(userId).orElse(null);

        boolean canSpin = spin != null && (!spin.isHasSpun() || spin.isCanRetry());

        return ResponseEntity.ok(Map.of("canSpin", canSpin));
    }


    @PostMapping("/spin/{userId}")
    public ResponseEntity<RewardDTO> spin(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SpinResult existing = spinRepository.findByUserId(userId).orElse(null);

        // Déjà spinné et pas Try Again → refus
        if (existing != null && existing.isHasSpun() && !existing.isCanRetry()) {
            RewardDTO dto = new RewardDTO();
            dto.setResult("ALREADY_USED");
            dto.setMessage("Vous avez déjà utilisé votre spin !");
            dto.setCanRetry(false);
            dto.setSegmentIndex(-1);
            return ResponseEntity.ok(dto);
        }

        // 🎰 Logique random — TRY_AGAIN x2 = plus fréquent
        String[] rewards = {"BADGE", "FREE_DELIVERY", "COUPON_5", "COUPON_10", "TRY_AGAIN", "TRY_AGAIN"};
        String result = rewards[new Random().nextInt(rewards.length)];

        SpinResult spin = existing != null ? existing : new SpinResult();
        spin.setUser(user);
        spin.setResult(result);
        spin.setHasSpun(true);
        spin.setCanRetry(result.equals("TRY_AGAIN"));
        spin.setSpinDate(LocalDate.now());
        spinRepository.save(spin);

        // Si reward → sauvegarder en badge
        if (!result.equals("TRY_AGAIN")) {
            UserBadge badge = new UserBadge();
            badge.setUser(user);
            badge.setBadgeType(result);
            badge.setEarnedDate(LocalDate.now());
            badge.setDescription(getRewardDescription(result));
            badgeRepository.save(badge);
        }

        int tryAgainIndex = new Random().nextBoolean() ? 4 : 5;

        Map<String, Integer> segmentMap = new HashMap<>();
        segmentMap.put("BADGE", 0);
        segmentMap.put("FREE_DELIVERY", 1);
        segmentMap.put("COUPON_5", 2);
        segmentMap.put("COUPON_10", 3);
        segmentMap.put("TRY_AGAIN", tryAgainIndex);

        RewardDTO dto = new RewardDTO();
        dto.setResult(result);
        dto.setCanRetry(result.equals("TRY_AGAIN"));
        dto.setMessage(getRewardDescription(result));
        dto.setSegmentIndex(segmentMap.getOrDefault(result, 4));
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/water/today/{userId}")
    //@PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<List<DailyWaterLog>> getTodayLogs(@PathVariable Long userId) {
        List<DailyWaterLog> logs = waterLogRepository.findByUserIdAndDate(userId, LocalDate.now())
                .map(List::of)
                .orElse(List.of());
        return ResponseEntity.ok(logs);
    }



    private void checkAndAwardBadge(Long userId, User user) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<DailyWaterLog> logs = waterLogRepository.findByUserIdAndDateBetween(userId, weekAgo, today);
        long daysGoalReached = logs.stream().filter(DailyWaterLog::isGoalReached).count();

        if (daysGoalReached >= 1) {
            boolean alreadyAwarded = badgeRepository.findByUserId(userId).stream()
                    .anyMatch(b -> b.getBadgeType().equals("WATER_WEEK") &&
                            b.getEarnedDate().isAfter(weekAgo));

            if (!alreadyAwarded) {
                UserBadge badge = new UserBadge();
                badge.setUser(user);
                badge.setBadgeType("WATER_WEEK");
                badge.setDescription("🏆 7 jours d'objectif eau atteint ! Tournez la roue !");
                badge.setEarnedDate(today);
                badgeRepository.save(badge);

                unlockSpin(userId, user);
            }
        }
    }

    private void unlockSpin(Long userId, User user) {
        SpinResult spin = spinRepository.findByUserId(userId).orElseGet(() -> {
            SpinResult s = new SpinResult();
            s.setUser(user);
            return s;
        });
        spin.setHasSpun(false);
        spin.setCanRetry(false);
        spin.setResult(null);
        spinRepository.save(spin);
    }

    private String getRewardDescription(String result) {
        return switch (result) {
            case "BADGE"         -> "🏆 Félicitations ! Vous avez gagné un Badge exclusif !";
            case "FREE_DELIVERY" -> "🚚 Livraison gratuite sur votre prochain achat !";
            case "COUPON_5"      -> "🎟️ Coupon -5% sur le store !";
            case "COUPON_10"     -> "🎟️ Coupon -10% sur le store !";
            case "TRY_AGAIN"     -> "😅 Pas de chance... Retentez votre chance !";
            default              -> "";
        };
    }
    @DeleteMapping("/water/reset/{userId}")
    //@PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<Void> resetTodayWater(@PathVariable Long userId) {
        waterLogRepository.findByUserIdAndDate(userId, LocalDate.now())
                .ifPresent(log -> {
                    log.setTotalMl(0);
                    log.setGoalReached(false);
                    waterLogRepository.save(log);
                });
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weekly-report/{userId}")
    public ResponseEntity<WeeklyHealthReportDTO> getWeeklyReport(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(weeklyReportService.generateReport(userId));
    }

    // 1. كل users مع health data
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsersHealth() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> u.getBmi() != null)
                        .map(u -> {
                            Map<String, Object> map = new java.util.LinkedHashMap<>();
                            map.put("userId",      u.getIdUser());
                            map.put("fullName",    u.getFullName());
                            map.put("email",       u.getEmail());
                            map.put("weight",      u.getWeight());
                            map.put("height",      u.getHeight());
                            map.put("bmi",         u.getBmi());
                            map.put("lastUpdated", LocalDate.now().toString());
                            return map;
                        })
                        .toList()
        );
    }

    // 2. Water logs par user (historique complet)
    @GetMapping("/water-logs/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DailyWaterLog>> getWaterLogs(@PathVariable Long userId) {
        return ResponseEntity.ok(
                waterLogRepository.findByUserIdAndDateBetween(
                        userId,
                        LocalDate.now().minusDays(30),
                        LocalDate.now()
                )
        );
    }


    @GetMapping("/streak/{userId}")
    public ResponseEntity<Map<String, Object>> getStreak(@PathVariable Long userId) {
        WaterStreak streak = streakRepository.findByUserId(userId).orElse(null);

        if (streak == null) {
            return ResponseEntity.ok(Map.of(
                    "currentStreak", 0,
                    "longestStreak", 0,
                    "lastGoalDate", ""
            ));
        }

        // Vérifier si le streak est encore actif (pas de break hier)
        LocalDate today = LocalDate.now();
        boolean streakBroken = streak.getLastGoalDate() != null &&
                streak.getLastGoalDate().isBefore(today.minusDays(1));

        int currentStreak = streakBroken ? 0 : streak.getCurrentStreak();

        // Si streak cassé, reset
        if (streakBroken) {
            streak.setCurrentStreak(0);
            streakRepository.save(streak);
        }

        return ResponseEntity.ok(Map.of(
                "currentStreak", currentStreak,
                "longestStreak", streak.getLongestStreak(),
                "lastGoalDate", streak.getLastGoalDate() != null ? streak.getLastGoalDate().toString() : ""
        ));
    }
}