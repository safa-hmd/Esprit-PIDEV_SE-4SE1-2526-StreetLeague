package com.example.streetleague.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietRequestDTO {
    private Double bmi;
    private Integer age;
}