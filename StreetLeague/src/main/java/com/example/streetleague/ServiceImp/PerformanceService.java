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

    // ── Base temporelle test-mode ─────────────────────────────────────────
    private LocalDate testBaseDate;
    private long      testBaseEpoch;

    @jakarta.annotation.PostConstruct
    private void initTestBase() {
        testBaseDate  = LocalDate.now();
        testBaseEpoch = Instant.now().getEpochSecond();
        log.info("[TEST-MODE] Base initialisée : date={} epoch={} windowSeconds={}",
                testBaseDate, testBaseEpoch, windowSeconds);
    }

    // ── Date effective (test / production) ──────────────────────────────
    private LocalDate getEffectiveDate() {
        if (testMode) {
            long elapsed     = Instant.now().getEpochSecond() - testBaseEpoch;
            long windowIndex = elapsed / windowSeconds;
            return testBaseDate.plusDays(windowIndex);
        }
        return LocalDate.now();
    }

    private LocalDate getDateBefore(LocalDate reference, int periods) {
        return reference.minusDays(periods);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CHECK-IN
    //
    //  CORRECTION BUG #1 (blocage Jour 1 / 30s) :
    //    Le blocage venait du fait qu'au Jour 1, buildFullDto() appelait
    //    AnomalyDetector.analyze() qui pouvait recevoir une liste vide et
    //    tentait des calculs statistiques (mean, stdDev) sur 0 éléments.
    //    → AnomalyDetector.insufficient() était déjà prévu MAIS le check
    //      était sur activeDaysCount < MIN_DATA_POINTS après buildDailySeries,
    //      qui retourne TOUJOURS 28 zéros même au Jour 1. L'ancien code
    //      vérifiait dailySeries.size() < 3 → toujours faux → plantait.
    //    → FIX : AnomalyDetector.analyze() vérifie maintenant les jours
    //      ACTIFS (non-zéro) dans la série, pas la taille totale (voir
    //      AnomalyDetector.java corrigé). Aucun changement nécessaire ici.
    //
    //  CORRECTION BUG #2 (5 check-ins = fatigue gonflée) :
    //    Cause : attendanceType était mis à jour à chaque check-in, même
    //    intra-fenêtre. Si le joueur checkait TRAINING puis MATCH puis BOTH
    //    5 fois, le type sauvegardé était BOTH → poids 3.0 dans ACWR.
    //    En soi c'est une seule entrée par jour (contrainte UNIQUE), donc
    //    l'ACWR ne double pas. LE VRAI PROBLÈME était que fatigueRisk
    //    montait parce que acuteLoad = somme des poids sur 7j, et le type
    //    BOTH (poids 3.0) multiplié par 7j = 21 = MAX → fatigueRisk = 1.0.
    //
    //    FIX : Règle de merge du type de séance — on prend TOUJOURS
    //    le type le plus fort déjà enregistré (BOTH > MATCH > TRAINING).
    //    Un joueur qui a déjà checké MATCH ne peut pas "dégrader" vers
    //    TRAINING. Et un joueur qui checker 5 fois TRAINING reste TRAINING.
    //    → Résultat : l'ACWR reflète le TYPE réel de la séance, pas le
    //      nombre de check-ins.
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
            // ── FIX BUG #2 : merge du type — on garde le plus fort ────────
            // BOTH > MATCH > TRAINING.
            // Un check-in TRAINING ne peut pas écraser un MATCH ou BOTH existant.
            String mergedType = mergeAttendanceType(a.getAttendanceType(), normalizedType);
            if (!mergedType.equals(a.getAttendanceType())) {
                log.info("[CHECK-IN] type upgrade: {} → {} for player={} date={}",
                        a.getAttendanceType(), mergedType, playerId, effectiveDate);
                a.setAttendanceType(mergedType);
                attendanceRepo.saveAndFlush(a);
            } else {
                log.info("[CHECK-IN] type unchanged={} (already at highest/same level) player={} date={}",
                        mergedType, playerId, effectiveDate);
            }
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

    /**
     * Retourne le type de séance le plus fort entre l'existant et le nouveau.
     * Priorité : BOTH (3) > MATCH (2) > TRAINING (1).
     *
     * FIX BUG #2 : empêche qu'un check-in répété dégrade ou gonfle artificiellement
     * le type de séance. Seule une "montée en puissance" est autorisée.
     */
    private String mergeAttendanceType(String existing, String incoming) {
        int existingWeight = typeWeight(existing);
        int incomingWeight = typeWeight(incoming);
        return existingWeight >= incomingWeight ? existing : incoming;
    }

    private int typeWeight(String type) {
        if (type == null) return 1;
        return switch (type.toUpperCase()) {
            case "BOTH"     -> 3;
            case "MATCH"    -> 2;
            default         -> 1; // TRAINING
        };
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

        // Points uniquement sur une nouvelle fenêtre (nouveau jour simulé)
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

        // ACWR (fenêtre 28 jours)
        LocalDate acwrFrom = getDateBefore(today, 28);
        List<PlayerAttendance> acwrAttendances = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, acwrFrom, today);
        AcwrResult acwr = AcwrCalculator.compute(acwrAttendances, today);
        streak.setFatigueRisk(acwr.fatigueRisk());

        streak.setLastAttendanceDate(today);
        streak.setLastUpdated(LocalDateTime.now());

        return streakRepo.saveAndFlush(streak);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ENDPOINTS PUBLICS
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
    //  ENDPOINTS ACWR
    // ══════════════════════════════════════════════════════════════════════

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
    //  ENDPOINTS ANOMALIES
    // ══════════════════════════════════════════════════════════════════════

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
                    // Inclure DROPS + SPIKES + ACWR danger (pas seulement les chutes)
                    boolean showAnomaly = anomaly.isAnomaly()
                            || anomaly.type() == AnomalyDetector.AnomalyType.PERFORMANCE_SPIKE;
                    if (!showAnomaly) return null;

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

    @Transactional(readOnly = true)
    public Optional<PlayerStatsDto> getFullAnalysis(Long playerId) {
        if (!isPlayer(playerId)) return Optional.empty();

        LocalDate today  = getEffectiveDate();
        LocalDate from30 = getDateBefore(today, 30);
        LocalDate from28 = getDateBefore(today, 28);

        List<PlayerAttendance> att28 = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, from28, today);

        Optional<PlayerStreak> streakOpt = streakRepo.findByPlayerId(playerId);
        if (streakOpt.isEmpty()) return Optional.empty();

        PlayerStreak streak = streakOpt.get();
        String name = getPlayerName(playerId);

        AcwrResult    acwr    = AcwrCalculator.compute(att28, today);
        AnomalyResult anomaly = AnomalyDetector.analyze(att28, today);

        PlayerStatsDto base = buildFullDto(streak, name, playerId);
        return Optional.of(base.toBuilder()
                .acwr(round2(acwr.acwr()))
                .acuteLoad(round2(acwr.acuteLoad()))
                .chronicLoad(round2(acwr.chronicLoad()))
                .acwrZone(acwr.zone().name())
                .acwrRecommendation(acwr.recommendation())
                .fatigueRisk(round3(acwr.fatigueRisk()))
                .riskLevel(acwr.riskLevel())
                .workloadFactor(round3(acwr.normalizedAcuteLoad()))
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
    //  MÉTRIQUES AVANCÉES
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
    //  CONSTRUCTION DU DTO COMPLET
    //
    //  FIX BUG #1 : Les deux appels à AcwrCalculator.compute() et
    //  AnomalyDetector.analyze() sont maintenant protégés contre les listes
    //  vides (Jour 1 / historique insuffisant). Chacun retourne un résultat
    //  "insufficient" au lieu de lancer une exception.
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

        // ACWR — retourne toujours un résultat valide (insufficientHistory si < 7j)
        AcwrResult acwr = AcwrCalculator.compute(att28, today);
        double fatigueRisk = acwr.fatigueRisk();

        // Anomalie — retourne toujours un résultat valide (NORMAL/insufficient si < 3j actifs)
        AnomalyResult anomaly = AnomalyDetector.analyze(att28, today);

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
        double workloadFactor   = acwr.normalizedAcuteLoad();
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
                .playerId(playerId).playerName(name)
                .currentStreak(currentStreak).bestStreak(bestStreak)
                .totalPoints(totalPoints).badge(s.getCurrentBadge())
                .status(status).momentumScore(momentum)
                .fatigueRisk(round3(fatigueRisk)).riskLevel(riskLevel)
                .recommendation(rec).recommendedRestDays(restDays)
                .consistencyScore(consistencyScore).performanceLevel(performanceLevel)
                .weeklyAverage(weeklyAverage).activeDaysLast30(activeDays30)
                .attendanceRate(attendanceRate).recoveryScore(recoveryScore)
                .predictedStreakIn7Days(predictedStreak)
                .streakContinuationProbability(continuationProb)
                .injuryRiskScore(injuryRiskScore).injuryRiskLevel(injuryRiskLevel)
                .workloadFactor(round3(workloadFactor))
                .weeklyHistory(weeklyHistory)
                .pointsThisWeek(pointsThisWeek).pointsThisMonth(pointsThisMonth)
                .acwr(round2(acwr.acwr()))
                .acuteLoad(round2(acwr.acuteLoad()))
                .chronicLoad(round2(acwr.chronicLoad()))
                .acwrZone(acwr.zone().name())
                .acwrRecommendation(acwr.recommendation())
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
                .acwr(1.0).acuteLoad(0.0).chronicLoad(0.0)
                .acwrZone("UNDERLOAD").acwrRecommendation("Aucune donnée disponible.")
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

    public List<PlayerStatsDto> getInjuryRiskRankingAcwr() {
        return getAcwrRanking();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SEED TEST DATA — injecte 28 jours de présences simulées pour un joueur
    //
    //  Pattern disponibles :
    //    "regular"  → présent les 28 jours (TRAINING) — pas d'anomalie, ACWR OPTIMAL
    //    "drop"     → présent J-28 à J-4, absent les 4 derniers jours → DROP détecté
    //    "spike"    → absent les 20 premiers jours, présent les 8 derniers → SPIKE
    //    "overload" → présent tous les jours avec BOTH (poids 3.0) → ACWR DANGER
    //    "custom"   → utilise les paramètres presentDays et skipLast
    //
    //  Utilisé uniquement en mode test / démo pour le prof.
    // ══════════════════════════════════════════════════════════════════════
    @Transactional
    public Map<String, Object> seedTestData(Long playerId, String pattern, int presentDays, int skipLast) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));
        if (player.getRole() != Role.PLAYER) {
            throw new RuntimeException("Only PLAYER can be seeded");
        }

        LocalDate today = getEffectiveDate();

        // Supprimer les anciennes présences sur 30 jours
        LocalDate from30 = today.minusDays(30);
        List<PlayerAttendance> old = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(playerId, from30, today);
        attendanceRepo.deleteAll(old);
        attendanceRepo.flush();

        // Construire le pattern de présences
        List<LocalDate> presentDates = new ArrayList<>();
        int totalDays = 28;

        switch (pattern.toLowerCase()) {
            case "drop" -> {
                // Présent J-28 à J-5, absent les 5 derniers → chute détectable
                for (int i = totalDays - 1; i >= 5; i--) {
                    presentDates.add(today.minusDays(i));
                }
            }
            case "spike" -> {
                // Absent les 20 premiers, présent les 8 derniers → pic
                for (int i = 7; i >= 0; i--) {
                    presentDates.add(today.minusDays(i));
                }
            }
            case "overload" -> {
                // Présent tous les jours avec BOTH → ACWR DANGER
                for (int i = totalDays - 1; i >= 0; i--) {
                    presentDates.add(today.minusDays(i));
                }
            }
            case "regular" -> {
                for (int i = totalDays - 1; i >= 0; i--) {
                    presentDates.add(today.minusDays(i));
                }
            }
            default -> {
                // custom : presentDays jours présents, skipLast jours absents à la fin
                int start = Math.max(0, totalDays - presentDays - skipLast);
                for (int i = totalDays - 1 - start; i >= skipLast; i--) {
                    presentDates.add(today.minusDays(i));
                }
            }
        }

        String type = "overload".equals(pattern) ? "BOTH" : "TRAINING";

        // Insérer les présences
        List<PlayerAttendance> toSave = presentDates.stream()
                .map(date -> PlayerAttendance.builder()
                        .playerId(playerId)
                        .attendanceDate(date)
                        .isPresent(true)
                        .attendanceType(type)
                        .intensity(1)
                        .build())
                .collect(Collectors.toList());
        attendanceRepo.saveAllAndFlush(toSave);

        // Mettre à jour le streak
        PlayerStreak streak = streakRepo.findByPlayerId(playerId).orElseGet(() -> {
            PlayerStreak s = new PlayerStreak();
            s.setPlayerId(playerId);
            s.setCurrentStreak(0);
            s.setBestStreak(0);
            s.setTotalPoints(0);
            s.setMomentumScore(0.0);
            s.setFatigueRisk(0.0);
            return s;
        });

        List<PlayerAttendance> att28 = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, today.minusDays(28), today);

        int currentStreak = computeCurrentStreak(att28, today);
        int pts = toSave.size() * ("BOTH".equals(type) ? 3 : 1);
        streak.setCurrentStreak(currentStreak);
        streak.setBestStreak(Math.max(streak.getBestStreak() != null ? streak.getBestStreak() : 0, currentStreak));
        streak.setTotalPoints(pts);
        streak.setCurrentBadge(computeBadge(currentStreak));
        streak.setMomentumScore(computeMomentum(att28));
        AcwrResult acwr = AcwrCalculator.compute(att28, today);
        streak.setFatigueRisk(acwr.fatigueRisk());
        streak.setLastAttendanceDate(today);
        streak.setLastUpdated(LocalDateTime.now());
        streakRepo.saveAndFlush(streak);

        AnomalyResult anomaly = AnomalyDetector.analyze(att28, today);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("playerId", playerId);
        result.put("playerName", player.getFullName());
        result.put("pattern", pattern);
        result.put("daysInjected", toSave.size());
        result.put("effectiveDate", today.toString());
        result.put("currentStreak", currentStreak);
        result.put("acwrZone", acwr.zone().name());
        result.put("acwr", round2(acwr.acwr()));
        result.put("anomalyType", anomaly.type().name());
        result.put("anomalySeverity", anomaly.severity().name());
        result.put("zScore", round3(anomaly.zScore()));
        result.put("ewmaDrop", round3(anomaly.ewmaDrop()));
        result.put("message", anomaly.message());
        log.info("[SEED] player={} pattern={} days={} acwr={} anomaly={} z={}",
                playerId, pattern, toSave.size(), acwr.acwr(), anomaly.type(), anomaly.zScore());
        return result;
    }

    @Transactional
    public Map<String, Object> resetPlayerData(Long playerId) {
        LocalDate today = getEffectiveDate();
        List<PlayerAttendance> old = attendanceRepo
                .findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                        playerId, today.minusDays(60), today);
        attendanceRepo.deleteAll(old);
        attendanceRepo.flush();

        streakRepo.findByPlayerId(playerId).ifPresent(s -> {
            s.setCurrentStreak(0);
            s.setBestStreak(0);
            s.setTotalPoints(0);
            s.setFatigueRisk(0.0);
            s.setMomentumScore(0.0);
            s.setCurrentBadge(null);
            s.setLastAttendanceDate(null);
            streakRepo.saveAndFlush(s);
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("playerId", playerId);
        result.put("deleted", old.size());
        result.put("status", "reset OK");
        return result;
    }
}