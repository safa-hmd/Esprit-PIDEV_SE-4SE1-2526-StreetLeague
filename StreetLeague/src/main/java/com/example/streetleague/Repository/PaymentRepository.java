package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Payment;
import com.example.streetleague.Entity.PaymentStatus;
import com.example.streetleague.dto.RevenueByFieldDto;
import com.example.streetleague.dto.RevenueByMonthDto;
import com.example.streetleague.dto.RevenueBySportDto;
import com.example.streetleague.dto.TopPlayerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ── Par réservation ───────────────────────────────────────────
    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.reservation r
        JOIN FETCH r.field f
        JOIN FETCH p.player u
        WHERE r.id = :reservationId
    """)
    Optional<Payment> findByReservationId(@Param("reservationId") Long reservationId);

    // ── Par joueur ────────────────────────────────────────────────
    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.reservation r
        JOIN FETCH r.field f
        JOIN FETCH p.player u
        WHERE u.idUser = :playerId
        ORDER BY p.createdAt DESC
    """)
    List<Payment> findByPlayerId(@Param("playerId") Long playerId);

    // ── Tous les paiements (admin) ────────────────────────────────
    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.reservation r
        JOIN FETCH r.field f
        JOIN FETCH p.player u
        ORDER BY p.createdAt DESC
    """)
    List<Payment> findAllWithDetails();

    // ── Par statut ────────────────────────────────────────────────
    List<Payment> findByStatus(PaymentStatus status);

    // ── Vérifier si paiement existe pour une réservation ─────────
    boolean existsByReservationId(Long reservationId);


    //***************************ejjaw mta3 el dashboard ********************
    // ── Summary ──────────────────────────────────────────────

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = com.example.streetleague.Entity.PaymentStatus.PAID")
    Double sumPaidRevenue();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    // ── Revenue by Field ─────────────────────────────────────
    @Query("""
    SELECT new com.example.streetleague.dto.RevenueByFieldDto(
        f.name, SUM(p.amount), COUNT(p)
    )
    FROM Payment p
    JOIN p.reservation r
    JOIN r.field f
    WHERE p.status = 'PAID'
    GROUP BY f.name
""")
    List<RevenueByFieldDto> revenueByField();

    // ── Revenue by Sport ─────────────────────────────────────
    @Query("""
    SELECT new com.example.streetleague.dto.RevenueBySportDto(
        CAST(f.sportType AS string), SUM(p.amount)
    )
    FROM Payment p
    JOIN p.reservation r
    JOIN r.field f
    WHERE p.status = 'PAID'
    GROUP BY f.sportType
""")
    List<RevenueBySportDto> revenueBySport();

    // ── Revenue by Month ─────────────────────────────────────
    @Query("""
    SELECT new com.example.streetleague.dto.RevenueByMonthDto(
        CONCAT(YEAR(p.paidAt), '-', LPAD(CAST(MONTH(p.paidAt) AS string), 2, '0')),
        SUM(p.amount)
    )
    FROM Payment p
    WHERE p.status = 'PAID'
    GROUP BY YEAR(p.paidAt), MONTH(p.paidAt)
    ORDER BY YEAR(p.paidAt), MONTH(p.paidAt)
""")
    List<RevenueByMonthDto> revenueByMonth();

    // ── Top 5 Players ─────────────────────────────────────────
    @Query("""
    SELECT new com.example.streetleague.dto.TopPlayerDto(
        u.fullName, u.email, SUM(p.amount), COUNT(p)
    )
    FROM Payment p
    JOIN p.player u
    WHERE p.status = 'PAID'
    GROUP BY u.idUser, u.fullName, u.email
    ORDER BY SUM(p.amount) DESC
    LIMIT 5
""")
    List<TopPlayerDto> topPlayers();

    @Query("""
    SELECT p FROM Payment p
    JOIN FETCH p.reservation r
    JOIN FETCH p.player u
    WHERE p.status = :status
    AND p.createdAt < :cutoff
""")
    List<Payment> findPendingOlderThan(
            @Param("status") PaymentStatus status,
            @Param("cutoff") LocalDateTime cutoff
    );
}