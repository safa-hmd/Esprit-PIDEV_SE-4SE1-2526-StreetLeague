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
    private static final double W_H2H  = 0.20; // Head-to-Head
    private static final int    ELO_THRESHOLD = 200;


    public double compute(Team teamA, String locationA,
                          Team teamB, String locationB) {
        double eloScore  = scoreElo(safeElo(teamA), safeElo(teamB));
        double distScore = geoUtils.scoreDistance(locationA, locationB);
        double h2hScore  = scoreH2H(teamA, teamB);
        return W_ELO * eloScore + W_DIST * distScore + W_H2H * h2hScore;
    }


    /**
     * [P2-FIX] Méthode null-safe : accepte Integer nullable pour éviter NPE si eloScore est null.
     * safeElo() centralise la valeur par défaut (1000 = ELO standard débutant).
     */
    public double scoreElo(Integer eloA, Integer eloB) {
        int safeA = (eloA != null) ? eloA : 1000;
        int safeB = (eloB != null) ? eloB : 1000;
        int delta = Math.abs(safeA - safeB);
        if (delta >= ELO_THRESHOLD) return 0.0;
        return 1.0 - (double) delta / ELO_THRESHOLD;
    }

    /** Surcharge pour compatibilité avec les appels existants (int primitif). */
    public double scoreElo(int eloA, int eloB) {
        return scoreElo(Integer.valueOf(eloA), Integer.valueOf(eloB));
    }

    /** Utilitaire centralisé pour récupérer l'ELO d'une équipe de façon null-safe. */
    private int safeElo(Team team) {
        return (team.getEloScore() != null) ? team.getEloScore() : 1000;
    }


    public double scoreH2H(Team teamA, Team teamB) {
        List<Match> h2hMatches = matchRepository
                .findFinishedMatchesBetween(
                        teamA.getIdTeam(),
                        teamB.getIdTeam()
                );
        // Pas d'historique H2H → score neutre 0.5 (opportunité d'une première rencontre)
        if (h2hMatches.isEmpty()) return 0.5;

        long winsA = h2hMatches.stream().filter(m -> {
            boolean aIsTeamA = m.getTeamA().getIdTeam().equals(teamA.getIdTeam());
            if (aIsTeamA) return m.getScoreTeamA() > m.getScoreTeamB();
            else          return m.getScoreTeamB() > m.getScoreTeamA();
        }).count();

        double winRateA = (double) winsA / h2hMatches.size();

        // [P2-FIX] Un bon matchmaking favorise les rivalités ÉQUILIBRÉES (proche 50/50),
        // pas les équipes qui dominent déjà l'adversaire.
        // Score max (1.0) quand winRate = 0.5 exactement ; score min (0.0) quand 0% ou 100%.
        return 1.0 - Math.abs(winRateA - 0.5) * 2.0;
    }
}