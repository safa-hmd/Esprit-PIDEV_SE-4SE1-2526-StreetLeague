package com.example.streetleague.Repository;


import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    //  Pour le planning — toutes les réservations d'un terrain sur une période
    @Query("""
    SELECT r FROM FieldReservation r
    JOIN FETCH r.field f
    JOIN FETCH r.player p
    WHERE f.id = :fieldId
    AND r.startTime >= :from
    AND r.startTime <  :to
""")
    List<FieldReservation> findScheduleByFieldAndPeriod(
            @Param("fieldId") Long fieldId,
            @Param("from")    LocalDateTime from,
            @Param("to")      LocalDateTime to
    );
}
