package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TournamentMatchRepository;
import com.example.streetleague.Repository.TournamentRegistrationRepository;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BracketgeneratorserviceImpl {

    private final TournamentRepository tournamentRepository;
    private final TournamentMatchRepository tournamentMatchRepository;
    private final TournamentRegistrationRepository registrationRepository;
    private final MatchRepository matchRepository;

    // ═══════════════════════════════════════════════════════════
    //  ENTRY POINT — sans BracketType → Single Elimination par défaut
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public List<TournamentMatch> generateBracket(Long tournamentId) {
        return generateBracket(tournamentId, BracketType.SINGLE_ELIMINATION);
    }

    @Transactional
    public List<TournamentMatch> generateBracket(Long tournamentId, BracketType bracketType) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found: " + tournamentId));

        // Supprimer un bracket existant avant de régénérer
        if (tournamentMatchRepository.existsByTournamentId(tournamentId)) {
            tournamentMatchRepository.clearNextMatchLinks(tournamentId); // ← ajouter cette ligne
            tournamentMatchRepository.deleteByTournamentId(tournamentId);
        }

        // Récupérer les inscrits CONFIRMED
        List<TournamentRegistration> confirmed = registrationRepository
                .findByTournamentIdAndStatusIn(tournamentId,
                        List.of(RegistrationStatus.CONFIRMED, RegistrationStatus.PENDING));

        if (confirmed.size() < 2)
            throw new IllegalArgumentException("Au moins 2 participants confirmés requis.");

        boolean isTeam = tournament.getTournamentType() == TournamentType.TEAM;

        return switch (bracketType) {
            case SINGLE_ELIMINATION -> generateSingleElimination(tournament, confirmed, isTeam);
            case ROUND_ROBIN        -> generateRoundRobin(tournament, confirmed, isTeam);
        };
    }

    // ═══════════════════════════════════════════════════════════
    //  ALGO 1 : SINGLE ELIMINATION
    // ═══════════════════════════════════════════════════════════

    private List<TournamentMatch> generateSingleElimination(
            Tournament tournament,
            List<TournamentRegistration> registrations,
            boolean isTeam) {

        List<TournamentRegistration> seeded = new ArrayList<>(registrations);
        Collections.shuffle(seeded);

        int n = nextPowerOfTwo(seeded.size());
        int totalRounds = (int) (Math.log(n) / Math.log(2));

        String fieldLocation = resolveLocation(tournament);
        List<TournamentMatch> allMatches = new ArrayList<>();
        LocalDateTime matchDate = tournament.getStartDate().atStartOfDay();

        // ── Round 1 ──
        List<TournamentMatch> currentRound = new ArrayList<>();

        for (int i = 0; i < n / 2; i++) {
            TournamentRegistration regA = (2 * i < seeded.size()) ? seeded.get(2 * i) : null;
            TournamentRegistration regB = (2 * i + 1 < seeded.size()) ? seeded.get(2 * i + 1) : null;

            boolean isBye = (regB == null); // regA est toujours non-null (seeded.size() >= 2)

            TournamentMatch tm = TournamentMatch.builder()
                    .tournament(tournament)
                    .round(1)
                    .position(i)
                    .bracketType(BracketType.SINGLE_ELIMINATION)
                    .status(isBye ? TournamentMatchStatus.BYE : TournamentMatchStatus.PENDING)
                    .build();

            if (isBye) {
                // FIX BUG 2 & 3 : pas de Match JPA pour un BYE — juste le vainqueur direct
                if (isTeam) {
                    tm.setWinnerTeam(regA.getTeam());
                } else {
                    tm.setPlayer1(regA.getPlayer());
                    tm.setWinnerPlayer(regA.getPlayer());
                }
            } else {
                if (isTeam) {
                    Match match = new Match();
                    match.setMatchDate(matchDate);
                    match.setLocation(fieldLocation);
                    match.setStatus(MatchStatus.SCHEDULED);
                    match.setTeamA(regA.getTeam());
                    match.setTeamB(regB.getTeam());
                    tm.setMatch(match);          // ← seulement pour TEAM
                } else {
                    tm.setPlayer1(regA.getPlayer());
                    tm.setPlayer2(regB.getPlayer());
                    // pas de Match JPA pour INDIVIDUAL
                }
                matchDate = matchDate.plusDays(1);
            }

            currentRound.add(tm);
        }

        List<TournamentMatch> savedRound = tournamentMatchRepository.saveAll(currentRound);
        allMatches.addAll(savedRound);

        // ── Rounds suivants : matchs vides + liens nextMatch ──
        for (int r = 2; r <= totalRounds; r++) {
            List<TournamentMatch> prevRound = savedRound;
            List<TournamentMatch> nextRound = new ArrayList<>();
            int matchesInRound = prevRound.size() / 2;

            for (int i = 0; i < matchesInRound; i++) {
                TournamentMatch tm = TournamentMatch.builder()
                        .tournament(tournament)
                        .round(r)
                        .position(i)
                        .bracketType(BracketType.SINGLE_ELIMINATION)
                        .status(TournamentMatchStatus.PENDING)
                        .build();

                TournamentMatch saved = tournamentMatchRepository.save(tm);

                prevRound.get(2 * i).setNextMatch(saved);
                prevRound.get(2 * i).setNextMatchSlot("A");
                prevRound.get(2 * i + 1).setNextMatch(saved);
                prevRound.get(2 * i + 1).setNextMatchSlot("B");

                nextRound.add(saved);
            }

            tournamentMatchRepository.saveAll(prevRound);
            savedRound = nextRound;
            allMatches.addAll(nextRound);
        }

        // Propager les BYEs : placer le vainqueur dans le prochain match
        propagateByes(allMatches, isTeam);

        return tournamentMatchRepository.saveAll(allMatches);
    }

    // ═══════════════════════════════════════════════════════════
    //  ALGO 2 : ROUND ROBIN — rotation circulaire (polygon rotation)
    // ═══════════════════════════════════════════════════════════
    private List<TournamentMatch> generateRoundRobin(
            Tournament tournament,
            List<TournamentRegistration> registrations,
            boolean isTeam) {

        List<TournamentRegistration> players = new ArrayList<>(registrations);

        if (players.size() % 2 != 0) players.add(null);

        int n = players.size();
        int totalRounds = n - 1;

        String fieldLocation = resolveLocation(tournament);
        List<TournamentMatch> allMatches = new ArrayList<>();
        LocalDateTime matchDate = tournament.getStartDate().atStartOfDay();

        for (int round = 0; round < totalRounds; round++) {
            int position = 0;

            for (int i = 0; i < n / 2; i++) {
                TournamentRegistration regA = players.get(i);
                TournamentRegistration regB = players.get(n - 1 - i);

                if (regA == null || regB == null) continue;

                TournamentMatch tm = TournamentMatch.builder()
                        .tournament(tournament)
                        .round(round + 1)
                        .position(position++)
                        .bracketType(BracketType.ROUND_ROBIN)
                        .status(TournamentMatchStatus.PENDING)
                        .build();

                if (isTeam) {
                    Match match = new Match();
                    match.setMatchDate(matchDate);
                    match.setLocation(fieldLocation);
                    match.setStatus(MatchStatus.SCHEDULED);
                    match.setTeamA(regA.getTeam());
                    match.setTeamB(regB.getTeam());
                    tm.setMatch(match);
                } else {
                    tm.setPlayer1(regA.getPlayer());
                    tm.setPlayer2(regB.getPlayer());
                }

                allMatches.add(tm);
            }

            matchDate = matchDate.plusDays(1);

            TournamentRegistration last = players.remove(n - 1);
            players.add(1, last);
        }

        return tournamentMatchRepository.saveAll(allMatches);
    }


    // ═══════════════════════════════════════════════════════════
    //  SOUMISSION D'UN RÉSULTAT + AVANCEMENT AUTOMATIQUE
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public TournamentMatch submitResult(Long tournamentMatchId, Long winnerId, boolean winnerIsTeam) {
        TournamentMatch tm = tournamentMatchRepository.findById(tournamentMatchId)
                .orElseThrow(() -> new RuntimeException("TournamentMatch not found: " + tournamentMatchId));

        if (tm.getStatus() == TournamentMatchStatus.COMPLETED)
            throw new IllegalStateException("Résultat déjà saisi pour ce match.");

        tm.setStatus(TournamentMatchStatus.COMPLETED);

        if (winnerIsTeam) {
            Team winner = resolveWinnerTeam(tm, winnerId);
            tm.setWinnerTeam(winner);
            advanceWinnerTeam(tm, winner);
        } else {
            User winner = resolveWinnerPlayer(tm, winnerId);
            tm.setWinnerPlayer(winner);
            advanceWinnerPlayer(tm, winner);
        }

        return tournamentMatchRepository.save(tm);
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS PRIVÉS
    // ═══════════════════════════════════════════════════════════

    /** Résout la location depuis le Field lié au tournoi */
    private String resolveLocation(Tournament tournament) {
        if (tournament.getField() != null && tournament.getField().getLocation() != null)
            return tournament.getField().getLocation();
        return "TBD";
    }

    /** Propage les BYEs : avancer le vainqueur dans le nextMatch */
    private void propagateByes(List<TournamentMatch> allMatches, boolean isTeam) {
        for (TournamentMatch tm : allMatches) {
            if (tm.getStatus() != TournamentMatchStatus.BYE) continue;
            if (isTeam && tm.getWinnerTeam() != null)
                advanceWinnerTeam(tm, tm.getWinnerTeam());
            else if (!isTeam && tm.getWinnerPlayer() != null)
                advanceWinnerPlayer(tm, tm.getWinnerPlayer());
        }
    }

    /** Avance le vainqueur (Team) dans le prochain match du bracket */
    private void advanceWinnerTeam(TournamentMatch tm, Team winner) {
        if (tm.getNextMatch() == null) return;

        TournamentMatch next = tm.getNextMatch();
        if (next.getMatch() == null) {
            Match m = new Match();
            m.setStatus(MatchStatus.SCHEDULED);          // ← manquait
            m.setLocation(resolveLocation(tm.getTournament())); // ← manquait
            next.setMatch(m);
        }

        if ("A".equals(tm.getNextMatchSlot())) next.getMatch().setTeamA(winner);
        else                                    next.getMatch().setTeamB(winner);

        Match nextMatch = next.getMatch();
        if (nextMatch.getTeamA() != null && nextMatch.getTeamB() != null) {
            next.setMatch(matchRepository.save(nextMatch));
            //tournamentMatchRepository.save(next);
        }

        tournamentMatchRepository.save(next);
    }

    /** Avance le vainqueur (User/Player) dans le prochain match */
    private void advanceWinnerPlayer(TournamentMatch tm, User winner) {
        if (tm.getNextMatch() == null) return;

        TournamentMatch next = tm.getNextMatch();
        if ("A".equals(tm.getNextMatchSlot())) next.setPlayer1(winner);
        else                                    next.setPlayer2(winner);

        tournamentMatchRepository.save(next);
    }

    private Team resolveWinnerTeam(TournamentMatch tm, Long winnerId) {
        Team a = (tm.getMatch() != null) ? tm.getMatch().getTeamA() : null;
        Team b = (tm.getMatch() != null) ? tm.getMatch().getTeamB() : null;
        if (a != null && a.getIdTeam().equals(winnerId)) return a;
        if (b != null && b.getIdTeam().equals(winnerId)) return b;
        throw new IllegalArgumentException("Winner team not part of this match: " + winnerId);
    }

    private User resolveWinnerPlayer(TournamentMatch tm, Long winnerId) {
        User p1 = tm.getPlayer1();
        User p2 = tm.getPlayer2();
        // FIX BUG : getIdUser() et non getId()
        if (p1 != null && p1.getIdUser().equals(winnerId)) return p1;
        if (p2 != null && p2.getIdUser().equals(winnerId)) return p2;
        throw new IllegalArgumentException("Winner player not part of this match: " + winnerId);
    }

    /** Prochaine puissance de 2 supérieure ou égale à n */
    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) power <<= 1;
        return power;
    }
}