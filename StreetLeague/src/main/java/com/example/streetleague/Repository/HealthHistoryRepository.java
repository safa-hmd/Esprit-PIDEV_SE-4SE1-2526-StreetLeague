package com.example.streetleague.Repository;

import com.example.streetleague.Entity.HealthHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HealthHistoryRepository extends JpaRepository<HealthHistory, Long> {
    @Query("SELECT h FROM HealthHistory h WHERE h.user.idUser = :userId ORDER BY h.date DESC")
    List<HealthHistory> findByUserId(@Param("userId") Long userId);

    @Query("SELECT h FROM HealthHistory h WHERE h.user.idUser = :userId AND h.date BETWEEN :start AND :end ORDER BY h.date DESC")
    List<HealthHistory> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT h FROM HealthHistory h WHERE h.user.idUser = :userId ORDER BY h.date DESC LIMIT 1")
    HealthHistory findTopByUserId(@Param("userId") Long userId);
}