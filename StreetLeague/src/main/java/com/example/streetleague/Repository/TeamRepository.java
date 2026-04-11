package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t WHERE t.sport = :sport ORDER BY ((t.victories * 3) + (t.matches) - (t.defeats)) DESC")
    List<Team> findTopTeamsBySport(@Param("sport") String sport);
}
