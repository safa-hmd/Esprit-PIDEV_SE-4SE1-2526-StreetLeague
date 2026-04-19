package com.example.streetleague.Repository;

import com.example.streetleague.Entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    Optional<UserGoal> findByUserIdAndGoalType(Long userId, String goalType);


    List<UserGoal> findByUserId(Long userId);
}
