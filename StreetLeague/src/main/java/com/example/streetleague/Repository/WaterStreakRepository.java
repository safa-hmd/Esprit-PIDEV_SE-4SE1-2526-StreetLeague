package com.example.streetleague.Repository;

import com.example.streetleague.Entity.WaterStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WaterStreakRepository extends JpaRepository<WaterStreak, Long> {
    @Query("SELECT w FROM WaterStreak w WHERE w.user.idUser = :userId")
    Optional<WaterStreak> findByUserId(@Param("userId") Long userId);}