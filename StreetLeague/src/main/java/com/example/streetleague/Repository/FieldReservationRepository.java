package com.example.streetleague.Repository;


import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FieldReservationRepository extends JpaRepository<FieldReservation, Long> {

    List<FieldReservation> findByPlayerIdUser(Long playerId);

    List<FieldReservation> findByFieldId(Long fieldId);

    List<FieldReservation> findByStatus(ReservationStatus status);

    List<FieldReservation> findByPlayerIdUserOrderByCreatedAtDesc(Long playerId);

    // Vérifier les conflits de créneau pour un terrain
    @Query("""
        SELECT r FROM FieldReservation r
        WHERE r.field.id = :fieldId
          AND r.status NOT IN ('CANCELLED', 'REJECTED')
          AND r.startTime < :endTime
          AND r.endTime > :startTime
    """)
    List<FieldReservation> findConflictingReservations(
            @Param("fieldId") Long fieldId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
