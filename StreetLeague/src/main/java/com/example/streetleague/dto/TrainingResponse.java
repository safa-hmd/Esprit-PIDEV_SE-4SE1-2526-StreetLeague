package com.example.streetleague.dto;

import com.example.streetleague.Entity.Training;
import com.example.streetleague.Entity.TrainingStatus;

import java.time.LocalDateTime;

public record TrainingResponse(
        Long idTraining,
        String title,
        String description,
        LocalDateTime trainingDate,
        Integer durationInMinutes,
        String location,
        String exercises,
        TrainingStatus status,
        String teamName,
        int participantCount
) {
    public static TrainingResponse fromEntity(Training training) {
        return new TrainingResponse(
                training.getIdTraining(),
                training.getTitle(),
                training.getDescription(),
                training.getTrainingDate(),
                training.getDurationInMinutes(),
                training.getLocation(),
                training.getExercises(),
                training.getStatus(),
                training.getTeam().getName(),
                training.getParticipants() == null ? 0 : training.getParticipants().size()
        );
    }
}