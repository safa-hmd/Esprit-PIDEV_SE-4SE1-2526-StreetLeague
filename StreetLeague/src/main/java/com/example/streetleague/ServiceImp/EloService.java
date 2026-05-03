package com.example.streetleague.ServiceImp;



import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.algorithm.EloCalculator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EloService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public void updateEloAfterMatch(Match match) {
        Team a = match.getTeamA();
        Team b = match.getTeamB();

        if (match.getScoreTeamA() == null || match.getScoreTeamB() == null) return;
        if (match.isStatsUpdated()) return; // idempotent — évite double MAJ

        double expectedA = EloCalculator.expectedScore(a.getEloScore(), b.getEloScore());
        double expectedB = 1.0 - expectedA;

        int sA = match.getScoreTeamA();
        int sB = match.getScoreTeamB();
        double actualA, actualB;
        if      (sA > sB) { actualA = 1.0; actualB = 0.0; }
        else if (sB > sA) { actualA = 0.0; actualB = 1.0; }
        else              { actualA = 0.5; actualB = 0.5; }

        // Mise à jour ELO
        a.setEloScore(EloCalculator.newElo(a.getEloScore(), expectedA, actualA, a.getLevel()));
        b.setEloScore(EloCalculator.newElo(b.getEloScore(), expectedB, actualB, b.getLevel()));

        // Mise à jour stats — champs exacts de ta classe Team
        a.setMatches(a.getMatches() + 1);
        b.setMatches(b.getMatches() + 1);
        if (sA > sB) {
            a.setVictories(a.getVictories() + 1);
            b.setDefeats(b.getDefeats() + 1);
        } else if (sB > sA) {
            b.setVictories(b.getVictories() + 1);
            a.setDefeats(a.getDefeats() + 1);
        }

        // Recalcul du Level (PROFESSIONAL, pas ELITE)
        a.setLevel(EloCalculator.computeLevel(a.getEloScore()));
        b.setLevel(EloCalculator.computeLevel(b.getEloScore()));

        match.setStatsUpdated(true);
        teamRepository.saveAll(List.of(a, b));
        matchRepository.save(match);
    }
}
