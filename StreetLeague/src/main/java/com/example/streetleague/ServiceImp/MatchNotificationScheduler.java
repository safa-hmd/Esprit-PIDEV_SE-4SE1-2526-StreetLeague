package com.example.streetleague.ServiceImp;

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

/**
 * Scheduler : envoie des notifications 24h avant chaque match ACCEPTED
 * en utilisant une requête JPQL qui fait JOIN sur Match → TeamA → players
 * et Join sur Match → TeamB → players (multi-table).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchNotificationScheduler {

    private final MatchRepository        matchRepository;
    private final InotificationService   notificationService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    /**
     * S'exécute toutes les heures.
     * Cherche les matchs dont la date est dans [now+23h, now+25h] puis envoie
     * une notification à tous les joueurs des deux équipes (JPQL multi-table).
     */
    @Scheduled(fixedRate = 3_600_000)   // toutes les heures
    @Transactional(readOnly = true)
    public void notifyUpcomingMatches() {
        LocalDateTime from = LocalDateTime.now().plusHours(23);
        LocalDateTime to   = LocalDateTime.now().plusHours(25);

        // ── JPQL avec JOIN FETCH sur 3 tables (Match, TeamA+players, TeamB+players)
        List<Match> matches = matchRepository.findUpcomingMatchesWithTeamPlayers(from, to);

        if (matches.isEmpty()) {
            log.debug("⏰ MatchNotificationScheduler : aucun match à notifier dans la fenêtre [{} → {}]", from, to);
            return;
        }

        log.info("🔔 MatchNotificationScheduler : {} match(s) à notifier", matches.size());

        for (Match match : matches) {
            String dateStr    = match.getMatchDate().format(FMT);
            String location   = match.getLocation() != null ? match.getLocation() : "lieu à définir";
            String teamAName  = match.getTeamA().getName();
            String teamBName  = match.getTeamB().getName();

            String message = String.format(
                    "⚽ Rappel : votre match %s vs %s aura lieu le %s à %s !",
                    teamAName, teamBName, dateStr, location
            );

            // Rassembler TOUS les joueurs des deux équipes (sans doublons)
            List<User> allPlayers = new ArrayList<>();
            allPlayers.addAll(match.getTeamA().getPlayers());
            allPlayers.addAll(match.getTeamB().getPlayers());

            // Ajouter aussi les capitaines si non déjà dans la liste
            if (match.getTeamA().getCaptain() != null &&
                    allPlayers.stream().noneMatch(u -> u.getIdUser().equals(match.getTeamA().getCaptain().getIdUser()))) {
                allPlayers.add(match.getTeamA().getCaptain());
            }
            if (match.getTeamB().getCaptain() != null &&
                    allPlayers.stream().noneMatch(u -> u.getIdUser().equals(match.getTeamB().getCaptain().getIdUser()))) {
                allPlayers.add(match.getTeamB().getCaptain());
            }

            notificationService.createNotificationForUsers(allPlayers, message);
            log.info("   ✅ Notifications envoyées pour le match {} ({} joueurs)", match.getIdMatch(), allPlayers.size());
        }
    }
}
