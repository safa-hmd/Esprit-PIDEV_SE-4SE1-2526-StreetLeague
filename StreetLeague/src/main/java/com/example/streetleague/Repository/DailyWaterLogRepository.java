package com.example.streetleague.Repository;

import com.example.streetleague.Entity.DailyWaterLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyWaterLogRepository extends JpaRepository<DailyWaterLog, Long> {
    Optional<DailyWaterLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<DailyWaterLog> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    long countByUserIdAndDateBetweenAndGoalReachedTrue(Long userId, LocalDate start, LocalDate end);
}
