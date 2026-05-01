package com.example.streetleague.Repository;

import com.example.streetleague.Entity.WaterStreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaterStreakRepository extends JpaRepository<WaterStreak, Long> {
    Optional<WaterStreak> findByUserId(Long userId);
}