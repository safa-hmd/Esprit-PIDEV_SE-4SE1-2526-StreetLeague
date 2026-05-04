package com.example.streetleague.dto;

import com.example.streetleague.Entity.PaymentMethod;
import com.example.streetleague.Entity.PaymentStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentDto {

    private Long id;
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private Long reservationId;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private Long fieldId;
    private String fieldName;
    private String fieldLocation;
    private Long playerId;
    private String playerName;

    public PaymentDto() {
    }

    public PaymentDto(Long id, Double amount, PaymentMethod method, PaymentStatus status, String transactionRef, LocalDateTime createdAt, LocalDateTime paidAt, LocalDateTime refundedAt, Long reservationId, LocalDateTime slotStart, LocalDateTime slotEnd, Long fieldId, String fieldName, String fieldLocation, Long playerId, String playerName) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionRef = transactionRef;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.refundedAt = refundedAt;
        this.reservationId = reservationId;
        this.slotStart = slotStart;
        this.slotEnd = slotEnd;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public LocalDateTime getSlotStart() { return slotStart; }
    public void setSlotStart(LocalDateTime slotStart) { this.slotStart = slotStart; }

    public LocalDateTime getSlotEnd() { return slotEnd; }
    public void setSlotEnd(LocalDateTime slotEnd) { this.slotEnd = slotEnd; }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDto that = (PaymentDto) o;
        return Objects.equals(id, that.id) && Objects.equals(amount, that.amount) && method == that.method && status == that.status && Objects.equals(transactionRef, that.transactionRef) && Objects.equals(createdAt, that.createdAt) && Objects.equals(paidAt, that.paidAt) && Objects.equals(refundedAt, that.refundedAt) && Objects.equals(reservationId, that.reservationId) && Objects.equals(slotStart, that.slotStart) && Objects.equals(slotEnd, that.slotEnd) && Objects.equals(fieldId, that.fieldId) && Objects.equals(fieldName, that.fieldName) && Objects.equals(fieldLocation, that.fieldLocation) && Objects.equals(playerId, that.playerId) && Objects.equals(playerName, that.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, method, status, transactionRef, createdAt, paidAt, refundedAt, reservationId, slotStart, slotEnd, fieldId, fieldName, fieldLocation, playerId, playerName);
    }

    @Override
    public String toString() {
        return "PaymentDto{" +
                "id=" + id +
                ", amount=" + amount +
                ", status=" + status +
                ", transactionRef='" + transactionRef + '\'' +
                '}';
    }

    public static PaymentDtoBuilder builder() {
        return new PaymentDtoBuilder();
    }

    public static class PaymentDtoBuilder {
        private Long id;
        private Double amount;
        private PaymentMethod method;
        private PaymentStatus status;
        private String transactionRef;
        private LocalDateTime createdAt;
        private LocalDateTime paidAt;
        private LocalDateTime refundedAt;
        private Long reservationId;
        private LocalDateTime slotStart;
        private LocalDateTime slotEnd;
        private Long fieldId;
        private String fieldName;
        private String fieldLocation;
        private Long playerId;
        private String playerName;

        public PaymentDtoBuilder id(Long id) { this.id = id; return this; }
        public PaymentDtoBuilder amount(Double amount) { this.amount = amount; return this; }
        public PaymentDtoBuilder method(PaymentMethod method) { this.method = method; return this; }
        public PaymentDtoBuilder status(PaymentStatus status) { this.status = status; return this; }
        public PaymentDtoBuilder transactionRef(String transactionRef) { this.transactionRef = transactionRef; return this; }
        public PaymentDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaymentDtoBuilder paidAt(LocalDateTime paidAt) { this.paidAt = paidAt; return this; }
        public PaymentDtoBuilder refundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; return this; }
        public PaymentDtoBuilder reservationId(Long reservationId) { this.reservationId = reservationId; return this; }
        public PaymentDtoBuilder slotStart(LocalDateTime slotStart) { this.slotStart = slotStart; return this; }
        public PaymentDtoBuilder slotEnd(LocalDateTime slotEnd) { this.slotEnd = slotEnd; return this; }
        public PaymentDtoBuilder fieldId(Long fieldId) { this.fieldId = fieldId; return this; }
        public PaymentDtoBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public PaymentDtoBuilder fieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; return this; }
        public PaymentDtoBuilder playerId(Long playerId) { this.playerId = playerId; return this; }
        public PaymentDtoBuilder playerName(String playerName) { this.playerName = playerName; return this; }

        public PaymentDto build() {
            return new PaymentDto(id, amount, method, status, transactionRef, createdAt, paidAt, refundedAt, reservationId, slotStart, slotEnd, fieldId, fieldName, fieldLocation, playerId, playerName);
        }
    }
}