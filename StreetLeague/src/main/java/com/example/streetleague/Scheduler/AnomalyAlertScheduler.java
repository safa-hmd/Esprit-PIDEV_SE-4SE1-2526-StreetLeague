package com.example.streetleague.Scheduler;

import com.example.streetleague.Entity.PlayerAttendance;
import com.example.streetleague.Repository.PlayerAttendanceRepository;
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
import java.util.List;

/**
 * AnomalyAlertScheduler — Détection automatique d'anomalies de performance
 *
 * Scan quotidien à 8h:
 * - Analyse tous les joueurs
 * - Détecte anomalies HIGH / CRITICAL
 * - Envoie notification aux coachs
 *
 * Scan horaire:
 * - Détecte uniquement CRITICAL
 * - Notification immédiate
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyAlertScheduler {

    private final UserRepository userRepository;
    private final PlayerAttendanceRepository attendanceRepo;
    private final InotificationService notificationService;

    private static final int ANALYSIS_WINDOW_DAYS = 28;

    /**
     * Scan quotidien à 8h00
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void detectAndAlertAnomalies() {
//        log.info("🔍 [AnomalyAlertScheduler] Démarrage du scan quotidien...");

        LocalDate today = LocalDate.now();
        LocalDate from28 = today.minusDays(ANALYSIS_WINDOW_DAYS - 1);

        // Charger coachs
        List<User> coaches = userRepository.findAllByRole(Role.COACH);
        if (coaches.isEmpty()) {
          //  log.warn("⚠️ Aucun coach trouvé.");
            return;
        }

        // Charger joueurs
        List<User> players = userRepository.findAllByRole(Role.PLAYER);
//        log.info("→ Analyse de {} joueur(s)", players.size());

        int alertsSent = 0;

        for (User player : players) {
            try {
                List<PlayerAttendance> attendances =
                        attendanceRepo.findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                player.getIdUser(),
                                from28,
                                today
                        );

                AnomalyResult result = AnomalyDetector.analyze(attendances, today);

                // HIGH ou CRITICAL
                if (result.requiresCoachAlert()) {

                    String message = buildCoachMessage(
                            player.getFullName(),
                            result
                    );

                    notificationService.createNotificationForUsers(
                            coaches,
                            message
                    );

                   /* log.warn(
                            "🚨 ANOMALIE {} — {} : Z={:.2f}, chute EWMA={:.0f}% — {} coach(s) alerté(s).",
                            result.severity().name(),
                            player.getFullName(),
                            result.zScore(),
                            result.ewmaDrop() * 100,
                            coaches.size()
                    );*/

                    alertsSent++;
                }

                else if (result.isAnomaly()) {
//                    log.info(
//                            "ℹ️ Anomalie MEDIUM — {} : Z={:.2f}",
//                            player.getFullName(),
//                            result.zScore()
//                    );
                }

            } catch (Exception e) {
              /*  log.error(
                        "❌ Erreur analyse joueur {} : {}",
                        player.getFullName(),
                        e.getMessage()
                );*/
            }
        }

//        log.info(
//                "✅ Scan terminé. {}/{} joueur(s) alertés.",
//                alertsSent,
//                players.size()
//        );
    }

    /**
     * Scan toutes les heures
     * Alerte uniquement CRITICAL
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void detectCriticalAnomaliesHourly() {

        LocalDate today = LocalDate.now();
        LocalDate from28 = today.minusDays(ANALYSIS_WINDOW_DAYS - 1);

        List<User> coaches = userRepository.findAllByRole(Role.COACH);
        if (coaches.isEmpty()) {
            return;
        }

        List<User> players = userRepository.findAllByRole(Role.PLAYER);

        for (User player : players) {
            try {

                List<PlayerAttendance> attendances =
                        attendanceRepo.findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                player.getIdUser(),
                                from28,
                                today
                        );

                AnomalyResult result =
                        AnomalyDetector.analyze(attendances, today);

                // فقط CRITICAL
                if (result.requiresCoachAlert()
                        && result.severity() == Severity.CRITICAL) {

                    String message =
                            "🔴 [ALERTE CRITIQUE HORAIRE] "
                                    + buildCoachMessage(
                                    player.getFullName(),
                                    result
                            );

                    notificationService.createNotificationForUsers(
                            coaches,
                            message
                    );

                   /* log.error(
                            "🔴 CRITIQUE — {} : Z={}",
                            player.getFullName(),
                            result.zScore()
                    );*/
                }

            } catch (Exception e) {
                log.error(
                        "❌ Erreur analyse horaire {} : {}",
                        player.getFullName(),
                        e.getMessage()
                );
            }
        }
    }

    /**
     * Construire message coach
     */
    private String buildCoachMessage(
            String playerName,
            AnomalyResult result
    ) {

        String severity = result.severity().name();
        String icon =
                "CRITICAL".equals(severity)
                        ? "🚨"
                        : "⚠️";

        String recommendation =
                "CRITICAL".equals(severity)
                        ? "Intervention immédiate recommandée. Vérifiez son état physique et mental."
                        : "Surveillance renforcée conseillée. Envisagez un entretien individuel.";

        return String.format(
                "%s [Anomalie %s] %s affiche une chute de performance confirmée " +
                        "(Z-Score = %.2f, chute EWMA = %.0f%%). " +
                        "Score actuel : %.1f | Moyenne historique : %.1f. " +
                        "→ %s",
                icon,
                severity,
                playerName,
                result.zScore(),
                result.ewmaDrop() * 100,
                result.currentScore(),
                result.mean(),
                recommendation
        );
    }
}

