package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingUpdateRequest {
    private Long id;
    
    @NotBlank(message = "Training title is required")
    private String title;

    private String description;
    
    @JsonProperty("startTime")
    private LocalDateTime startTime;
    
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
