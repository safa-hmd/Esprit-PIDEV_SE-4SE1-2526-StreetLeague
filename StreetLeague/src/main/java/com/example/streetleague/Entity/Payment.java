package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Montant ───────────────────────────────────────────────────
    @Column(nullable = false)
    private Double amount;

    // ── Méthode de paiement ───────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)  // null tant que joueur n'a pas choisi
    private PaymentMethod method;

    // ── Statut ────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // ── Dates ─────────────────────────────────────────────────────
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private LocalDateTime refundedAt;

    // ── Référence transaction (simulée) ───────────────────────────
    private String transactionRef;

    // ── Relations ─────────────────────────────────────────────────

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private FieldReservation reservation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    // ── Lifecycle ─────────────────────────────────────────────────

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }
}