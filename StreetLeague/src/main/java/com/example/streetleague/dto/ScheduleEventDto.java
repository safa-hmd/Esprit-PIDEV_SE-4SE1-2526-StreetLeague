// dto/ScheduleEventDto.java
package com.example.streetleague.dto;

import lombok.*;
import java.time.LocalDateTime;

// dto/ScheduleEventDto.java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScheduleEventDto {
    private Long id;
    private String type;           // "TRAINING" ou "MATCH"
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String status;
    private String teamName;
    private String coachName;
    private String opponentTeamName;
    private Integer scoreTeamA;
    private Integer scoreTeamB;
    private boolean hasConflict;
    private String conflictReason;
    private String color;
    private Double aiScore;          // score du modèle Flask (0-1)
    private String recommendation;   // EXCELLENT / ACCEPTABLE / DÉCONSEILLÉ
}