package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class waterReminderDTO {
    private Long id;



    @Min(value = 1)
    @Max(value = 120)
    private int frequency;

    @Min(value = 100, message = "Quantity must be at least 100ml")
    @Max(value = 2000, message = "Quantity must be at most 2000ml")
    private int quantity;
    private boolean active;

    private Long userId;
}

