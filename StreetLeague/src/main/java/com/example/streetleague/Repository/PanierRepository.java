package com.example.streetleague.Repository;

import com.example.streetleague.domain.Panier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUserId(Long userId);
    @Query("SELECT p FROM Panier p WHERE p.updatedAt < :cutoffDate")
    List<Panier> findByUpdatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}