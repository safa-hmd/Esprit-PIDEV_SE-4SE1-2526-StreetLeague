package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Match m WHERE m.teamA.idTeam = :teamId1 OR m.teamB.idTeam = :teamId2")
    void deleteByTeamAIdOrTeamBId(
            @Param("teamId1") Long teamId1,
            @Param("teamId2") Long teamId2
    );

    @Query("DELETE FROM Match m WHERE m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId")
    void deleteByTeamAIdOrTeamBId(@Param("teamId") Long teamId);

    List<Match> findByTeamA_IdTeamOrTeamB_IdTeam(Long teamAId, Long teamBId);

    List<Match> findByTeamA_IdTeam(Long teamId);

    List<Match> findByTeamB_IdTeam(Long teamId);
}