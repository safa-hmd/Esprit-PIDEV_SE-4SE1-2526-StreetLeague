package com.example.streetleague.Repository;


import com.example.streetleague.Entity.RegistrationStatus;
import com.example.streetleague.Entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {

    List<TournamentRegistration> findByTournamentId(Long tournamentId);

    List<TournamentRegistration> findByPlayerIdUser(Long playerId);

    List<TournamentRegistration> findByTeamIdTeam(Long teamId);

   // Optional<TournamentRegistration> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);

    //Optional<TournamentRegistration> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    long countByTournamentIdAndStatus(Long tournamentId, RegistrationStatus status);

    boolean existsByTournamentIdAndPlayerIdUser(Long tournamentId, Long playerId);

    boolean existsByTournamentIdAndTeamIdTeam(Long tournamentId, Long teamId);

    @Query("""
    SELECT r FROM TournamentRegistration r
    LEFT JOIN FETCH r.team t
    LEFT JOIN FETCH t.captain
    LEFT JOIN FETCH r.player
    WHERE r.tournament.id = :tournamentId
    AND r.status = :status
""")
    List<TournamentRegistration> findByTournamentIdAndStatus(
            @Param("tournamentId") Long tournamentId,
            @Param("status") RegistrationStatus status
    );
    @Query("""
    SELECT r FROM TournamentRegistration r 
    JOIN r.team t 
    WHERE t.captain.idUser = :playerId 
    OR EXISTS (
        SELECT p FROM t.players p WHERE p.idUser = :playerId
    )
""")
    List<TournamentRegistration> findTeamRegistrationsByPlayerId(@Param("playerId") Long playerId);

    List<TournamentRegistration> findByTournamentIdAndStatusIn(Long tournamentId, List<RegistrationStatus> statuses);}
