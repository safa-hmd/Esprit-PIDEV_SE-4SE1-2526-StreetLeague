package com.example.streetleague.Repository;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.Entity.TournamentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByStatus(TournamentStatus status);

    List<Tournament> findBySportType(SportType sportType);

    List<Tournament> findByTournamentType(TournamentType tournamentType);

    List<Tournament> findByStatusAndSportType(TournamentStatus status, SportType sportType);

    boolean existsByNameIgnoreCase(String name);
}
