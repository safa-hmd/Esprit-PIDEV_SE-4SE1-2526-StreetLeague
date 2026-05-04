package com.example.streetleague.dto;

import com.example.streetleague.Entity.ReservationStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class FieldReservationDto {

    private Long id;

    @NotNull(message = "Field is required")
    private Long fieldId;

    private String fieldName;
    private String fieldLocation;

    @NotNull(message = "Player is required")
    private Long playerId;

    private String playerUsername;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;  // optionnel

    private ReservationStatus status;
    private Double totalPrice;

    @Size(max = 500, message = "Admin note cannot exceed 500 characters")
    private String adminNote;  // optionnel

    private LocalDateTime createdAt;

    public FieldReservationDto() {
    }

    public FieldReservationDto(Long id, Long fieldId, String fieldName, String fieldLocation, Long playerId, String playerUsername, LocalDateTime startTime, LocalDateTime endTime, String notes, ReservationStatus status, Double totalPrice, String adminNote, LocalDateTime createdAt) {
        this.id = id;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.fieldLocation = fieldLocation;
        this.playerId = playerId;
        this.playerUsername = playerUsername;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
        this.status = status;
        this.totalPrice = totalPrice;
        this.adminNote = adminNote;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerUsername() { return playerUsername; }
    public void setPlayerUsername(String playerUsername) { this.playerUsername = playerUsername; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldReservationDto that = (FieldReservationDto) o;
        return Objects.equals(id, that.id) && Objects.equals(fieldId, that.fieldId) && Objects.equals(fieldName, that.fieldName) && Objects.equals(fieldLocation, that.fieldLocation) && Objects.equals(playerId, that.playerId) && Objects.equals(playerUsername, that.playerUsername) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(notes, that.notes) && status == that.status && Objects.equals(totalPrice, that.totalPrice) && Objects.equals(adminNote, that.adminNote) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fieldId, fieldName, fieldLocation, playerId, playerUsername, startTime, endTime, notes, status, totalPrice, adminNote, createdAt);
    }

    @Override
    public String toString() {
        return "FieldReservationDto{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", playerUsername='" + playerUsername + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }

    public static FieldReservationDtoBuilder builder() {
        return new FieldReservationDtoBuilder();
    }

    public static class FieldReservationDtoBuilder {
        private Long id;
        private Long fieldId;
        private String fieldName;
        private String fieldLocation;
        private Long playerId;
        private String playerUsername;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String notes;
        private ReservationStatus status;
        private Double totalPrice;
        private String adminNote;
        private LocalDateTime createdAt;

        public FieldReservationDtoBuilder id(Long id) { this.id = id; return this; }
        public FieldReservationDtoBuilder fieldId(Long fieldId) { this.fieldId = fieldId; return this; }
        public FieldReservationDtoBuilder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public FieldReservationDtoBuilder fieldLocation(String fieldLocation) { this.fieldLocation = fieldLocation; return this; }
        public FieldReservationDtoBuilder playerId(Long playerId) { this.playerId = playerId; return this; }
        public FieldReservationDtoBuilder playerUsername(String playerUsername) { this.playerUsername = playerUsername; return this; }
        public FieldReservationDtoBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public FieldReservationDtoBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public FieldReservationDtoBuilder notes(String notes) { this.notes = notes; return this; }
        public FieldReservationDtoBuilder status(ReservationStatus status) { this.status = status; return this; }
        public FieldReservationDtoBuilder totalPrice(Double totalPrice) { this.totalPrice = totalPrice; return this; }
        public FieldReservationDtoBuilder adminNote(String adminNote) { this.adminNote = adminNote; return this; }
        public FieldReservationDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public FieldReservationDto build() {
            return new FieldReservationDto(id, fieldId, fieldName, fieldLocation, playerId, playerUsername, startTime, endTime, notes, status, totalPrice, adminNote, createdAt);
        }
    }
}