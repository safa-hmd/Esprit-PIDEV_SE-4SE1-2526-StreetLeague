package com.example.streetleague.dto;

import lombok.Data;

@Data
public class WaterReminderResponseDTO {
    private Long id;
    private int frequency;
    private int quantity;
    private boolean active;
    private String userName;
    private String userEmail;
}