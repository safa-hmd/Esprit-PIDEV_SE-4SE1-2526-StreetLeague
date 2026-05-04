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