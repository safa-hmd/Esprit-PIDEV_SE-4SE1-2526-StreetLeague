package com.example.streetleague.Repository;


import com.example.streetleague.domain.LivreurStatus;

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

    
    @Query(value = "SELECT tp.team_id FROM team_players tp WHERE tp.user_id = :userId LIMIT 1", nativeQuery = true)
    Long findTeamIdByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN team_players tp ON u.id = tp.user_id " +
            "WHERE tp.team_id = :teamId",
            nativeQuery = true)
    List<User> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT u FROM User u JOIN u.teams t WHERE t.idTeam = :teamId AND u.role = :role")
    List<User> findByTeamIdAndRole(@Param("teamId") Long teamId, @Param("role") Role role);


    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findAllByRole(@Param("role") Role role);

    // Tous les livreurs
    List<User> findByRole(Role role);

    // Livreurs par statut
    List<User> findByRoleAndStatusLivreur(Role role, LivreurStatus status);

    @Query("""
SELECT COUNT(u) FROM User u
WHERE u.role = :role
  AND u.statusLivreur IN :statuses
""")
    int countLivreursActifs(@Param("role") Role role,
                            @Param("statuses") List<LivreurStatus> statuses);


}