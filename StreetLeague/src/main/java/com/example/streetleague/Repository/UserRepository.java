package com.example.streetleague.Repository;

import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN team_players tp ON u.id = tp.user_id " +
            "WHERE tp.team_id = :teamId",
            nativeQuery = true)
    List<User> findByTeamId(@Param("teamId") Long teamId);

    List<User> findByTeamIdAndRole(Long teamId, Role role);
}