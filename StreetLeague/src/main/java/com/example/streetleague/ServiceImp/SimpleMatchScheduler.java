//// SimpleMatchScheduler.java - Version qui s'exécute à 7h00
//package com.example.streetleague.ServiceImp;
//
//import com.example.streetleague.Entity.Match;
//import com.example.streetleague.Entity.MatchStatus;
//import com.example.streetleague.Entity.Team;
//import com.example.streetleague.Repository.MatchRepository;
//import com.example.streetleague.Repository.TeamRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SimpleMatchScheduler {
//
//    private final MatchRepository matchRepository;
//    private final TeamRepository teamRepository;
//
//    //  S'exécute tous les jours à 07:00:00
//    @Scheduled(cron = "0 0 7 * * *")
//    @Transactional
//    public void updateFinishedMatches() {
//        log.info("=== DEBUT SCHEDULER (07:00) ===");
//
//        // 1. Récupérer les matchs FINISHED non traités
//        List<Match> matches = matchRepository.findByStatus(MatchStatus.FINISHED);
//
//        log.info("Matchs trouvés: {}", matches.size());
//
//        int processedCount = 0;
//
//        // 2. Pour chaque match, mettre à jour les stats des équipes
//        for (Match match : matches) {
//            if (!match.isStatsUpdated()) {
//
//                Team teamA = match.getTeamA();
//                Team teamB = match.getTeamB();
//
//                // Augmenter le nombre de matchs joués
//                teamA.setMatches(teamA.getMatches() + 1);
//                teamB.setMatches(teamB.getMatches() + 1);
//
//                // Voir qui a gagné
//                if (match.getScoreTeamA() > match.getScoreTeamB()) {
//                    teamA.setVictories(teamA.getVictories() + 1);
//                    teamB.setDefeats(teamB.getDefeats() + 1);
//                    log.info("🏆 {} a gagné", teamA.getName());
//
//                } else if (match.getScoreTeamB() > match.getScoreTeamA()) {
//                    teamB.setVictories(teamB.getVictories() + 1);
//                    teamA.setDefeats(teamA.getDefeats() + 1);
//                    log.info("🏆 {} a gagné", teamB.getName());
//
//                } else {
//                    log.info(" Match nul entre {} et {}", teamA.getName(), teamB.getName());
//                }
//
//                // Sauvegarder
//                teamRepository.save(teamA);
//                teamRepository.save(teamB);
//
//                match.setStatsUpdated(true);
//                matchRepository.save(match);
//
//                processedCount++;
//                log.info(" Match {} mis à jour", match.getIdMatch());
//            }
//        }
//
//        log.info("=== FIN SCHEDULER - {} matchs traités à 07:00 ===", processedCount);
//    }
//}