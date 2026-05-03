package com.example.streetleague.Repository;

import com.example.streetleague.Entity.TournamentMatch;
import com.example.streetleague.Entity.TournamentMatchStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {

    List<TournamentMatch> findByTournamentIdOrderByRoundAscPositionAsc(Long tournamentId);

    List<TournamentMatch> findByTournamentIdAndRound(Long tournamentId, int round);

    boolean existsByTournamentId(Long tournamentId);

    @Modifying
    @Transactional
    @Query("UPDATE TournamentMatch tm SET tm.nextMatch = null WHERE tm.tournament.id = :tournamentId")
    void clearNextMatchLinks(@Param("tournamentId") Long tournamentId);

    void deleteByTournamentId(Long tournamentId);

    @Query("SELECT tm FROM TournamentMatch tm WHERE tm.nextMatch.id = :nextMatchId")
    List<TournamentMatch> findFeederMatches(@Param("nextMatchId") Long nextMatchId);

    @Query("SELECT COUNT(tm) FROM TournamentMatch tm WHERE tm.tournament.id = :tId AND tm.status <> :status")
    long countNotInStatus(@Param("tId") Long tournamentId, @Param("status") TournamentMatchStatus status);

    // ✅ AJOUT : mettre match à null avant suppression du match
    @Modifying
    @Transactional
    @Query("UPDATE TournamentMatch tm SET tm.match = null WHERE tm.match.id = :matchId")
    void clearMatchLink(@Param("matchId") Long matchId);

    @Modifying
    @Transactional
    @Query("UPDATE TournamentMatch tm SET tm.match = null WHERE tm.match.id IN " +
            "(SELECT m.idMatch FROM Match m WHERE m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)")
    void clearMatchLinkByTeamId(@Param("teamId") Long teamId);
}