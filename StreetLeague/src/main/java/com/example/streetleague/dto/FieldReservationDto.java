package com.example.streetleague.dto;

import com.example.streetleague.Entity.ReservationStatus;
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
}
