package com.example.streetleague.dto;

import lombok.Data;

@Data
public class GoalDTO {
    private String goalType;    // "WATER" ou "BMI"
    private double targetValue;
}
