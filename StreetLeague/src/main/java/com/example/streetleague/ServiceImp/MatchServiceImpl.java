package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ImatchService;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.MatchRequest;
import com.example.streetleague.dto.MatchResponse;
import com.example.streetleague.dto.MatchUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MatchServiceImpl implements ImatchService {

    MatchRepository       matchRepository;
    TeamRepository        teamRepository;
    UserRepository        userRepository;
    EloService            eloService;
    InotificationService  notificationService; // ← ajout


    // ─────────────────────────────────────────────────────────────────────
    // Helper : collecte tous les joueurs + capitaines des 2 équipes
    // ─────────────────────────────────────────────────────────────────────
    private List<User> collectTargets(Team teamA, Team teamB) {
        List<User> targets = new ArrayList<>();
        if (teamA.getPlayers() != null) targets.addAll(teamA.getPlayers());
        if (teamB.getPlayers() != null) targets.addAll(teamB.getPlayers());
        if (teamA.getCaptain() != null && !targets.contains(teamA.getCaptain()))
            targets.add(teamA.getCaptain());
        if (teamB.getCaptain() != null && !targets.contains(teamB.getCaptain()))
            targets.add(teamB.getCaptain());
        return targets;
    }

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─────────────────────────────────────────────────────────────────────
    // ADD MATCH
    // ─────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public MatchResponse addMatch(MatchRequest dto, Long teamAId, Long teamBId, Long captainId) {
        Team teamA = teamRepository.findById(teamAId)
                .orElseThrow(() -> new RuntimeException("TeamA not found: " + teamAId));
        Team teamB = teamRepository.findById(teamBId)
                .orElseThrow(() -> new RuntimeException("TeamB not found: " + teamBId));
        User captain = userRepository.findById(captainId)
                .orElseThrow(() -> new RuntimeException("User not found: " + captainId));

        if (captain.getRole() != Role.PLAYER && captain.getRole() != Role.COACH)
            throw new RuntimeException("Only a PLAYER or COACH can create a match");
        if (!teamA.getCaptain().getIdUser().equals(captainId))
            throw new RuntimeException("Only the captain of TeamA can send a match request");
        if (teamAId.equals(teamBId))
            throw new RuntimeException("A team cannot play against itself");
        if (teamA.getSport() == null || !teamA.getSport().equalsIgnoreCase(teamB.getSport()))
            throw new RuntimeException("Both teams must play the same sport to create a match");
        if (dto.location() == null || dto.location().isBlank())
            throw new RuntimeException("Location is required");
        if (dto.location().length() < 3 || dto.location().length() > 100)
            throw new RuntimeException("Location must be between 3 and 100 characters");

        if (dto.matchDate() == null)
            throw new RuntimeException("Match date is required");
        if (dto.matchDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Match date must be in the future");


        //boolean alreadyExists = matchRepository.findAll().stream().anyMatch(e ->
        boolean alreadyExists = matchRepository.findAllComplete().stream().anyMatch(e ->

                (e.getStatus() == MatchStatus.PENDING || e.getStatus() == MatchStatus.ACCEPTED) &&
                        ((e.getTeamA().getIdTeam().equals(teamAId) && e.getTeamB().getIdTeam().equals(teamBId)) ||
                                (e.getTeamA().getIdTeam().equals(teamBId) && e.getTeamB().getIdTeam().equals(teamAId))));
        if (alreadyExists)
            throw new RuntimeException("A pending/accepted match already exists between these teams");

        Match m = new Match();
        m.setMatchDate(dto.matchDate());
        m.setLocation(dto.location());
        m.setTeamA(teamA);
        m.setTeamB(teamB);
        m.setCreatedBy(captain);
        m.setStatus(MatchStatus.PENDING);

        Match saved = matchRepository.save(m);

        // ── Notification création ─────────────────────────────────────
        String notifMsg = String.format(
                "New Match Scheduled\n%s vs %s\nDate: %s\nLocation: %s\nStatus: PENDING",
                teamA.getName(),
                teamB.getName(),
                dto.matchDate().format(FMT),
                dto.location()
        );
        notificationService.createNotificationForUsers(collectTargets(teamA, teamB), notifMsg);

        return MatchResponse.fromEntity(saved);
    }


    @Override
    @Transactional
    public MatchResponse updateMatch(MatchUpdateRequest dto, Long captainId) {
        Match existing = matchRepository.findById(dto.idMatch())
                .orElseThrow(() -> new RuntimeException("Match not found: " + dto.idMatch()));

        if (!existing.getCreatedBy().getIdUser().equals(captainId))
            throw new RuntimeException("Only the captain who created this match can edit it");
        if (existing.getStatus() == MatchStatus.FINISHED || existing.getStatus() == MatchStatus.CANCELLED)
            throw new RuntimeException("Cannot edit a finished or cancelled match");

        if (dto.location() != null) {
            if (dto.location().isBlank())
                throw new RuntimeException("Location cannot be empty");
            if (dto.location().length() < 3 || dto.location().length() > 100)
                throw new RuntimeException("Location must be between 3 and 100 characters");
            existing.setLocation(dto.location());
        }
        if (dto.matchDate() != null) {
            if (dto.matchDate().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Match date must be in the future");
            existing.setMatchDate(dto.matchDate());
        }
        if (dto.status() == MatchStatus.FINISHED) {
            if (dto.scoreTeamA() == null || dto.scoreTeamB() == null)
                throw new RuntimeException("Scores are required when setting match as FINISHED");
            if (dto.scoreTeamA() < 0 || dto.scoreTeamB() < 0)
                throw new RuntimeException("Scores cannot be negative");
            existing.setScoreTeamA(dto.scoreTeamA());
            existing.setScoreTeamB(dto.scoreTeamB());
        }
        if (dto.status() != null)
            existing.setStatus(dto.status());

        Match saved = matchRepository.save(existing);

        if (saved.getStatus() == MatchStatus.FINISHED
                && saved.getScoreTeamA() != null
                && saved.getScoreTeamB() != null
                && !saved.isStatsUpdated()) {
            eloService.updateEloAfterMatch(saved);
            saved = matchRepository.findById(saved.getIdMatch()).orElse(saved);
        }

        Team teamA = saved.getTeamA();
        Team teamB = saved.getTeamB();

        // ── Notification selon le nouveau statut ──────────────────────
        String notifMsg;

        if (saved.getStatus() == MatchStatus.REJECTED) {
            notifMsg = String.format(
                    "Match Rejected\n%s vs %s\nDate: %s\nThe match request has been declined.",
                    teamA.getName(),
                    teamB.getName(),
                    saved.getMatchDate().format(FMT)
            );
        } else if (saved.getStatus() == MatchStatus.FINISHED) {
            notifMsg = String.format(
                    "Match Finished\n%s %d - %d %s\nDate: %s\nLocation: %s",
                    teamA.getName(), saved.getScoreTeamA(),
                    saved.getScoreTeamB(), teamB.getName(),
                    saved.getMatchDate().format(FMT),
                    saved.getLocation()
            );
        } else if (saved.getStatus() == MatchStatus.ACCEPTED) {
            notifMsg = String.format(
                    "Match Accepted\n%s vs %s\nDate: %s\nLocation: %s\nSee you on the field!",
                    teamA.getName(),
                    teamB.getName(),
                    saved.getMatchDate().format(FMT),
                    saved.getLocation()
            );
        } else {
            notifMsg = String.format(
                    "Match Updated\n%s vs %s\nDate: %s\nLocation: %s\nStatus: %s",
                    teamA.getName(),
                    teamB.getName(),
                    saved.getMatchDate().format(FMT),
                    saved.getLocation(),
                    saved.getStatus()
            );
        }

        notificationService.createNotificationForUsers(collectTargets(teamA, teamB), notifMsg);

        return MatchResponse.fromEntity(saved);
    }




    @Override
    @Transactional
    public void deleteMatch(Long idMatch, Long userId) {
        Match match = matchRepository.findById(idMatch)
                .orElseThrow(() -> new RuntimeException("Match not found: " + idMatch));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        boolean isAdmin    = user.getRole() == Role.ADMIN;
        boolean isCaptainA = match.getTeamA().getCaptain().getIdUser().equals(userId);
        boolean isCaptainB = match.getTeamB().getCaptain().getIdUser().equals(userId);

        if (!isAdmin && !isCaptainA && !isCaptainB)
            throw new RuntimeException("Only the captain of TeamA or TeamB can delete this match");

        // ✅ Supprimer cette ligne :
        // if (match.getStatus() == MatchStatus.FINISHED)
        //     throw new RuntimeException("Cannot delete a finished match");

        Team teamA = match.getTeamA();
        Team teamB = match.getTeamB();

        String notifMsg = String.format(
                "Match Cancelled\n%s vs %s\nWas scheduled for: %s\nLocation: %s",
                teamA.getName(), teamB.getName(),
                match.getMatchDate().format(FMT),
                match.getLocation()
        );
        notificationService.createNotificationForUsers(collectTargets(teamA, teamB), notifMsg);

        matchRepository.deleteById(idMatch);
    }


    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> ShowMatchs() {
        //return matchRepository.findAll().stream()
          //      .map(MatchResponse::fromEntity).toList();

        return matchRepository.findAllComplete().stream()
                .map(MatchResponse::fromEntity).toList();
    }



    // ── SHOW ONE ──────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public MatchResponse ShowMatch(Long idMatch) {
        return MatchResponse.fromEntity(
                matchRepository.findById(idMatch)
                        .orElseThrow(() -> new RuntimeException("Match not found: " + idMatch)));
    }

    @Override
    public MatchResponse respondToMatch(Long matchId, Long captainId, boolean accept) {
        Match match = matchRepository.findByIdWithTeams(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (match.getStatus() != MatchStatus.PENDING)
            throw new RuntimeException("Match is no longer pending");

        if (!match.getTeamB().getCaptain().getIdUser().equals(captainId))
            throw new RuntimeException("Only the captain of TeamB can respond");

        match.setStatus(accept ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
        Match saved = matchRepository.save(match);

        // ── Notification sécurisée ────────────────────────────────
        try {
            Team teamA = saved.getTeamA();
            Team teamB = saved.getTeamB();

            String notifMsg = accept
                    ? String.format("Match Accepted\n%s vs %s\nDate: %s\nLocation: %s",
                    teamA.getName(), teamB.getName(),
                    saved.getMatchDate().format(FMT), saved.getLocation())
                    : String.format("Match Rejected\n%s vs %s\nDate: %s\n%s declined.",
                    teamA.getName(), teamB.getName(),
                    saved.getMatchDate().format(FMT), teamB.getName());

            List<User> targets = new ArrayList<>();
            if (teamA.getPlayers() != null) targets.addAll(teamA.getPlayers());
            if (teamB.getPlayers() != null) targets.addAll(teamB.getPlayers());
            if (teamA.getCaptain() != null && !targets.contains(teamA.getCaptain()))
                targets.add(teamA.getCaptain());
            if (teamB.getCaptain() != null && !targets.contains(teamB.getCaptain()))
                targets.add(teamB.getCaptain());

            if (!targets.isEmpty())
                notificationService.createNotificationForUsers(targets, notifMsg);

        } catch (Exception e) {
          //  log.warn("Notification failed for match {}: {}", matchId, e.getMessage());
        }

        return MatchResponse.fromEntity(saved);
    }
}
