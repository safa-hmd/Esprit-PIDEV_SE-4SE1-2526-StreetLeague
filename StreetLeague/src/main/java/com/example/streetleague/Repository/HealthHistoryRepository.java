package com.example.streetleague.Repository;

import com.example.streetleague.Entity.HealthHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthHistoryRepository extends JpaRepository<HealthHistory, Long> {
    List<HealthHistory> findByUserIdOrderByDateDesc(Long userId);


    // Keyword 1 : HealthHistory + User + date range (2 tables)
    List<HealthHistory> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate start, LocalDate end
    );

    // Keyword 2 : dernier enregistrement d'un user
    HealthHistory findTopByUserIdOrderByDateDesc(Long userId);
}