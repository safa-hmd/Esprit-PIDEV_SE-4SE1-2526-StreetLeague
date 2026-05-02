package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Match;

import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.MatchHistoryDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface MatchRepository extends JpaRepository<Match, Long> {


    // ✅ Une seule méthode, un seul paramètre
    @Modifying
    @Transactional
    @Query("DELETE FROM Match m WHERE m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId")
    void deleteByTeamAIdOrTeamBId(@Param("teamId") Long teamId);

    List<Match> findByTeamA_IdTeamOrTeamB_IdTeam(Long teamAId, Long teamBId);

    List<Match> findByTeamA_IdTeam(Long teamId);

    List<Match> findByTeamB_IdTeam(Long teamId);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Match m WHERE m.teamA.idTeam = :teamAId OR m.teamB.idTeam = :teamBId")
//    void deleteByTeamAIdOrTeamBId(
//            @Param("teamAId") Long teamAId,
//            @Param("teamBId") Long teamBId);

    @Query("""
        SELECT DISTINCT m FROM Match m
        JOIN FETCH m.teamA tA
        JOIN FETCH m.teamB tB
        JOIN FETCH tA.players pA
        LEFT JOIN FETCH tB.players pB
        WHERE m.status = com.example.streetleague.Entity.MatchStatus.ACCEPTED
          AND m.matchDate BETWEEN :from AND :to
    """)
    List<Match> findUpcomingMatchesWithTeamPlayers(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    @Query("""
        SELECT new com.example.streetleague.dto.MatchHistoryDto(
            m.idMatch, m.matchDate, m.location, m.status,
            m.scoreTeamA, m.scoreTeamB,
            tA.name, tB.name, cA.fullName, cB.fullName,
            tA.sport, tA.eloScore, tB.eloScore)
        FROM Match m
        LEFT JOIN m.teamA tA LEFT JOIN m.teamB tB
        LEFT JOIN tA.captain cA LEFT JOIN tB.captain cB
        WHERE (:status IS NULL OR m.status = :status)
          AND (:from IS NULL OR m.matchDate >= :from)
          AND (:to   IS NULL OR m.matchDate <= :to)
        ORDER BY m.matchDate DESC
    """)
    List<MatchHistoryDto> findMatchHistoryEnriched(
            @Param("status") MatchStatus status,
            @Param("from")   LocalDateTime from,
            @Param("to")     LocalDateTime to);

    @Query("""
        SELECT m FROM Match m JOIN m.teamA tA JOIN m.teamB tB
        WHERE (LOWER(tA.name) LIKE LOWER(:pattern)
            OR LOWER(tB.name) LIKE LOWER(:pattern))
          AND m.status IN :statuses
        ORDER BY m.matchDate DESC
    """)
    List<Match> findMatchesByTeamAndStatus(
            @Param("pattern")  String pattern,
            @Param("statuses") List<MatchStatus> statuses);

    List<Match> findByTeamA_NameContainingIgnoreCaseOrTeamB_NameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrderByMatchDateDesc(
            String keywordForTeamA, String keywordForTeamB, String keywordForLocation);

    @Query("""
        SELECT DISTINCT m FROM Match m
        JOIN FETCH m.teamA tA JOIN FETCH m.teamB tB
        JOIN FETCH tA.players pA LEFT JOIN FETCH tB.players pB
        LEFT JOIN FETCH tA.captain cA LEFT JOIN FETCH tB.captain cB
        WHERE m.status = com.example.streetleague.Entity.MatchStatus.ACCEPTED
          AND m.matchDate BETWEEN :from AND :to
    """)
    List<Match> findMatchesClosingSoonWithTeams(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    List<Match> findByStatusAndMatchDateBetweenAndTeamA_NameContainingIgnoreCaseOrStatusAndMatchDateBetweenAndTeamB_NameContainingIgnoreCase(
            MatchStatus statusA, LocalDateTime fromA, LocalDateTime toA, String teamAName,
            MatchStatus statusB, LocalDateTime fromB, LocalDateTime toB, String teamBName);

    @Query("""
        SELECT m FROM Match m
        WHERE (m.teamA = :team OR m.teamB = :team)
        AND m.matchDate BETWEEN :from AND :to
    """)
    List<Match> findByTeamAndDateBetween(
            @Param("team") Team team,
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    @Query("""
        SELECT m FROM Match m
        JOIN FETCH m.teamA tA JOIN FETCH m.teamB tB
        LEFT JOIN FETCH tA.players LEFT JOIN FETCH tB.players
        LEFT JOIN FETCH tA.captain LEFT JOIN FETCH tB.captain
        WHERE m.idMatch = :id
    """)
    Optional<Match> findByIdWithTeams(@Param("id") Long id);

    // ✅ NOUVEAU — trouve les matchs d'un utilisateur dans une période
    // Utilisé par ScheduleController pour construire le calendrier
    @Query("""
        SELECT DISTINCT m FROM Match m
        JOIN FETCH m.teamA tA
        JOIN FETCH m.teamB tB
        LEFT JOIN tA.players pA
        LEFT JOIN tB.players pB
        WHERE m.matchDate BETWEEN :from AND :to
          AND (pA = :user OR pB = :user OR m.createdBy = :user)
    """)
    List<Match> findByUserAndDateBetween(
            @Param("user") User user,
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    @Query("""
        SELECT m.location FROM Match m
        WHERE m.status = 'FINISHED'
          AND (m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)
          AND m.location IS NOT NULL
        ORDER BY m.matchDate DESC
        LIMIT 1
    """)
    Optional<String> findLastLocationByTeam(@Param("teamId") Long teamId);

    @Query("""
        SELECT COUNT(m) FROM Match m 
        WHERE (m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)
          AND m.status = 'FINISHED'
          AND ((m.teamA.idTeam = :teamId AND m.scoreTeamA > m.scoreTeamB)
            OR (m.teamB.idTeam = :teamId AND m.scoreTeamB > m.scoreTeamA))
    """)
    Integer findTotalWins(@Param("teamId") Long teamId);

    @Query("""
        SELECT COUNT(m) FROM Match m 
        WHERE (m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)
          AND m.status = 'FINISHED'
          AND ((m.teamA.idTeam = :teamId AND m.scoreTeamA < m.scoreTeamB)
            OR (m.teamB.idTeam = :teamId AND m.scoreTeamB < m.scoreTeamA))
    """)
    Integer findTotalLosses(@Param("teamId") Long teamId);

    @Query("""
        SELECT COUNT(m) FROM Match m 
        WHERE (m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)
          AND m.status = 'FINISHED'
    """)
    Integer findTotalMatches(@Param("teamId") Long teamId);

    @Query("""
        SELECT m FROM Match m
        WHERE m.status = 'FINISHED'
          AND ((m.teamA.idTeam = :idA AND m.teamB.idTeam = :idB)
            OR (m.teamA.idTeam = :idB AND m.teamB.idTeam = :idA))
        ORDER BY m.matchDate DESC
    """)
    List<Match> findFinishedMatchesBetween(
            @Param("idA") Long idA,
            @Param("idB") Long idB);

    @Query("""
        SELECT COUNT(m) FROM Match m 
        WHERE (m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)
          AND m.status = 'FINISHED'
          AND ((m.teamA.idTeam = :teamId AND m.scoreTeamA > m.scoreTeamB)
            OR (m.teamB.idTeam = :teamId AND m.scoreTeamB > m.scoreTeamA))
          AND m.matchDate > COALESCE(
              (SELECT MAX(m2.matchDate) FROM Match m2 
               WHERE (m2.teamA.idTeam = :teamId OR m2.teamB.idTeam = :teamId)
                 AND m2.status = 'FINISHED'
                 AND ((m2.teamA.idTeam = :teamId AND m2.scoreTeamA < m2.scoreTeamB)
                   OR (m2.teamB.idTeam = :teamId AND m2.scoreTeamB < m2.scoreTeamA))),
              '1900-01-01')
    """)
    Integer findCurrentWinStreak(@Param("teamId") Long teamId);

    @Query("""
        SELECT COUNT(m) FROM Match m 
        WHERE (m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId)
          AND m.status = 'FINISHED'
          AND ((m.teamA.idTeam = :teamId AND m.scoreTeamA >= m.scoreTeamB)
            OR (m.teamB.idTeam = :teamId AND m.scoreTeamB >= m.scoreTeamA))
          AND m.matchDate > COALESCE(
              (SELECT MAX(m2.matchDate) FROM Match m2 
               WHERE (m2.teamA.idTeam = :teamId OR m2.teamB.idTeam = :teamId)
                 AND m2.status = 'FINISHED'
                 AND ((m2.teamA.idTeam = :teamId AND m2.scoreTeamA < m2.scoreTeamB)
                   OR (m2.teamB.idTeam = :teamId AND m2.scoreTeamB < m2.scoreTeamA))),
              '1900-01-01')
    """)
    Integer findCurrentUnbeatenStreak(@Param("teamId") Long teamId);
    
    @Query("SELECT m FROM Match m WHERE m.teamA IS NOT NULL AND m.teamB IS NOT NULL AND m.createdBy IS NOT NULL")
    List<Match> findAllComplete();

}