package com.example.streetleague.Repository;

import com.example.streetleague.Entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    @Query("SELECT b FROM UserBadge b WHERE b.user.idUser = :userId")
    List<UserBadge> findByUserId(@Param("userId") Long userId);
}
