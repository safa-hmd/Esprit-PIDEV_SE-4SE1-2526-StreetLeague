package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.PlayerAttendance;
import com.example.streetleague.Entity.PlayerStreak;
import com.example.streetleague.Repository.PlayerAttendanceRepository;
import com.example.streetleague.Repository.PlayerStreakRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.algorithm.AcwrCalculator;
import com.example.streetleague.algorithm.AcwrCalculator.AcwrResult;
import com.example.streetleague.algorithm.AnomalyDetector;
import com.example.streetleague.algorithm.AnomalyDetector.AnomalyResult;
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

    private final PlayerStreakRepository     streakRepo;
    private final PlayerAttendanceRepository attendanceRepo;
    private final UserRepository             userRepository;
    private final TeamRepository             teamRepository;

    // ── Configuration mode test ──────────────────────────────────────────
    @Value("${app.performance.test-mode:false}")
    private boolean testMode;

    @Value("${app.performance.window-seconds:30}")
    private long windowSeconds;

    // ── Constantes streak ────────────────────────────────────────────────
    private static final double DECAY_FACTOR    = 0.1;
    private static final int    STREAK_7_PTS    = 5;
    private static final int    STREAK_14_PTS   = 15;
    private static final int    STREAK_21_PTS   = 30;

    private static final int CHECKIN_TRAINING_PTS = 1;
    private static final int CHECKIN_MATCH_PTS    = 2;
    private static final int CHECKIN_BOTH_PTS     = 3;

    private static final long TEST_BASE_EPOCH_SECOND =
            LocalDate.of(2024, 1, 1).toEpochDay() * 86_400L;

    // ── Date effective (test / production) ──────────────────────────────
    private LocalDate getEffectiveDate() {
        if (testMode) {
            long relativeSeconds = Instant.now().getEpochSecond() - TEST_BASE_EPOCH_SECOND;
            long windowIndex     = relativeSeconds / windowSeconds;
            return LocalDate.of(2024, 1, 1).plusDays(windowIndex);
        }
        return LocalDate.now();
    }

    private LocalDate getDateBefore(LocalDate reference, int periods) {
        return reference.minusDays(periods);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CHECK-IN
    // ══════════════════════════════════════════════════════════════════════
    @Transactional
    public PlayerStatsDto checkin(Long playerId, String attendanceType) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        if (player.getRole() != Role.PLAYER) {
            throw new RuntimeException("Only PLAYER users can check in");
        }

        String    normalizedType = normalizeAttendanceType(attendanceType);
        LocalDate effectiveDate  = getEffectiveDate();

        log.info("[CHECK-IN] player={} type={} effectiveDate={} testMode={}",
                playerId, normalizedType, effectiveDate, testMode);

        Optional<PlayerAttendance> existing =
                attendanceRepo.findByPlayerIdAndAttendanceDate(playerId, effectiveDate);
        boolean isNewWindow = existing.isEmpty();

        if (existing.isPresent()) {
            PlayerAttendance a = existing.get();
            a.setIsPresent(true);
            a.setAttendanceType(normalizedType);
            attendanceRepo.saveAndFlush(a);
        } else {
            attendanceRepo.saveAndFlush(PlayerAttendance.builder()
                    .playerId(playerId)
                    .attendanceDate(effectiveDate)
                    .isPresent(true)
                    .attendanceType(normalizedType)
                    .intensity(1)
                    .build());
        }

        PlayerStreak streak = updateStreak(playerId, normalizedType, isNewWindow);
        return buildFullDto(streak, player.getFullName(), playerId);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MISE À JOUR DU STREAK
    // ══════════════════════════════════════════════════════════════════════
    private PlayerStreak updateStreak(Long playerId, String attendanceType, boolean isNewWindow) {
        LocalDate today     = getEffectiveDate();
        LocalDate thirtyAgo = getDateBefore(today, 30);

        List<PlayerAttendance> attendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, thirtyAgo, today);

        int currentStreak = computeCurrentStreak(attendances, today);

        PlayerStreak streak = streakRepo.findByPlayerId(playerId)
                .orElseGet(() -> {
                    PlayerStreak s = new PlayerStreak();
                    s.setPlayerId(playerId);
                    s.setCurrentStreak(0);
                    s.setBestStreak(0);
                    s.setTotalPoints(0);
                    s.setMomentumScore(0.0);
                    s.setFatigueRisk(0.0);
                    return s;
                });

        int oldStreak = streak.getCurrentStreak() != null ? streak.getCurrentStreak() : 0;
        streak.setCurrentStreak(currentStreak);

        int total = streak.getTotalPoints() != null ? streak.getTotalPoints() : 0;

        if (isNewWindow) {
            int dailyPts = switch (attendanceType) {
                case "BOTH"  -> CHECKIN_BOTH_PTS;
                case "MATCH" -> CHECKIN_MATCH_PTS;
                default      -> CHECKIN_TRAINING_PTS;
            };
            total += dailyPts;
        }

        if (currentStreak >= 7  && oldStreak < 7)  total += STREAK_7_PTS;
        if (currentStreak >= 14 && oldStreak < 14) total += STREAK_14_PTS;
        if (currentStreak >= 21 && oldStreak < 21) total += STREAK_21_PTS;
        streak.setTotalPoints(total);

        int best = streak.getBestStreak() != null ? streak.getBestStreak() : 0;
        if (currentStreak > best) streak.setBestStreak(currentStreak);

        streak.setCurrentBadge(computeBadge(currentStreak));
        streak.setMomentumScore(computeMomentum(attendances));

        // ── NOUVEAU : Fatigue via ACWR ────────────────────────────────────
        // Fenêtre 28 jours pour ACWR (au lieu de 30 précédemment)
        LocalDate acwrFrom = getDateBefore(today, 28);
        List<PlayerAttendance> acwrAttendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, acwrFrom, today);
        AcwrResult acwr = AcwrCalculator.compute(acwrAttendances, today);
        streak.setFatigueRisk(acwr.fatigueRisk()); // remplace l'ancienne formule

        streak.setLastAttendanceDate(today);
        streak.setLastUpdated(LocalDateTime.now());

        return streakRepo.saveAndFlush(streak);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ENDPOINTS PUBLICS EXISTANTS
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getPlayerStats(Long playerId) {
        if (!isPlayer(playerId)) return Optional.empty();
        String name = getPlayerName(playerId);
        LocalDate today     = getEffectiveDate();
        LocalDate thirtyAgo = getDateBefore(today, 30);
        List<PlayerAttendance> attendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, thirtyAgo, today);
        Optional<PlayerStreak> streakOpt = streakRepo.findById(playerId);
        if (streakOpt.isEmpty()) return Optional.of(emptyStats(playerId, name));
        PlayerStreak streak = streakOpt.get();
        streak.setCurrentStreak(computeCurrentStreak(attendances, today));
        return Optional.of(buildFullDto(streak, name, playerId));
    }

    public List<PlayerStatsDto> getGlobalLeaderboard() {
        Map<Long, String> names   = loadAllPlayerNames();
        List<PlayerStreak> streaks = streakRepo.findAllByOrderByTotalPointsDesc();
        int[] rank = {1};
        return streaks.stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId())
                        .toBuilder().rank(rank[0]++).build())
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getTeamLeaderboard(Long teamId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        Map<Long, String> names   = loadAllPlayerNames();
        List<PlayerStreak> streaks = streakRepo.getTeamLeaderboard(teamId);
        int[] rank = {1};
        return streaks.stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId())
                        .toBuilder().rank(rank[0]++).build())
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getGlobalMomentum() {
        Map<Long, String> names = loadAllPlayerNames();
        return streakRepo.findAllByOrderByCurrentStreakDesc().stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> {
                    double momentum = s.getMomentumScore()  != null ? s.getMomentumScore()  : 0.0;
                    int    curr     = s.getCurrentStreak()  != null ? s.getCurrentStreak()  : 0;
                    int    best     = s.getBestStreak()     != null ? s.getBestStreak()     : 0;
                    String trend = curr >= best * 0.9 ? "UP"
                            : curr <= best * 0.5 ? "DOWN" : "STABLE";
                    return PlayerStatsDto.builder()
                            .playerId(s.getPlayerId())
                            .playerName(names.get(s.getPlayerId()))
                            .momentumScore(momentum)
                            .trend(trend)
                            .currentStreak(curr)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getGlobalFatigueAlerts() {
        Map<Long, String> names = loadAllPlayerNames();
        return streakRepo.findAll().stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId()))
                .filter(d -> "CRITICAL".equals(d.getRiskLevel()) || "HIGH".equals(d.getRiskLevel()))
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getInjuryRiskRanking() {
        Map<Long, String> names = loadAllPlayerNames();
        return streakRepo.findAll().stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId()))
                .sorted(Comparator.comparingDouble(
                                (PlayerStatsDto d) -> d.getInjuryRiskScore() != null ? d.getInjuryRiskScore() : 0.0)
                        .reversed())
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getConsistencyRanking() {
        Map<Long, String> names = loadAllPlayerNames();
        int[] rank = {1};
        return streakRepo.findAll().stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId()))
                .sorted(Comparator.comparingDouble(
                                (PlayerStatsDto d) -> d.getConsistencyScore() != null ? d.getConsistencyScore() : 0.0)
                        .reversed())
                .map(d -> d.toBuilder().rank(rank[0]++).build())
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getPlayersByPerformanceLevel(String level) {
        Map<Long, String> names = loadAllPlayerNames();
        String normalizedLevel  = level.trim().toUpperCase();
        return streakRepo.findAll().stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId()))
                .filter(d -> normalizedLevel.equals(d.getPerformanceLevel()))
                .collect(Collectors.toList());
    }

    public List<PlayerStatsDto> getPerformancePredictions() {
        Map<Long, String> names = loadAllPlayerNames();
        return streakRepo.findAll().stream()
                .filter(s -> names.containsKey(s.getPlayerId()))
                .map(s -> buildFullDto(s, names.get(s.getPlayerId()), s.getPlayerId()))
                .sorted(Comparator.comparingDouble(
                                (PlayerStatsDto d) -> d.getStreakContinuationProbability() != null
                                        ? d.getStreakContinuationProbability() : 0.0)
                        .reversed())
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  NOUVEAUX ENDPOINTS — ACWR
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Stats ACWR d'un joueur spécifique.
     */
    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getAcwrStats(Long playerId) {
        if (!isPlayer(playerId)) return Optional.empty();
        String    name    = getPlayerName(playerId);
        LocalDate today   = getEffectiveDate();
        LocalDate from28  = getDateBefore(today, 28);

        List<PlayerAttendance> attendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, from28, today);

        AcwrResult acwr = AcwrCalculator.compute(attendances, today);

        return Optional.of(PlayerStatsDto.builder()
                .playerId(playerId)
                .playerName(name)
                .acwr(round2(acwr.acwr()))
                .acuteLoad(round2(acwr.acuteLoad()))
                .chronicLoad(round2(acwr.chronicLoad()))
                .acwrZone(acwr.zone().name())
                .acwrRecommendation(acwr.recommendation())
                .fatigueRisk(round3(acwr.fatigueRisk()))
                .riskLevel(acwr.riskLevel())
                .workloadFactor(round3(acwr.normalizedAcuteLoad()))
                .build());
    }

    /**
     * Classement global trié par ACWR décroissant (les plus à risque en premier).
     */
    public List<PlayerStatsDto> getAcwrRanking() {
        Map<Long, String> names   = loadAllPlayerNames();
        LocalDate         today   = getEffectiveDate();
        LocalDate         from28  = getDateBefore(today, 28);

        return names.entrySet().stream()
                .map(e -> {
                    Long   id   = e.getKey();
                    String name = e.getValue();
                    List<PlayerAttendance> att = attendanceRepo
                            .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                    id, from28, today);
                    AcwrResult acwr = AcwrCalculator.compute(att, today);
                    return PlayerStatsDto.builder()
                            .playerId(id)
                            .playerName(name)
                            .acwr(round2(acwr.acwr()))
                            .acuteLoad(round2(acwr.acuteLoad()))
                            .chronicLoad(round2(acwr.chronicLoad()))
                            .acwrZone(acwr.zone().name())
                            .acwrRecommendation(acwr.recommendation())
                            .fatigueRisk(round3(acwr.fatigueRisk()))
                            .riskLevel(acwr.riskLevel())
                            .workloadFactor(round3(acwr.normalizedAcuteLoad()))
                            .build();
                })
                .sorted(Comparator.comparingDouble(
                                (PlayerStatsDto d) -> d.getAcwr() != null ? d.getAcwr() : 0.0)
                        .reversed())
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  NOUVEAUX ENDPOINTS — ANOMALIES
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Analyse d'anomalie pour un joueur spécifique.
     */
    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getAnomalyStats(Long playerId) {
        if (!isPlayer(playerId)) return Optional.empty();
        String    name   = getPlayerName(playerId);
        LocalDate today  = getEffectiveDate();
        LocalDate from28 = getDateBefore(today, 28);

        List<PlayerAttendance> attendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, from28, today);

        AnomalyResult anomaly = AnomalyDetector.analyze(attendances, today);

        return Optional.of(PlayerStatsDto.builder()
                .playerId(playerId)
                .playerName(name)
                .anomalyType(anomaly.type().name())
                .anomalySeverity(anomaly.severity().name())
                .zScore(round3(anomaly.zScore()))
                .ewmaScore(round3(anomaly.ewma()))
                .ewmaDrop(round3(anomaly.ewmaDrop()))
                .anomalyMessage(anomaly.message())
                .coachAlertSent(anomaly.requiresCoachAlert())
                .build());
    }

    /**
     * Liste de tous les joueurs avec anomalies (MEDIUM, HIGH, CRITICAL).
     * Utilisé par le scheduler et le dashboard coach.
     */
    public List<PlayerStatsDto> getPlayersWithAnomalies() {
        Map<Long, String> names  = loadAllPlayerNames();
        LocalDate         today  = getEffectiveDate();
        LocalDate         from28 = getDateBefore(today, 28);

        return names.entrySet().stream()
                .map(e -> {
                    Long   id   = e.getKey();
                    String name = e.getValue();
                    List<PlayerAttendance> att = attendanceRepo
                            .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                    id, from28, today);
                    AnomalyResult anomaly = AnomalyDetector.analyze(att, today);
                    if (!anomaly.isAnomaly()) return null;

                    // Enrichir avec les stats de streak pour le contexte
                    Optional<PlayerStreak> streakOpt = streakRepo.findByPlayerId(id);
                    PlayerStatsDto.PlayerStatsDtoBuilder builder = PlayerStatsDto.builder()
                            .playerId(id)
                            .playerName(name)
                            .anomalyType(anomaly.type().name())
                            .anomalySeverity(anomaly.severity().name())
                            .zScore(round3(anomaly.zScore()))
                            .ewmaScore(round3(anomaly.ewma()))
                            .ewmaDrop(round3(anomaly.ewmaDrop()))
                            .anomalyMessage(anomaly.message())
                            .coachAlertSent(anomaly.requiresCoachAlert());

                    streakOpt.ifPresent(s -> builder
                            .currentStreak(s.getCurrentStreak())
                            .totalPoints(s.getTotalPoints())
                            .fatigueRisk(s.getFatigueRisk()));

                    return builder.build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(d -> {
                    String sev = d.getAnomalySeverity();
                    if ("CRITICAL".equals(sev)) return 0;
                    if ("HIGH".equals(sev))     return 1;
                    return 2;
                }))
                .collect(Collectors.toList());
    }

    /**
     * Analyse ACWR + Anomalie complète pour un joueur (combiné).
     * Endpoint principal utilisé par le dashboard coach.
     */
    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getFullAnalysis(Long playerId) {
        if (!isPlayer(playerId)) return Optional.empty();

        LocalDate today  = getEffectiveDate();
        LocalDate from30 = getDateBefore(today, 30);
        LocalDate from28 = getDateBefore(today, 28);

        List<PlayerAttendance> att30 = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, from30, today);
        List<PlayerAttendance> att28 = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, from28, today);

        Optional<PlayerStreak> streakOpt = streakRepo.findByPlayerId(playerId);
        if (streakOpt.isEmpty()) return Optional.empty();

        PlayerStreak streak = streakOpt.get();
        String name = getPlayerName(playerId);

        // Calculs ACWR + Anomalie
        AcwrResult    acwr    = AcwrCalculator.compute(att28, today);
        AnomalyResult anomaly = AnomalyDetector.analyze(att28, today);

        // DTO complet — enrichit le buildFullDto avec les nouveaux champs
        PlayerStatsDto base = buildFullDto(streak, name, playerId);
        return Optional.of(base.toBuilder()
                // ACWR
                .acwr(round2(acwr.acwr()))
                .acuteLoad(round2(acwr.acuteLoad()))
                .chronicLoad(round2(acwr.chronicLoad()))
                .acwrZone(acwr.zone().name())
                .acwrRecommendation(acwr.recommendation())
                // Mise à jour fatigueRisk avec la valeur ACWR (plus précise)
                .fatigueRisk(round3(acwr.fatigueRisk()))
                .riskLevel(acwr.riskLevel())
                .workloadFactor(round3(acwr.normalizedAcuteLoad()))
                // Anomalie
                .anomalyType(anomaly.type().name())
                .anomalySeverity(anomaly.severity().name())
                .zScore(round3(anomaly.zScore()))
                .ewmaScore(round3(anomaly.ewma()))
                .ewmaDrop(round3(anomaly.ewmaDrop()))
                .anomalyMessage(anomaly.message())
                .coachAlertSent(anomaly.requiresCoachAlert())
                .build());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MÉTRIQUES AVANCÉES EXISTANTES (inchangées)
    // ══════════════════════════════════════════════════════════════════════

    private double computeConsistencyScore(List<PlayerAttendance> attendances, LocalDate today) {
        if (attendances.isEmpty()) return 0.0;
        int activeDays = (int) attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent())).count();
        double baseRate   = (double) activeDays / 30.0;
        int    maxGap     = computeMaxGap(attendances, today);
        double gapPenalty = Math.min(1.0, maxGap / 10.0);
        double score      = (baseRate * 100.0) * (1.0 - gapPenalty * 0.5);
        return Math.round(Math.min(100.0, score) * 10.0) / 10.0;
    }

    private int computeMaxGap(List<PlayerAttendance> attendances, LocalDate today) {
        Set<LocalDate> presentDays = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .map(PlayerAttendance::getAttendanceDate)
                .collect(Collectors.toSet());
        int maxGap = 0, currentGap = 0;
        for (int i = 0; i < 30; i++) {
            LocalDate day = today.minusDays(i);
            if (presentDays.contains(day)) { maxGap = Math.max(maxGap, currentGap); currentGap = 0; }
            else currentGap++;
        }
        return Math.max(maxGap, currentGap);
    }

    private String computePerformanceLevel(int totalPoints, int currentStreak, double consistencyScore) {
        if (totalPoints >= 100 && currentStreak >= 21 && consistencyScore >= 70) return "ELITE";
        if (totalPoints >= 50  && currentStreak >= 10 && consistencyScore >= 50) return "PRO";
        if (totalPoints >= 20  && currentStreak >= 5  && consistencyScore >= 30) return "INTERMEDIATE";
        return "BEGINNER";
    }

    private double computeWeeklyAverage(List<PlayerAttendance> attendances, LocalDate today) {
        long activeLast7 = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .filter(a -> !a.getAttendanceDate().isBefore(today.minusDays(6))).count();
        return Math.round((activeLast7 / 7.0) * 100.0) / 100.0;
    }

    private double computeRecoveryScore(List<PlayerAttendance> attendances, LocalDate today) {
        Set<LocalDate> presentDays = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .map(PlayerAttendance::getAttendanceDate).collect(Collectors.toSet());
        int restDays = 0;
        for (int i = 0; i < 7; i++) if (!presentDays.contains(today.minusDays(i))) restDays++;
        return restDays == 0 ? 10.0 : restDays == 1 ? 50.0 : restDays <= 3 ? 100.0
                : restDays <= 5 ? 70.0 : 40.0;
    }

    private double computeStreakContinuationProbability(int currentStreak, double consistencyScore,
                                                        double fatigueRisk, double momentumScore) {
        if (currentStreak == 0) return 0.0;
        double prob = (consistencyScore / 100.0) * (1.0 - fatigueRisk) * (momentumScore / 100.0);
        return Math.round(Math.min(100.0, prob * 100.0) * 10.0) / 10.0;
    }

    private int computePredictedStreak(int currentStreak, double continuationProbability) {
        if (continuationProbability >= 70.0) return currentStreak + 7;
        if (continuationProbability >= 40.0) return currentStreak + 3;
        return Math.max(0, currentStreak - 2);
    }

    private double computeInjuryRiskScore(double fatigueRisk, double workloadFactor,
                                          double consistencyScore, double recoveryScore) {
        double recoveryRisk = (100.0 - recoveryScore) / 100.0;
        double score = (fatigueRisk * 40.0) + (workloadFactor * 35.0) + (recoveryRisk * 25.0);
        return Math.round(Math.min(100.0, score) * 10.0) / 10.0;
    }

    private List<Integer> computeWeeklyHistory(List<PlayerAttendance> attendances, LocalDate today) {
        Set<LocalDate> presentDays = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .map(PlayerAttendance::getAttendanceDate).collect(Collectors.toSet());
        List<Integer> history = new ArrayList<>();
        for (int i = 6; i >= 0; i--) history.add(presentDays.contains(today.minusDays(i)) ? 1 : 0);
        return history;
    }

    private int computePointsInPeriod(List<PlayerAttendance> attendances,
                                      LocalDate from, LocalDate to) {
        return attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .filter(a -> !a.getAttendanceDate().isBefore(from)
                        && !a.getAttendanceDate().isAfter(to))
                .mapToInt(a -> "BOTH".equals(a.getAttendanceType()) ? 3
                        : "MATCH".equals(a.getAttendanceType()) ? 2 : 1).sum();
    }

    // ── Calculs de base ───────────────────────────────────────────────────

    private int computeCurrentStreak(List<PlayerAttendance> attendances, LocalDate today) {
        int streak = 0;
        for (int i = 0; i < 30; i++) {
            LocalDate window = today.minusDays(i);
            boolean present  = attendances.stream()
                    .anyMatch(a -> a.getAttendanceDate().equals(window)
                            && Boolean.TRUE.equals(a.getIsPresent()));
            if (present) streak++;
            else if (i > 0) break;
        }
        return streak;
    }

    private double computeMomentum(List<PlayerAttendance> attendances) {
        LocalDate today = LocalDate.now();
        double weighted = attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPresent()))
                .mapToDouble(a -> {
                    long daysAgo = ChronoUnit.DAYS.between(a.getAttendanceDate(), today);
                    return Math.exp(-DECAY_FACTOR * daysAgo);
                }).sum();
        return Math.round(Math.min(100.0, (weighted / 30.0) * 100.0) * 10.0) / 10.0;
    }

    private String computeBadge(int currentStreak) {
        if (currentStreak >= 30) return "LEGEND";
        if (currentStreak >= 21) return "IRON WILL";
        if (currentStreak >= 14) return "FORTNIGHT";
        if (currentStreak >= 7)  return "WEEK STREAK";
        return null;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CONSTRUCTION DU DTO COMPLET (enrichi avec ACWR + Anomalie)
    // ══════════════════════════════════════════════════════════════════════

    private PlayerStatsDto buildFullDto(PlayerStreak s, String name, Long playerId) {
        LocalDate today     = getEffectiveDate();
        LocalDate thirtyAgo = getDateBefore(today, 30);
        LocalDate from28    = getDateBefore(today, 28);

        List<PlayerAttendance> att30 = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, thirtyAgo, today);
        List<PlayerAttendance> att28 = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, from28, today);

        int    currentStreak = s.getCurrentStreak() != null ? s.getCurrentStreak() : 0;
        int    bestStreak    = s.getBestStreak()     != null ? s.getBestStreak()    : 0;
        int    totalPoints   = s.getTotalPoints()    != null ? s.getTotalPoints()   : 0;
        double momentum      = s.getMomentumScore()  != null ? s.getMomentumScore() : 0.0;

        // ── ACWR (remplace l'ancienne formule de fatigue) ─────────────────
        AcwrResult acwr = AcwrCalculator.compute(att28, today);
        double fatigueRisk = acwr.fatigueRisk();

        // ── Anomalie ─────────────────────────────────────────────────────
        AnomalyResult anomaly = AnomalyDetector.analyze(att28, today);

        // ── Métriques existantes ──────────────────────────────────────────
        String riskLevel = acwr.riskLevel();
        String status    = currentStreak >= 21 ? "OPTIMAL" : currentStreak >= 10 ? "ACTIF" : "FAIBLE";

        String rec; int restDays;
        if      (fatigueRisk >= 0.85) { rec = "Repos obligatoire 3j"; restDays = 3; }
        else if (fatigueRisk >= 0.70) { rec = "Repos recommandé 2j";  restDays = 2; }
        else if (fatigueRisk >= 0.50) { rec = "Réduire intensité";    restDays = 1; }
        else                          { rec = "Normal - continuer";   restDays = 0; }

        int    activeDays30     = (int) att30.stream().filter(a -> Boolean.TRUE.equals(a.getIsPresent())).count();
        double attendanceRate   = Math.round((activeDays30 / 30.0) * 1000.0) / 10.0;
        double consistencyScore = computeConsistencyScore(att30, today);
        double weeklyAverage    = computeWeeklyAverage(att30, today);
        double recoveryScore    = computeRecoveryScore(att30, today);
        double workloadFactor   = acwr.normalizedAcuteLoad(); // ACWR remplace l'ancienne formule
        String performanceLevel = computePerformanceLevel(totalPoints, currentStreak, consistencyScore);
        double continuationProb = computeStreakContinuationProbability(currentStreak, consistencyScore, fatigueRisk, momentum);
        int    predictedStreak  = computePredictedStreak(currentStreak, continuationProb);
        double injuryRiskScore  = computeInjuryRiskScore(fatigueRisk, workloadFactor, consistencyScore, recoveryScore);
        String injuryRiskLevel  = injuryRiskScore >= 75 ? "CRITICAL" : injuryRiskScore >= 50 ? "HIGH"
                : injuryRiskScore >= 25 ? "MODERATE" : "LOW";
        List<Integer> weeklyHistory  = computeWeeklyHistory(att30, today);
        int    pointsThisWeek  = computePointsInPeriod(att30, today.minusDays(6), today);
        int    pointsThisMonth = computePointsInPeriod(att30, thirtyAgo, today);

        return PlayerStatsDto.builder()
                // Base
                .playerId(playerId).playerName(name)
                .currentStreak(currentStreak).bestStreak(bestStreak)
                .totalPoints(totalPoints).badge(s.getCurrentBadge())
                .status(status).momentumScore(momentum)
                // Fatigue — désormais basée sur ACWR
                .fatigueRisk(round3(fatigueRisk)).riskLevel(riskLevel)
                .recommendation(rec).recommendedRestDays(restDays)
                // Métriques existantes
                .consistencyScore(consistencyScore).performanceLevel(performanceLevel)
                .weeklyAverage(weeklyAverage).activeDaysLast30(activeDays30)
                .attendanceRate(attendanceRate).recoveryScore(recoveryScore)
                .predictedStreakIn7Days(predictedStreak)
                .streakContinuationProbability(continuationProb)
                .injuryRiskScore(injuryRiskScore).injuryRiskLevel(injuryRiskLevel)
                .workloadFactor(round3(workloadFactor))
                .weeklyHistory(weeklyHistory)
                .pointsThisWeek(pointsThisWeek).pointsThisMonth(pointsThisMonth)
                // NOUVEAU — ACWR
                .acwr(round2(acwr.acwr()))
                .acuteLoad(round2(acwr.acuteLoad()))
                .chronicLoad(round2(acwr.chronicLoad()))
                .acwrZone(acwr.zone().name())
                .acwrRecommendation(acwr.recommendation())
                // NOUVEAU — Anomalie
                .anomalyType(anomaly.type().name())
                .anomalySeverity(anomaly.severity().name())
                .zScore(round3(anomaly.zScore()))
                .ewmaScore(round3(anomaly.ewma()))
                .ewmaDrop(round3(anomaly.ewmaDrop()))
                .anomalyMessage(anomaly.message())
                .coachAlertSent(anomaly.requiresCoachAlert())
                .build();
    }

    private PlayerStatsDto emptyStats(Long playerId, String name) {
        return PlayerStatsDto.builder()
                .playerId(playerId).playerName(name)
                .currentStreak(0).bestStreak(0).totalPoints(0)
                .fatigueRisk(0.0).riskLevel("LOW").momentumScore(0.0)
                .status("FAIBLE").consistencyScore(0.0).performanceLevel("BEGINNER")
                .weeklyAverage(0.0).activeDaysLast30(0).attendanceRate(0.0)
                .recoveryScore(100.0).predictedStreakIn7Days(0)
                .streakContinuationProbability(0.0).injuryRiskScore(0.0)
                .injuryRiskLevel("LOW").workloadFactor(0.0)
                .weeklyHistory(List.of(0,0,0,0,0,0,0))
                .pointsThisWeek(0).pointsThisMonth(0)
                // ACWR par défaut (zone optimale pour un joueur inactif)
                .acwr(1.0).acuteLoad(0.0).chronicLoad(0.0)
                .acwrZone("UNDERLOAD").acwrRecommendation("Aucune donnée disponible.")
                // Anomalie par défaut
                .anomalyType("NORMAL").anomalySeverity("NONE")
                .zScore(0.0).ewmaScore(0.0).ewmaDrop(0.0)
                .anomalyMessage("Données insuffisantes.").coachAlertSent(false)
                .build();
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    private Map<Long, String> loadAllPlayerNames() {
        return userRepository.findAllByRole(Role.PLAYER).stream()
                .collect(Collectors.toMap(User::getIdUser, User::getFullName));
    }
    private String getPlayerName(Long playerId) {
        return userRepository.findById(playerId).map(User::getFullName).orElse(null);
    }
    private boolean isPlayer(Long playerId) {
        return userRepository.findById(playerId)
                .map(u -> u.getRole() == Role.PLAYER).orElse(false);
    }
    private String normalizeAttendanceType(String type) {
        if (type == null || type.isBlank()) return "TRAINING";
        return switch (type.trim().toUpperCase()) {
            case "TRAINING", "MATCH", "BOTH" -> type.trim().toUpperCase();
            default -> throw new RuntimeException("Attendance type must be TRAINING, MATCH or BOTH, got: " + type);
        };
    }
    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private double round3(double v) { return Math.round(v * 1000.0) / 1000.0; }

    // ── Rankings avancés ──────────────────────────────────────────────────

    public List<PlayerStatsDto> getInjuryRiskRankingAcwr() {
        return getAcwrRanking(); // trié par ACWR décroissant = plus à risque en premier
    }
}