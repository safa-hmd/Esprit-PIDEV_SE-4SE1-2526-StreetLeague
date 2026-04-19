package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Team;
import com.example.streetleague.dto.LeaderboardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("""
    SELECT new com.example.streetleague.dto.LeaderboardDto(
        t.idTeam,
        t.name,
        t.sport,
        c.fullName,
        c.email,
        t.victories,
        t.defeats,
        t.matches,
        (t.victories * 3 + t.matches - t.defeats),
        MAX(m.matchDate),
        CASE WHEN m.teamA.idTeam = t.idTeam THEN mB.name ELSE mA.name END
    )
    FROM Team t
    JOIN t.captain c
    LEFT JOIN Match m ON (m.teamA.idTeam = t.idTeam OR m.teamB.idTeam = t.idTeam)
        AND m.status = 'FINISHED'
    LEFT JOIN m.teamA mA
    LEFT JOIN m.teamB mB
    WHERE t.sport = :sport
    GROUP BY t.idTeam, t.name, t.sport, c.fullName, c.email,
             t.victories, t.defeats, t.matches, m.teamA.idTeam, mA.name, mB.name
    ORDER BY (t.victories * 3 + t.matches - t.defeats) DESC
""")
    List<LeaderboardDto> findLeaderboardBySport(@Param("sport") String sport);
}
