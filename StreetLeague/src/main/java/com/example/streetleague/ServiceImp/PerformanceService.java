package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.PlayerAttendance;
import com.example.streetleague.Entity.PlayerStreak;
import com.example.streetleague.Repository.PlayerAttendanceRepository;
import com.example.streetleague.Repository.PlayerStreakRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.PlayerStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PlayerStreakRepository streakRepo;
    private final PlayerAttendanceRepository attendanceRepo;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    // ── Configuration mode test ──────────────────────────────────────────────
    /** true  → 30 secondes = 1 "jour" (pour validation sans attendre 24h) */
    @Value("${app.performance.test-mode:false}")
    private boolean testMode;

    /** Durée d'une fenêtre en secondes (ex: 30 pour les tests) */
    @Value("${app.performance.window-seconds:30}")
    private long windowSeconds;

    private static final double DECAY_FACTOR    = 0.1;
    private static final int    FIXED_THRESHOLD = 19;
    private static final int    STREAK_7_PTS    = 5;
    private static final int    STREAK_14_PTS   = 15;
    private static final int    STREAK_21_PTS   = 30;

    // Points gagnés par check-in (nouveau jour seulement)
    private static final int    CHECKIN_TRAINING_PTS = 1;  // +1 pt / jour training
    private static final int    CHECKIN_MATCH_PTS    = 2;  // +2 pts / jour match
    private static final int    CHECKIN_BOTH_PTS     = 3;  // +3 pts / jour both

    /** Epoch-second de la date de référence pour le mode test : 2024-01-01 00:00:00 UTC */
    private static final long TEST_BASE_EPOCH_SECOND =
            LocalDate.of(2024, 1, 1).toEpochDay() * 86_400L;

    // ── Date effective ──────────────────────────────────────────────────────
    /**
     * En mode test  : chaque fenêtre de `windowSeconds` secondes = 1 "jour".
     *   La date retournée est calculée depuis l'epoch Unix divisée par windowSeconds.
     *   Ex : windowSeconds=30 → une nouvelle date toutes les 30 secondes.
     *
     * En production : retourne simplement LocalDate.now().
     *
     * STREAK RESET : si une fenêtre (ou un vrai jour) est manqué,
     * le streak consécutif REPART À 0. C'est le comportement voulu.
     */
    private LocalDate getEffectiveDate() {
        if (testMode) {
            // Chaque fenêtre de windowSeconds = 1 "jour" virtuel.
            // On calcule un index relatif depuis 2024-01-01 pour éviter une date
            // de l'an 164 000 (ce qui se produisait avec LocalDate.ofEpochDay(windowIndex absolu)).
            long relativeSeconds = Instant.now().getEpochSecond() - TEST_BASE_EPOCH_SECOND;
            long windowIndex     = relativeSeconds / windowSeconds;
            return LocalDate.of(2024, 1, 1).plusDays(windowIndex);
        }
        return LocalDate.now();
    }

    /**
     * Retourne la date "il y a N fenêtres" pour la plage de recherche.
     * En mode test : N fenêtres de 30s en arrière.
     * En production : N jours en arrière.
     */
    private LocalDate getDateBefore(LocalDate reference, int periods) {
        return reference.minusDays(periods);
    }

    // ── CHECK-IN (sans position) ──────────────────────────────────────────
    @Transactional
    public PlayerStatsDto checkin(Long playerId, String attendanceType) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        if (player.getRole() != Role.PLAYER) {
            throw new RuntimeException("Only PLAYER users can check in");
        }

        String normalizedAttendanceType = normalizeAttendanceType(attendanceType);
        // En mode test : localDate = fenêtre de 30s courante
        // En production : localDate = jour réel
        LocalDate effectiveDate = getEffectiveDate();

        log.info("[CHECK-IN] player={} type={} effectiveDate={} testMode={}",
                playerId, normalizedAttendanceType, effectiveDate, testMode);

        Optional<PlayerAttendance> existing =
                attendanceRepo.findByPlayerIdAndAttendanceDate(playerId, effectiveDate);
        boolean isNewWindow = existing.isEmpty();

        if (existing.isPresent()) {
            // Peut re-cliquer dans la même fenêtre → met à jour le type
            PlayerAttendance attendance = existing.get();
            attendance.setIsPresent(true);
            attendance.setAttendanceType(normalizedAttendanceType);
            attendanceRepo.saveAndFlush(attendance);
        } else {
            // Nouvelle fenêtre (nouveau "jour") → crée une nouvelle entrée
            attendanceRepo.saveAndFlush(PlayerAttendance.builder()
                    .playerId(playerId)
                    .attendanceDate(effectiveDate)
                    .isPresent(true)
                    .attendanceType(normalizedAttendanceType)
                    .intensity(1)
                    .build());
        }

        PlayerStreak streak = updateStreak(playerId, normalizedAttendanceType, isNewWindow);
        return toDto(streak, player.getFullName());
    }

    // ── MISE À JOUR DU STREAK ────────────────────────────────────────────
    /**
     * @param attendanceType  type de l'activité du jour (TRAINING / MATCH / BOTH)
     * @param isNewWindow     true si c'est un nouveau "jour" virtuel (pas un re-clic)
     */
    private PlayerStreak updateStreak(Long playerId, String attendanceType, boolean isNewWindow) {
        LocalDate today     = getEffectiveDate();
        LocalDate thirtyAgo = getDateBefore(today, 30);

        List<PlayerAttendance> attendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, thirtyAgo, today);

        /*
         * CALCUL DU STREAK CONSÉCUTIF :
         * On remonte depuis aujourd'hui (fenêtre 0) vers le passé.
         * Dès qu'une fenêtre (jour/30s) est absente → ARRÊT → streak reset.
         *
         * Exemple : présent J0, J1, J2, absent J3 → streak = 3
         * Si absent J0 (pas encore check-in aujourd'hui) → streak = 0 (i=0 skip)
         */
        int currentStreak = 0;
        for (int i = 0; i < 30; i++) {
            LocalDate window = today.minusDays(i);
            boolean present = attendances.stream()
                    .anyMatch(a -> a.getAttendanceDate().equals(window)
                            && Boolean.TRUE.equals(a.getIsPresent()));
            if (present) {
                currentStreak++;
            } else if (i > 0) {
                // Fenêtre manquée → streak brisé, on s'arrête
                break;
            }
            // i == 0 et absent : pas encore checké aujourd'hui,
            // on continue pour voir les jours précédents
        }

        // Toujours chercher en DB : si existe -> mise à jour, sinon -> créer
        PlayerStreak streak = streakRepo.findByPlayerId(playerId)
                .orElseGet(() -> {
                    PlayerStreak newStreak = new PlayerStreak();
                    newStreak.setPlayerId(playerId);
                    newStreak.setCurrentStreak(0);
                    newStreak.setBestStreak(0);
                    newStreak.setTotalPoints(0);
                    newStreak.setMomentumScore(0.0);
                    newStreak.setFatigueRisk(0.0);
                    return newStreak;
                });

        int oldStreak = streak.getCurrentStreak() != null ? streak.getCurrentStreak() : 0;
        streak.setCurrentStreak(currentStreak);

        int total = streak.getTotalPoints() != null ? streak.getTotalPoints() : 0;

        // 1️⃣ Points par check-in (uniquement si nouvelle fenêtre = nouveau "jour")
        if (isNewWindow) {
            int dailyPts = switch (attendanceType) {
                case "BOTH"  -> CHECKIN_BOTH_PTS;
                case "MATCH" -> CHECKIN_MATCH_PTS;
                default      -> CHECKIN_TRAINING_PTS;
            };
            total += dailyPts;
            log.info("[POINTS] player={} +{} pts ({})", playerId, dailyPts, attendanceType);
        }

        // 2️⃣ Bonus jalons de streak (7 / 14 / 21 jours)
        if (currentStreak >= 7  && oldStreak < 7)  total += STREAK_7_PTS;
        if (currentStreak >= 14 && oldStreak < 14) total += STREAK_14_PTS;
        if (currentStreak >= 21 && oldStreak < 21) total += STREAK_21_PTS;
        streak.setTotalPoints(total);

        int best = streak.getBestStreak() != null ? streak.getBestStreak() : 0;
        if (currentStreak > best) streak.setBestStreak(currentStreak);

        streak.setCurrentBadge(
                currentStreak >= 21 ? "IRON WILL"  :
                        currentStreak >= 14 ? "FORTNIGHT"  :
                                currentStreak >= 7  ? "WEEK STREAK" : null);

        double momentum = computeMomentum(attendances);
        streak.setMomentumScore(momentum);
        streak.setFatigueRisk(computeFatigue(currentStreak, momentum));

        streak.setLastAttendanceDate(today);
        streak.setLastUpdated(LocalDateTime.now());

        return streakRepo.saveAndFlush(streak);
    }

    // ── CALCULS (sans position) ────────────────────────────────────

    private double computeMomentum(List<PlayerAttendance> attendances) {
        LocalDate today = LocalDate.now();

        double weighted = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .mapToDouble(a -> {
                    long daysAgo = ChronoUnit.DAYS.between(a.getAttendanceDate(), today);
                    return Math.exp(-DECAY_FACTOR * daysAgo);
                })
                .sum();

        double maxPossible = 30.0;
        double score = Math.min(100.0, (weighted / maxPossible) * 100.0);
        return Math.round(score * 10.0) / 10.0;
    }

    private double computeFatigue(int currentStreak, double momentumScore) {
        double rawRisk   = (double) currentStreak / FIXED_THRESHOLD;
        double loadFactor = 0.8 + (momentumScore / 100.0) * 0.4;
        return Math.min(1.0, Math.round(rawRisk * loadFactor * 100.0) / 100.0);
    }

    // ── ENDPOINTS PUBLICS ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getPlayerStats(Long playerId) {
        if (!isPlayer(playerId)) return Optional.empty();

        String name = getPlayerName(playerId);

        Optional<PlayerStreak> streakOpt = streakRepo.findById(playerId);
        if (streakOpt.isEmpty()) {
            return Optional.of(PlayerStatsDto.builder()
                    .playerId(playerId)
                    .playerName(name)
                    .currentStreak(0)
                    .bestStreak(0)
                    .totalPoints(0)
                    .fatigueRisk(0.0)
                    .riskLevel("LOW")
                    .momentumScore(0.0)
                    .status("FAIBLE")
                    .badge(null)
                    .build());
        }

        PlayerStreak streak = streakOpt.get();
        LocalDate today     = getEffectiveDate();
        LocalDate thirtyAgo = getDateBefore(today, 30);

        List<PlayerAttendance> attendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, thirtyAgo, today);

        int currentStreak = 0;
        for (int i = 0; i < 30; i++) {
            LocalDate day = today.minusDays(i);
            boolean present = attendances.stream()
                    .anyMatch(a -> a.getAttendanceDate().equals(day)
                            && Boolean.TRUE.equals(a.getIsPresent()));
            if (present) { currentStreak++; }
            else if (i > 0) { break; }
        }

        double momentum = computeMomentum(attendances);
        double fatigue  = computeFatigue(currentStreak, momentum);

        String riskLevel = fatigue >= 0.85 ? "CRITICAL"
                : fatigue >= 0.70 ? "HIGH"
                : fatigue >= 0.50 ? "MODERATE" : "LOW";
        String status = currentStreak >= 21 ? "OPTIMAL"
                : currentStreak >= 10 ? "ACTIF" : "FAIBLE";
        String badge = currentStreak >= 21 ? "IRON WILL"
                : currentStreak >= 14 ? "FORTNIGHT"
                : currentStreak >= 7  ? "WEEK STREAK" : null;

        return Optional.of(PlayerStatsDto.builder()
                .playerId(playerId)
                .playerName(name)
                .currentStreak(currentStreak)
                .bestStreak(streak.getBestStreak() != null ? streak.getBestStreak() : 0)
                .totalPoints(streak.getTotalPoints() != null ? streak.getTotalPoints() : 0)
                .momentumScore(momentum)
                .fatigueRisk(fatigue)
                .riskLevel(riskLevel)
                .status(status)
                .badge(badge)
                .build());
    }

    public List<PlayerStatsDto> getGlobalLeaderboard() {
        Map<Long, String> playerNames = loadAllPlayerNames();
        List<PlayerStreak> streaks    = streakRepo.findAllByOrderByTotalPointsDesc();

        int[] rank = {1};
        return streaks.stream()
                .filter(s -> playerNames.containsKey(s.getPlayerId()))
                .map(s -> toDto(s, playerNames.get(s.getPlayerId()))
                        .toBuilder().rank(rank[0]++).build())
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getTeamLeaderboard(Long teamId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        Map<Long, String> playerNames = loadAllPlayerNames();
        List<PlayerStreak> streaks    = streakRepo.getTeamLeaderboard(teamId);

        int[] rank = {1};
        return streaks.stream()
                .filter(s -> playerNames.containsKey(s.getPlayerId()))
                .map(s -> toDto(s, playerNames.get(s.getPlayerId()))
                        .toBuilder().rank(rank[0]++).build())
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getGlobalMomentum() {
        Map<Long, String> playerNames = loadAllPlayerNames();
        return streakRepo.findAllByOrderByCurrentStreakDesc().stream()
                .filter(s -> playerNames.containsKey(s.getPlayerId()))
                .map(s -> {
                    double momentum   = s.getMomentumScore() != null ? s.getMomentumScore() : 0.0;
                    int currentStreak = s.getCurrentStreak() != null ? s.getCurrentStreak() : 0;
                    int bestStreak    = s.getBestStreak()    != null ? s.getBestStreak()    : 0;
                    String trend = currentStreak >= bestStreak * 0.9 ? "UP"
                            : currentStreak <= bestStreak * 0.5 ? "DOWN" : "STABLE";
                    return PlayerStatsDto.builder()
                            .playerId(s.getPlayerId())
                            .playerName(playerNames.get(s.getPlayerId()))
                            .momentumScore(momentum)
                            .trend(trend)
                            .currentStreak(currentStreak)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getGlobalFatigueAlerts() {
        Map<Long, String> playerNames = loadAllPlayerNames();
        return streakRepo.findAll().stream()
                .filter(s -> playerNames.containsKey(s.getPlayerId()))
                .map(s -> {
                    double risk       = s.getFatigueRisk()   != null ? s.getFatigueRisk()   : 0.0;
                    int currentStreak = s.getCurrentStreak() != null ? s.getCurrentStreak() : 0;
                    String riskLevel;
                    String recommendation;
                    int restDays;
                    if (risk >= 0.85) {
                        riskLevel = "CRITICAL"; recommendation = "Repos obligatoire 3j"; restDays = 3;
                    } else if (risk >= 0.70) {
                        riskLevel = "HIGH";     recommendation = "Repos recommandé 2j";  restDays = 2;
                    } else if (risk >= 0.50) {
                        riskLevel = "MODERATE"; recommendation = "Réduire intensité";    restDays = 1;
                    } else {
                        riskLevel = "LOW";      recommendation = "Normal - continuer";   restDays = 0;
                    }
                    return PlayerStatsDto.builder()
                            .playerId(s.getPlayerId())
                            .playerName(playerNames.get(s.getPlayerId()))
                            .currentStreak(currentStreak)
                            .fatigueRisk(risk)
                            .riskLevel(riskLevel)
                            .recommendation(recommendation)
                            .recommendedRestDays(restDays)
                            .build();
                })
                .filter(dto -> "CRITICAL".equals(dto.getRiskLevel())
                        || "HIGH".equals(dto.getRiskLevel()))
                .collect(Collectors.toList());
    }

    // ── UTILITAIRES ───────────────────────────────────────────────────────

    private Map<Long, String> loadAllPlayerNames() {
        return userRepository.findAllByRole(Role.PLAYER)
                .stream()
                .collect(Collectors.toMap(User::getIdUser, User::getFullName));
    }

    private String getPlayerName(Long playerId) {
        return userRepository.findById(playerId).map(User::getFullName).orElse(null);
    }

    private boolean isPlayer(Long playerId) {
        return userRepository.findById(playerId)
                .map(user -> user.getRole() == Role.PLAYER).orElse(false);
    }

    private String normalizeAttendanceType(String type) {
        if (type == null || type.isBlank()) return "TRAINING";
        String normalized = type.trim().toUpperCase();
        return switch (normalized) {
            case "TRAINING", "MATCH", "BOTH" -> normalized;
            default -> throw new RuntimeException(
                    "Attendance type must be TRAINING, MATCH or BOTH, got: " + type);
        };
    }

    // mergeAttendanceTypes removed — check-in now replaces type directly

    private PlayerStatsDto toDto(PlayerStreak s, String name) {
        int currentStreak = s.getCurrentStreak() != null ? s.getCurrentStreak() : 0;
        String status = currentStreak >= 21 ? "OPTIMAL"
                : currentStreak >= 10 ? "ACTIF" : "FAIBLE";
        double risk = s.getFatigueRisk() != null ? s.getFatigueRisk() : 0.0;
        String riskLevel = risk >= 0.85 ? "CRITICAL"
                : risk >= 0.70 ? "HIGH"
                : risk >= 0.50 ? "MODERATE" : "LOW";

        return PlayerStatsDto.builder()
                .playerId(s.getPlayerId())
                .playerName(name)
                .currentStreak(currentStreak)
                .bestStreak(s.getBestStreak())
                .totalPoints(s.getTotalPoints())
                .momentumScore(s.getMomentumScore())
                .badge(s.getCurrentBadge())
                .status(status)
                .fatigueRisk(risk)
                .riskLevel(riskLevel)
                .build();
    }
}