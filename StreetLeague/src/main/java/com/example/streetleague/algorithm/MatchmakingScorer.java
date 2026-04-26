package com.example.streetleague.algorithm;

import com.example.streetleague.Entity.Match;

import com.example.streetleague.Entity.Team;

import com.example.streetleague.Repository.MatchRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchmakingScorer {

    private final MatchRepository matchRepository;
    private final GeoUtils geoUtils;

    private static final double W_ELO  = 0.50;
    private static final double W_DIST = 0.30;
    private static final double W_H2H  = 0.20; // Head-to-Head = "forme" corrigé
    private static final int    ELO_THRESHOLD = 200;

    /**
     * Score composite entre 0.0 et 1.0.
     * requestorLocation = "lat,lng" fourni par le capitaine au moment de la requête.
     * teamBLocation     = dernière localisation connue de teamB (ex: dernier match joué).
     */
    public double compute(Team teamA, String locationA,
                          Team teamB, String locationB) {
        double eloScore  = scoreElo(teamA.getEloScore(), teamB.getEloScore());
        double distScore = geoUtils.scoreDistance(locationA, locationB);
        double h2hScore  = scoreH2H(teamA, teamB);
        return W_ELO * eloScore + W_DIST * distScore + W_H2H * h2hScore;
    }

    /** Scores individuels exposés pour remplir MatchCandidateResponse */
    public double scoreElo(int eloA, int eloB) {
        int delta = Math.abs(eloA - eloB);
        if (delta >= ELO_THRESHOLD) return 0.0;
        return 1.0 - (double) delta / ELO_THRESHOLD;
    }

    /**
     * H2H : parmi les matchs directs entre les deux équipes,
     * quel est le ratio de victoires de teamA ?
     * → Si jamais joué : 0.5 (neutre)
     */
    public double scoreH2H(Team teamA, Team teamB) {
        List<Match> h2hMatches = matchRepository
                .findFinishedMatchesBetween(
                        teamA.getIdTeam(),
                        teamB.getIdTeam()
                );
        if (h2hMatches.isEmpty()) return 0.5;

        long winsA = h2hMatches.stream().filter(m -> {
            boolean aIsTeamA = m.getTeamA().getIdTeam().equals(teamA.getIdTeam());
            if (aIsTeamA) return m.getScoreTeamA() > m.getScoreTeamB();
            else          return m.getScoreTeamB() > m.getScoreTeamA();
        }).count();

        return (double) winsA / h2hMatches.size();
    }
}