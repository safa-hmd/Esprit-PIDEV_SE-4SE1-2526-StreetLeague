package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "field_reservations")
public class FieldReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private Double totalPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;


    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReservationStatus.PENDING;
        }
        computeTotalPrice();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        computeTotalPrice();
    }

    private void computeTotalPrice() {
        if (field != null && field.getPricePerHour() != null && startTime != null && endTime != null) {
            long hours = Duration.between(startTime, endTime).toHours();
            this.totalPrice = hours * field.getPricePerHour();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public FieldReservation() {

    }

    public FieldReservation(Long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, Double totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt, Field field, User player, Payment payment) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.field = field;
        this.player = player;
        this.payment = payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldReservation that = (FieldReservation) o;
        return Objects.equals(id, that.id) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && status == that.status && Objects.equals(totalPrice, that.totalPrice) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(field, that.field) && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, status, totalPrice, createdAt, updatedAt, field, player);
    }

    @Override
    public String toString() {
        return "FieldReservation{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", field=" + field +
                ", player=" + player +
                '}';
    }

    public static FieldReservationBuilder builder() {
        return new FieldReservationBuilder();
    }

    public static class FieldReservationBuilder {
        private Long id;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private ReservationStatus status;
        private Double totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Field field;
        private User player;
        private Payment payment;

        public FieldReservationBuilder id(Long id) { this.id = id; return this; }
        public FieldReservationBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public FieldReservationBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public FieldReservationBuilder status(ReservationStatus status) { this.status = status; return this; }
        public FieldReservationBuilder totalPrice(Double totalPrice) { this.totalPrice = totalPrice; return this; }
        public FieldReservationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public FieldReservationBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public FieldReservationBuilder field(Field field) { this.field = field; return this; }
        public FieldReservationBuilder player(User player) { this.player = player; return this; }
        public FieldReservationBuilder payment(Payment payment) { this.payment = payment; return this; }

        public FieldReservation build() {
            return new FieldReservation(id, startTime, endTime, status, totalPrice, createdAt, updatedAt, field, player, payment);
        }
    }
}

