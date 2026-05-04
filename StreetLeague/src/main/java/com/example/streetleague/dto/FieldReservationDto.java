package com.example.streetleague.dto;

import com.example.streetleague.Entity.ReservationStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}