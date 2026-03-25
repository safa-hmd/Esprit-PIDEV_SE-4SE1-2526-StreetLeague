package com.example.streetleague.dto;

import java.time.LocalDateTime;

public record TrainingRequest(
        String title,
        String description,
        LocalDateTime trainingDate,
        Integer durationInMinutes,
        String location,
        String exercises
) {}