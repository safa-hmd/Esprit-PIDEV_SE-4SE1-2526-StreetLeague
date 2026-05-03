package com.example.streetleague.Repository;

import com.example.streetleague.Entity.waterReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaterReminderRepository extends JpaRepository<waterReminder,Long> {
    @Query("SELECT w FROM waterReminder w WHERE w.active = true")
    List<waterReminder> findByActiveTrue();

    @Query("SELECT w FROM waterReminder w WHERE w.user.idUser = :userId")
    Optional<waterReminder> findByUserId(@Param("userId") Long userId);
}
