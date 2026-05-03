package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldReservationDto {
    private Long id;
    @JsonProperty("fieldId")
    private Long fieldId;
    @JsonProperty("fieldName")
    private String fieldName;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("playerId")
    private Long playerId;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("startTime")
    private LocalDateTime startTime;
    @JsonProperty("endTime")
    private LocalDateTime endTime;
    private String status;
    @JsonProperty("reservationDate")
    private LocalDateTime reservationDate;
    private Double totalPrice;
    private String notes;
    @JsonProperty("fieldLocation")
    private String fieldLocation;
    @JsonProperty("fieldType")
    private String fieldType;
}
