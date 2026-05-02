package com.example.streetleague.Repository;

import com.example.streetleague.Entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    @Query("SELECT g FROM UserGoal g WHERE g.user.idUser = :userId AND g.goalType = :goalType")
    Optional<UserGoal> findByUserIdAndGoalType(@Param("userId") Long userId, @Param("goalType") String goalType);

    @Query("SELECT g FROM UserGoal g WHERE g.user.idUser = :userId")
    List<UserGoal> findByUserId(@Param("userId") Long userId);
}
