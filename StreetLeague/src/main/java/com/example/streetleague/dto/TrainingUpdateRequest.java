package com.example.streetleague.dto;

import com.example.streetleague.Entity.TrainingStatus;

import java.time.LocalDateTime;

public record TrainingUpdateRequest(
        Long idTraining,
        String title,
        String description,
        LocalDateTime trainingDate,
        Integer durationInMinutes,
        String location,
        String exercises,
        TrainingStatus status
) {}