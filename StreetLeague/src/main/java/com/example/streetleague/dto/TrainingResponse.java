package com.example.streetleague.dto;

import com.example.streetleague.Entity.Training;
import com.example.streetleague.Entity.TrainingStatus;
import com.example.streetleague.domain.User;

import java.time.LocalDateTime;
import java.util.List;

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
        int participantCount,
        List<String> participantEmails
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
                training.getParticipants() == null ? 0 : training.getParticipants().size(),
                training.getParticipants() != null                          // ← ici
                        ? training.getParticipants().stream()
                        .map(User::getEmail)
                        .toList()
                        : List.of()
        );
    }
}