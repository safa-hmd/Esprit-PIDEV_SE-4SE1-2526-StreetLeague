package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Match;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {


    @Modifying
    @Transactional
    @Query("DELETE FROM Match m WHERE m.teamA.idTeam = :teamId OR m.teamB.idTeam = :teamId")
    void deleteByTeamAIdOrTeamBId(@Param("teamId") Long teamId, @Param("teamId2") Long teamId2);
}
