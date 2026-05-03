package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.algorithm.GeoUtils;
import com.example.streetleague.algorithm.MatchmakingScorer;
import com.example.streetleague.dto.MatchCandidateResponse;
import com.example.streetleague.exception.NoOpponentFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final MatchmakingScorer scorer;
    private final GeoUtils geoUtils;

    /** Surcharge sans filtre sport → utilise le sport de l'équipe A */
    public List<MatchCandidateResponse> findCandidates(Team teamA,
                                                       String locationA,
                                                       int topN) {
        return findCandidates(teamA, locationA, topN, null);
    }

    /** Recherche avec filtre sport optionnel.
     *  Si sportFilter est null/vide, on filtre sur le sport de l'équipe A.
     *  Si sportFilter = "ALL", on retourne tous les sports. */
    public List<MatchCandidateResponse> findCandidates(Team teamA,
                                                       String locationA,
                                                       int topN,
                                                       String sportFilter) {
        int safeTopN = topN > 0 ? topN : 50;
        int teamAElo = teamA.getEloScore() != null ? teamA.getEloScore() : 1000;

        // Déterminer le sport effectif pour le filtrage
        String effectiveSport;
        if ("ALL".equalsIgnoreCase(sportFilter)) {
            effectiveSport = null; // pas de filtre sport
        } else if (sportFilter != null && !sportFilter.isBlank()) {
            effectiveSport = sportFilter;
        } else {
            effectiveSport = teamA.getSport(); // par défaut : même sport que l'équipe A
        }

        List<Team> candidates = (effectiveSport != null)
                ? teamRepository.findEligibleOpponentsBySport(teamA.getIdTeam(), effectiveSport)
                : teamRepository.findEligibleOpponents(teamA.getIdTeam());

        System.out.println(">>> candidates found: " + candidates.size()
                + " (sport=" + (effectiveSport != null ? effectiveSport : "ALL") + ")");

        if (candidates.isEmpty()) {
            throw new NoOpponentFoundException(
                    "Aucune équipe compatible trouvée pour " + teamA.getName()
                            + (effectiveSport != null ? " (sport: " + effectiveSport + ")" : "")
            );
        }

        return candidates.stream()
                .map(teamB -> {
                    String locationB = matchRepository
                            .findLastLocationByTeam(teamB.getIdTeam())
                            .orElse(null);
                    int teamBElo = teamB.getEloScore() != null ? teamB.getEloScore() : 1000;

                    double eloFit = scorer.scoreElo(teamAElo, teamBElo);
                    double h2h    = scorer.scoreH2H(teamA, teamB);

                    double distScore;
                    if (geoUtils.isGpsCoords(locationA) && geoUtils.isGpsCoords(locationB)) {
                        double km = geoUtils.haversineKm(locationA, locationB);
                        distScore = Math.max(0.0, 1.0 - Math.min(1.0, km / 50.0));
                    } else {
                        distScore = 0.5;
                    }

                    double composite = (0.50 * eloFit + 0.20 * h2h + 0.30 * distScore) * 100.0;

                    return MatchCandidateResponse.builder()
                            .teamId(teamB.getIdTeam())
                            .teamName(teamB.getName())
                            .sport(teamB.getSport())
                            .eloScore(teamBElo)
                            .eloFitScore(Math.round(eloFit * 1000.0) / 10.0)
                            .h2hScore(Math.round(h2h * 1000.0) / 10.0)
                            .matchCompatibilityScore(Math.round(composite * 10.0) / 10.0)
                            .build();
                })
                .filter(c -> c.getMatchCompatibilityScore() > 0.0)
                .sorted(Comparator.comparingDouble(
                        MatchCandidateResponse::getMatchCompatibilityScore).reversed())
                .limit(safeTopN)
                .toList();
    }
}
