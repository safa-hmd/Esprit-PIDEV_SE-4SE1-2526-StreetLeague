package com.example.streetleague.Repository;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.Entity.TournamentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByStatus(TournamentStatus status);

    List<Tournament> findBySportType(SportType sportType);

    List<Tournament> findByTournamentType(TournamentType tournamentType);

    List<Tournament> findByStatusAndSportType(TournamentStatus status, SportType sportType);

    boolean existsByNameIgnoreCase(String name);

    //  Pour le planning — tournois liés à un terrain qui chevauchent la période
    @Query("""
    SELECT t FROM Tournament t
    JOIN FETCH t.field f
    WHERE f.id = :fieldId
    AND t.startDate <= :to
    AND t.endDate   >= :from
""")
    List<Tournament> findScheduleByFieldAndPeriod(
            @Param("fieldId") Long fieldId,
            @Param("from")    LocalDate from,
            @Param("to")      LocalDate to
    );

}
