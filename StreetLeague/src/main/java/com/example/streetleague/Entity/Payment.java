package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payments")
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

    public Payment() {
    }

    public Payment(Long id, Double amount, PaymentMethod method, PaymentStatus status, LocalDateTime createdAt, LocalDateTime paidAt, LocalDateTime refundedAt, String transactionRef, FieldReservation reservation, User player) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.refundedAt = refundedAt;
        this.transactionRef = transactionRef;
        this.reservation = reservation;
        this.player = player;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public FieldReservation getReservation() {
        return reservation;
    }

    public void setReservation(FieldReservation reservation) {
        this.reservation = reservation;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) && Objects.equals(amount, payment.amount) && method == payment.method && status == payment.status && Objects.equals(createdAt, payment.createdAt) && Objects.equals(paidAt, payment.paidAt) && Objects.equals(refundedAt, payment.refundedAt) && Objects.equals(transactionRef, payment.transactionRef) && Objects.equals(reservation, payment.reservation) && Objects.equals(player, payment.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, method, status, createdAt, paidAt, refundedAt, transactionRef, reservation, player);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", method=" + method +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", refundedAt=" + refundedAt +
                ", transactionRef='" + transactionRef + '\'' +
                ", reservation=" + reservation +
                ", player=" + player +
                '}';
    }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private Long id;
        private Double amount;
        private PaymentMethod method;
        private PaymentStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime paidAt;
        private LocalDateTime refundedAt;
        private String transactionRef;
        private FieldReservation reservation;
        private User player;

        public PaymentBuilder id(Long id) { this.id = id; return this; }
        public PaymentBuilder amount(Double amount) { this.amount = amount; return this; }
        public PaymentBuilder method(PaymentMethod method) { this.method = method; return this; }
        public PaymentBuilder status(PaymentStatus status) { this.status = status; return this; }
        public PaymentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaymentBuilder paidAt(LocalDateTime paidAt) { this.paidAt = paidAt; return this; }
        public PaymentBuilder refundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; return this; }
        public PaymentBuilder transactionRef(String transactionRef) { this.transactionRef = transactionRef; return this; }
        public PaymentBuilder reservation(FieldReservation reservation) { this.reservation = reservation; return this; }
        public PaymentBuilder player(User player) { this.player = player; return this; }

        public Payment build() {
            return new Payment(id, amount, method, status, createdAt, paidAt, refundedAt, transactionRef, reservation, player);
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }
}