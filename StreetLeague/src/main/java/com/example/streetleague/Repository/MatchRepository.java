package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.dto.MatchHistoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // ── Méthode existante ─────────────────────────────────────────────────
    @Transactional
    @Modifying
    @Query("DELETE FROM Match m WHERE m.teamA.idTeam = :teamAId OR m.teamB.idTeam = :teamBId")
    void deleteByTeamAIdOrTeamBId(
            @Param("teamAId") Long teamAId,
            @Param("teamBId") Long teamBId);

    // ═══════════════════════════════════════════════════════════════════════
    // MÉTHODE 0 — JPQL avancé : Match JOIN TeamA JOIN TeamB JOIN players
    // Trouve les matchs ACCEPTED dont la date approche dans [from, to]
    // Utilisé par le scheduler pour notifier tous les joueurs des deux équipes
    // ═══════════════════════════════════════════════════════════════════════
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
            @Param("to")   LocalDateTime to
    );

    // ═══════════════════════════════════════════════════════════════════════
    // MÉTHODE 1 — JPQL avec JOINS (Match + Team + User)
    // Historique enrichi filtré par statut et/ou période
    // ═══════════════════════════════════════════════════════════════════════
    @Query("""
        SELECT new com.example.streetleague.dto.MatchHistoryDto(
            m.idMatch,
            m.matchDate,
            m.location,
            m.status,
            m.scoreTeamA,
            m.scoreTeamB,
            tA.name,
            tB.name,
            cA.fullName,
            cB.fullName,
            tA.sport,
            tA.eloScore,
            tB.eloScore
        )
        FROM Match m
        LEFT JOIN m.teamA tA
        LEFT JOIN m.teamB tB
        LEFT JOIN tA.captain cA
        LEFT JOIN tB.captain cB
        WHERE (:status IS NULL OR m.status = :status)
          AND (:from IS NULL OR m.matchDate >= :from)
          AND (:to   IS NULL OR m.matchDate <= :to)
        ORDER BY m.matchDate DESC
    """)
    List<MatchHistoryDto> findMatchHistoryEnriched(
            @Param("status") MatchStatus status,
            @Param("from")   LocalDateTime from,
            @Param("to")     LocalDateTime to
    );

    // ═══════════════════════════════════════════════════════════════════════
    // MÉTHODE 2 — Keywords Spring Data (Match + Team — multi-table)
    // Recherche par nom d'équipe (LIKE) + liste de statuts
    // ═══════════════════════════════════════════════════════════════════════
    @Query("""
        SELECT m FROM Match m
        JOIN m.teamA tA
        JOIN m.teamB tB
        WHERE (LOWER(tA.name) LIKE LOWER(:pattern)
            OR LOWER(tB.name) LIKE LOWER(:pattern))
          AND m.status IN :statuses
        ORDER BY m.matchDate DESC
    """)
    List<Match> findMatchesByTeamAndStatus(
            @Param("pattern")  String pattern,
            @Param("statuses") List<MatchStatus> statuses
    );

    // ═══════════════════════════════════════════════════════════════════════
    // MÉTHODE 3 — ADVANCED KEYWORDS (Match + TeamA + TeamB) 
    // Recherche Complexe avec Keywords
    // ═══════════════════════════════════════════════════════════════════════
    List<Match> findByTeamA_NameContainingIgnoreCaseOrTeamB_NameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrderByMatchDateDesc(
            String keywordForTeamA, 
            String keywordForTeamB, 
            String keywordForLocation
    );


    // À ajouter dans MatchRepository.java

    // ═══════════════════════════════════════════════════════════════════════
// MÉTHODE 4 — JPQL avancé : Match JOIN TeamA JOIN TeamB JOIN players
// Trouve les matchs ACCEPTED dont la date est dans moins de X heures
// Utilisé par le scheduler de rappel "match bientôt fermé"
// ═══════════════════════════════════════════════════════════════════════
    @Query("""
    SELECT DISTINCT m FROM Match m
    JOIN FETCH m.teamA tA
    JOIN FETCH m.teamB tB
    JOIN FETCH tA.players pA
    LEFT JOIN FETCH tB.players pB
    LEFT JOIN FETCH tA.captain cA
    LEFT JOIN FETCH tB.captain cB
    WHERE m.status = com.example.streetleague.Entity.MatchStatus.ACCEPTED
      AND m.matchDate BETWEEN :from AND :to
""")
    List<Match> findMatchesClosingSoonWithTeams(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    // ═══════════════════════════════════════════════════════════════════════
// MÉTHODE 5 — Keywords Spring Data multi-table (Match + TeamA + TeamB)
// Trouve les matchs ACCEPTED par nom d'équipe dont la date approche
// Complément keyword-based à la méthode JPQL ci-dessus
// ═══════════════════════════════════════════════════════════════════════
    List<Match> findByStatusAndMatchDateBetweenAndTeamA_NameContainingIgnoreCaseOrStatusAndMatchDateBetweenAndTeamB_NameContainingIgnoreCase(
            MatchStatus statusA, LocalDateTime fromA, LocalDateTime toA, String teamAName,
            MatchStatus statusB, LocalDateTime fromB, LocalDateTime toB, String teamBName
    );


    // AJOUTER dans MatchRepository.java

    @Query("""
    SELECT m FROM Match m
    WHERE (m.teamA = :team OR m.teamB = :team)
    AND m.matchDate BETWEEN :from AND :to
""")
    List<Match> findByTeamAndDateBetween(
            @Param("team") Team team,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}