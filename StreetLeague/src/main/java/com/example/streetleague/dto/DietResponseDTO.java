package com.example.streetleague.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietResponseDTO {
    private Long id;
    private String goal;
    private String status;
    private Integer dailyCalories;
    private List<String> dietPlan;
    private LocalDate createdDate;
}