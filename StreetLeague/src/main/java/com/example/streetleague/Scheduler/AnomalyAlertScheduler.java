package com.example.streetleague.Scheduler;

import com.example.streetleague.Entity.PlayerAttendance;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.PlayerAttendanceRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.algorithm.AnomalyDetector;
import com.example.streetleague.algorithm.AnomalyDetector.AnomalyResult;
import com.example.streetleague.algorithm.AnomalyDetector.Severity;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AnomalyAlertScheduler — Détection automatique d'anomalies de performance
 *
 * FIXES appliqués :
 *   [P1-4] Coach ciblé : on notifie uniquement le coach de l'équipe du joueur
 *   [P1-5] Anti-spam   : mémoire lastAlertedAt + lastSeverity par joueur
 *                        — pas de re-notification si même anomalie dans les 24h
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyAlertScheduler {

    private final UserRepository             userRepository;
    private final PlayerAttendanceRepository attendanceRepo;
    private final TeamRepository             teamRepository;
    private final InotificationService       notificationService;

    private static final int ANALYSIS_WINDOW_DAYS = 28;

    // ── Anti-spam : mémoire en RAM (suffit pour éviter le spam intra-journalier) ─
    // clé = playerId, valeur = [lastAlertedAt, lastSeverity]
    private final Map<Long, AlertRecord> alertMemory = new ConcurrentHashMap<>();

    private record AlertRecord(LocalDateTime sentAt, Severity severity) {}

    // Délai minimum entre deux alertes pour le MÊME joueur (24h pour daily, 6h pour critique)
    private static final int DAILY_COOLDOWN_HOURS    = 24;
    private static final int CRITICAL_COOLDOWN_HOURS = 6;

    // ════════════════════════════════════════════════════════════════════════
    //  Scan quotidien à 8h — HIGH + CRITICAL
    // ════════════════════════════════════════════════════════════════════════
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void detectAndAlertAnomalies() {
        log.info("🔍 [AnomalyAlertScheduler] Scan quotidien démarré...");

        LocalDate today  = LocalDate.now();
        LocalDate from28 = today.minusDays(ANALYSIS_WINDOW_DAYS - 1);
        int alertsSent   = 0;

        List<User> players = userRepository.findAllByRole(Role.PLAYER);
        log.info("→ {} joueur(s) à analyser", players.size());

        for (User player : players) {
            try {
                List<PlayerAttendance> attendances =
                        attendanceRepo.findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                player.getIdUser(), from28, today);

                AnomalyResult result = AnomalyDetector.analyze(attendances, today);

                if (!result.requiresCoachAlert()) continue;

                // ── Anti-spam : skip si même sévérité déjà envoyée dans les 24h ─
                if (isRecentlyAlerted(player.getIdUser(), result.severity(), DAILY_COOLDOWN_HOURS)) {
                    log.info("⏭ Skip spam {} — déjà alerté récemment", player.getFullName());
                    continue;
                }

                // ── [P1-4] Cibler uniquement le coach de l'équipe du joueur ────
                Optional<User> coach = findCoachForPlayer(player.getIdUser());
                if (coach.isEmpty()) {
                    log.warn("⚠️ Aucun coach trouvé pour {}", player.getFullName());
                    continue;
                }

                String message = buildCoachMessage(player.getFullName(), result);
                notificationService.createNotificationForUsers(List.of(coach.get()), message);

                // Mémoriser l'alerte envoyée
                alertMemory.put(player.getIdUser(),
                        new AlertRecord(LocalDateTime.now(), result.severity()));

                log.warn("🚨 {} [{}] → coach {} alerté",
                        player.getFullName(), result.severity(), coach.get().getFullName());
                alertsSent++;

            } catch (Exception e) {
                log.error("❌ Erreur analyse joueur {} : {}", player.getFullName(), e.getMessage());
            }
        }
        log.info("✅ Scan quotidien terminé. {}/{} alertes envoyées.", alertsSent, players.size());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Scan horaire — CRITICAL uniquement
    // ════════════════════════════════════════════════════════════════════════
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void detectCriticalAnomaliesHourly() {
        LocalDate today  = LocalDate.now();
        LocalDate from28 = today.minusDays(ANALYSIS_WINDOW_DAYS - 1);

        List<User> players = userRepository.findAllByRole(Role.PLAYER);

        for (User player : players) {
            try {
                List<PlayerAttendance> attendances =
                        attendanceRepo.findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                player.getIdUser(), from28, today);

                AnomalyResult result = AnomalyDetector.analyze(attendances, today);

                if (!result.requiresCoachAlert() || result.severity() != Severity.CRITICAL) continue;

                // Anti-spam : cooldown 6h pour les critiques
                if (isRecentlyAlerted(player.getIdUser(), Severity.CRITICAL, CRITICAL_COOLDOWN_HOURS)) {
                    continue;
                }

                Optional<User> coach = findCoachForPlayer(player.getIdUser());
                if (coach.isEmpty()) continue;

                String message = "🔴 [ALERTE CRITIQUE HORAIRE] "
                        + buildCoachMessage(player.getFullName(), result);

                notificationService.createNotificationForUsers(List.of(coach.get()), message);
                alertMemory.put(player.getIdUser(),
                        new AlertRecord(LocalDateTime.now(), Severity.CRITICAL));

                log.error("🔴 CRITIQUE horaire — {} → coach {} notifié",
                        player.getFullName(), coach.get().getFullName());

            } catch (Exception e) {
                log.error("❌ Erreur horaire {} : {}", player.getFullName(), e.getMessage());
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Trouve le coach de l'équipe d'un joueur.
     * Cherche dans toutes les équipes si le joueur y appartient et si l'équipe a un coach.
     */
    private Optional<User> findCoachForPlayer(Long playerId) {
        return teamRepository.findTeamsByPlayerId(playerId).stream()
                .map(Team::getCoach)
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Retourne true si une alerte de même sévérité a déjà été envoyée dans les N heures.
     */
    private boolean isRecentlyAlerted(Long playerId, Severity severity, int cooldownHours) {
        AlertRecord rec = alertMemory.get(playerId);
        if (rec == null) return false;
        boolean sameOrWorse = rec.severity().ordinal() >= severity.ordinal();
        boolean withinWindow = rec.sentAt().isAfter(
                LocalDateTime.now().minusHours(cooldownHours));
        return sameOrWorse && withinWindow;
    }

    private String buildCoachMessage(String playerName, AnomalyResult result) {
        String severity = result.severity().name();
        String icon = "CRITICAL".equals(severity) ? "🚨" : "⚠️";
        String reco = "CRITICAL".equals(severity)
                ? "Intervention immédiate recommandée."
                : "Surveillance renforcée conseillée.";
        return String.format(
                "%s [Anomalie %s] %s — chute confirmée (Z=%.2f, EWMA drop=%.0f%%). " +
                        "Score actuel: %.1f | Moyenne: %.1f. → %s",
                icon, severity, playerName,
                result.zScore(), result.ewmaDrop() * 100,
                result.currentScore(), result.mean(), reco);
    }
}
