package com.example.streetleague.dto;

import com.example.streetleague.Entity.PaymentMethod;
import com.example.streetleague.Entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long id;

    // ── Paiement ──────────────────────────────────────────────────
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionRef;

    // ── Dates ─────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;

    // ── Réservation concernée ─────────────────────────────────────
    private Long reservationId;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;

    // ── Terrain ───────────────────────────────────────────────────
    private Long fieldId;
    private String fieldName;
    private String fieldLocation;

    // ── Joueur ────────────────────────────────────────────────────
    private Long playerId;
    private String playerName;
}