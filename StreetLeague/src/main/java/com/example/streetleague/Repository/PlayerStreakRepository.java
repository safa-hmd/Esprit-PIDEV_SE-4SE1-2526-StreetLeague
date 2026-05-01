package com.example.streetleague.Repository;

import com.example.streetleague.Entity.PlayerStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStreakRepository extends JpaRepository<PlayerStreak, Long> {

    // Leaderboard global trié par points
    List<PlayerStreak> findAllByOrderByTotalPointsDesc();

    // Leaderboard trié par streak actuel (utilisé pour momentum)
    List<PlayerStreak> findAllByOrderByCurrentStreakDesc();


    @Query("SELECT ps FROM PlayerStreak ps WHERE ps.playerId IN " +
            "(SELECT u.idUser FROM User u JOIN u.teams t WHERE t.idTeam = :teamId) " +
            "ORDER BY ps.totalPoints DESC")
    List<PlayerStreak> getTeamLeaderboard(@Param("teamId") Long teamId);

    // Streak d'un joueur spécifique
    Optional<PlayerStreak> findByPlayerId(Long playerId);
}