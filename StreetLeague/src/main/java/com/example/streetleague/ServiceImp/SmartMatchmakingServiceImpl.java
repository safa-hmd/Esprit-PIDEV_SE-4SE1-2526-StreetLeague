package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.ServiceInterface.ISmartMatchmakingService;
import com.example.streetleague.dto.MatchCandidateResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SmartMatchmakingServiceImpl implements ISmartMatchmakingService {

    private final TeamRepository  teamRepository;
    private final MatchRepository matchRepository;

    private static final int    K_FACTOR     = 32;
    private static final int    MAX_ELO_DIFF = 200;
    private static final double W_ELO        = 0.70;
    private static final double W_H2H        = 0.30;

    // ─────────────────────────────────────────────────────────────────────────
    // ELO UPDATE  (Δelo = K × (résultat − probabilité_attendue))
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void updateEloAfterMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (match.getScoreTeamA() == null || match.getScoreTeamB() == null) {
            log.warn("ELO skip — scores not set for match {}", matchId);
            return;
        }

        Team teamA = match.getTeamA();
        Team teamB = match.getTeamB();
        int  eloA  = teamA.getEloScore() != null ? teamA.getEloScore() : 1000;
        int  eloB  = teamB.getEloScore() != null ? teamB.getEloScore() : 1000;

        double expA = 1.0 / (1.0 + Math.pow(10, (eloB - eloA) / 400.0));
        double expB = 1.0 - expA;

        double actA, actB;
        int sA = match.getScoreTeamA(), sB = match.getScoreTeamB();
        if      (sA > sB) { actA = 1.0; actB = 0.0; }
        else if (sA < sB) { actA = 0.0; actB = 1.0; }
        else              { actA = 0.5; actB = 0.5; }

        int newEloA = (int) Math.round(eloA + K_FACTOR * (actA - expA));
        int newEloB = (int) Math.round(eloB + K_FACTOR * (actB - expB));

        teamA.setEloScore(newEloA);
        teamB.setEloScore(newEloB);
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        log.info("ELO | {} : {} → {}  |  {} : {} → {}",
                teamA.getName(), eloA, newEloA,
                teamB.getName(), eloB, newEloB);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SMART OPPONENT SEARCH
    // Pipeline :
    //   1. Filtrage  → même sport, équipe différente, |Δelo| ≤ MAX_ELO_DIFF
    //   2. ELO fit   → 100 − (|Δelo| / 2)          [0–100]
    //   3. H2H       → 100 − (matchs_joués × 20)    [0–100]
    //   4. Score     → 0.70 × eloFit + 0.30 × h2h
    //   5. Top 5     trié par score décroissant
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public List<MatchCandidateResponse> findSmartOpponents(Long teamId) {

        Team myTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        int    myElo   = myTeam.getEloScore() != null ? myTeam.getEloScore() : 1000;
        String mySport = myTeam.getSport();

        // Charge une seule fois tous les matchs (évite N+1)
        List<Match> allMatches = matchRepository.findAll();

        List<MatchCandidateResponse> candidates = teamRepository.findAll().stream()
                // ── Filtrage ──────────────────────────────────────────────
                .filter(t -> t.getSport() != null
                        && t.getSport().equalsIgnoreCase(mySport))
                .filter(t -> !t.getIdTeam().equals(teamId))
                .filter(t -> {
                    int oElo = t.getEloScore() != null ? t.getEloScore() : 1000;
                    return Math.abs(myElo - oElo) <= MAX_ELO_DIFF;
                })
                // ── Scoring ───────────────────────────────────────────────
                .map(opponent -> {
                    int    opponentElo = opponent.getEloScore() != null
                            ? opponent.getEloScore() : 1000;
                    Long   oppId       = opponent.getIdTeam();

                    // 1) ELO fit
                    int    eloDiff    = Math.abs(myElo - opponentElo);
                    double eloFit     = 100.0 - (eloDiff / 2.0); // [0–100]

                    // 2) H2H balance  (moins on a joué ensemble, mieux c'est)
                    long h2hCount = allMatches.stream()
                            .filter(m ->
                                    (m.getTeamA().getIdTeam().equals(teamId)   && m.getTeamB().getIdTeam().equals(oppId)) ||
                                            (m.getTeamB().getIdTeam().equals(teamId)   && m.getTeamA().getIdTeam().equals(oppId))
                            ).count();
                    double h2h = Math.max(0, 100.0 - (h2hCount * 20));

                    // 3) Score final pondéré
                    double finalScore = W_ELO * eloFit + W_H2H * h2h;
                    double rounded    = Math.round(finalScore * 100.0) / 100.0;

                    return MatchCandidateResponse.builder()
                            .teamId(oppId)
                            .teamName(opponent.getName())
                            .sport(opponent.getSport())
                            .eloScore(opponentElo)
                            .eloFitScore(Math.round(eloFit  * 100.0) / 100.0)
                            .h2hScore(Math.round(h2h        * 100.0) / 100.0)
                            .matchCompatibilityScore(rounded)
                            .build();
                })
                // ── Top 5 ─────────────────────────────────────────────────
                .sorted(Comparator
                        .comparingDouble(MatchCandidateResponse::getMatchCompatibilityScore)
                        .reversed())
                .limit(5)
                .collect(Collectors.toList());

        log.info("SmartMatchmaking | team {} | {} candidats trouvés", teamId, candidates.size());
        return candidates;
    }
}