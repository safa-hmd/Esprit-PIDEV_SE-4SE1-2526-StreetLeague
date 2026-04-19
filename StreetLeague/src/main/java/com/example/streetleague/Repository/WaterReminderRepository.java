package com.example.streetleague.Repository;

import com.example.streetleague.Entity.waterReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterReminderRepository extends JpaRepository<waterReminder,Long> {
    List<waterReminder> findByActiveTrue();
}
