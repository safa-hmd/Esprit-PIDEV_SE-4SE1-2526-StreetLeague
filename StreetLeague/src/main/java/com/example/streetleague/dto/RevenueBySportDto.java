package com.example.streetleague.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueBySportDto {
    private String sportType;
    private Double revenue;
}