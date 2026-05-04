package com.example.streetleague.Scheduler;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Scheduler : rappelle les équipes qu'un match va bientôt être clôturé.
 *
 * Fenêtres de rappel :
 *   • J-2  (entre 47h et 49h avant le match)
 *   • J-1  (entre 23h et 25h avant le match)
 *   • H-2  (entre  1h et  3h avant le match)
 *
 * Utilise deux approches repository :
 *   1. JPQL  (findMatchesClosingSoonWithTeams)   → chargement complet avec JOIN FETCH
 *   2. Keywords Spring Data (findBy...Containing) → recherche complémentaire par nom
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchCloseReminderScheduler {

    private final MatchRepository      matchRepository;
    private final InotificationService notificationService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    // ────────────────────────────────────────────────────────────────────
    // RAPPEL J-2 : 48 h avant le match
    // ────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 0 * * * *")   // toutes les heures
    @Transactional(readOnly = true)
    public void remindMatchIn48Hours() {
        sendReminders(
                LocalDateTime.now().plusHours(47),
                LocalDateTime.now().plusHours(49),
                "📅 J-2 : Votre match contre %s aura lieu dans ~48h (le %s à %s). Préparez votre équipe !"
        );
    }

    // ────────────────────────────────────────────────────────────────────
    // RAPPEL J-1 : 24 h avant le match
    // ────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 0 * * * *")   // toutes les heures
    @Transactional(readOnly = true)
    public void remindMatchIn24Hours() {
        sendReminders(
                LocalDateTime.now().plusHours(23),
                LocalDateTime.now().plusHours(25),
                "⏰ J-1 : Votre match contre %s est demain le %s à %s. Dernière ligne droite !"
        );
    }

    // ────────────────────────────────────────────────────────────────────
    // RAPPEL H-2 : 2 h avant le match
    // ────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 0 * * * *")   // toutes les heures
    @Transactional(readOnly = true)
    public void remindMatchIn2Hours() {
        sendReminders(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3),
                "🚨 H-2 : Votre match contre %s commence dans ~2h (le %s à %s). Échauffez-vous !"
        );
    }

    // ════════════════════════════════════════════════════════════════════
    // MÉTHODE CENTRALE — utilisée par les 3 schedulers
    // ════════════════════════════════════════════════════════════════════
    private void sendReminders(LocalDateTime from, LocalDateTime to, String messageTemplate) {

        // ── APPROCHE 1 : JPQL avec JOIN FETCH sur 4 tables ───────────────
        // (Match ← TeamA ← players/captain, TeamB ← players/captain)
        List<Match> matches = matchRepository.findMatchesClosingSoonWithTeams(from, to);

        if (matches.isEmpty()) {
            log.debug("⏰ ReminderScheduler : aucun match dans [{} → {}]", from, to);
            return;
        }

//        log.info("🔔 ReminderScheduler : {} match(s) à rappeler [{} → {}]",
//                matches.size(), from, to);

        for (Match match : matches) {

            String dateStr   = match.getMatchDate().format(FMT);
            String location  = match.getLocation() != null ? match.getLocation() : "lieu à définir";
            String teamAName = match.getTeamA().getName();
            String teamBName = match.getTeamB().getName();

            // ── Notification équipe A ─────────────────────────────────
            String msgForA = String.format(messageTemplate, teamBName, dateStr, location);
            List<User> membersA = collectMembers(
                    match.getTeamA().getPlayers(),
                    match.getTeamA().getCaptain()
            );
            notificationService.createNotificationForUsers(membersA, msgForA);

            // ── Notification équipe B ─────────────────────────────────
            String msgForB = String.format(messageTemplate, teamAName, dateStr, location);
            List<User> membersB = collectMembers(
                    match.getTeamB().getPlayers(),
                    match.getTeamB().getCaptain()
            );
            notificationService.createNotificationForUsers(membersB, msgForB);

//            log.info("   ✅ Match {} → {} membres notifiés (A:{} | B:{})",
//                    match.getIdMatch(), membersA.size() + membersB.size(),
//                    membersA.size(), membersB.size());
        }
    }

    // ════════════════════════════════════════════════════════════════════
    // APPROCHE 2 — Keywords Spring Data : recherche par nom d'équipe
    // Exemple d'utilisation : retrouver les matchs d'une équipe spécifique
    // ════════════════════════════════════════════════════════════════════
    public List<Match> findUpcomingMatchesForTeamByKeyword(
            String teamName,
            LocalDateTime from,
            LocalDateTime to) {

        // Keyword multi-table : cherche dans teamA.name ET teamB.name
        return matchRepository
                .findByStatusAndMatchDateBetweenAndTeamA_NameContainingIgnoreCaseOrStatusAndMatchDateBetweenAndTeamB_NameContainingIgnoreCase(
                        com.example.streetleague.Entity.MatchStatus.ACCEPTED, from, to, teamName,
                        com.example.streetleague.Entity.MatchStatus.ACCEPTED, from, to, teamName
                );
    }

    // ────────────────────────────────────────────────────────────────────
    // UTILITAIRE : rassemble joueurs + capitaine sans doublons
    // ────────────────────────────────────────────────────────────────────
    private List<User> collectMembers(Set<User> players, User captain) {
        List<User> members = new ArrayList<>(players);
        if (captain != null && members.stream()
                .noneMatch(u -> u.getIdUser().equals(captain.getIdUser()))) {
            members.add(captain);
        }
        return members;
    }
}
