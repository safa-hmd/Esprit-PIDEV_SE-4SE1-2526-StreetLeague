package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class waterReminderDTO {
    private Long id;



    @Min(value = 1, message = "Frequency must be at least 1")
    @Max(value = 24, message = "Frequency must be at most 24")
    private int frequency;

    @Min(value = 100, message = "Quantity must be at least 100ml")
    @Max(value = 2000, message = "Quantity must be at most 2000ml")
    private int quantity;
    private boolean active;

    private Long userId;
}

