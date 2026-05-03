package com.example.streetleague.Repository;

import com.example.streetleague.Entity.DailyWaterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyWaterLogRepository extends JpaRepository<DailyWaterLog, Long> {
    @Query("SELECT d FROM DailyWaterLog d WHERE d.user.idUser = :userId AND d.date = :date")
    Optional<DailyWaterLog> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT d FROM DailyWaterLog d WHERE d.user.idUser = :userId AND d.date BETWEEN :start AND :end")
    List<DailyWaterLog> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(d) FROM DailyWaterLog d WHERE d.user.idUser = :userId AND d.date BETWEEN :start AND :end AND d.goalReached = true")
    long countByUserIdAndDateBetweenAndGoalReachedTrue(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
