package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequest {
    @NotBlank(message = "Training title is required")
    private String title;

    private String description;
    
    @NotNull(message = "Start time is required")
    @JsonProperty("startTime")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @JsonProperty("endTime")
    private LocalDateTime endTime;
    
    private String location;
    
    @JsonProperty("fieldId")
    private Long fieldId;
    
    @JsonProperty("coachId")
    private Long coachId;
    
    private String type;
    private String level;
    @JsonProperty("maxParticipants")
    private Integer maxParticipants;
    private String status;
}
