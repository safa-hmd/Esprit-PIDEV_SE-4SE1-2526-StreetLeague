package com.example.streetleague.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByFieldDto {
    private String fieldName;
    private Double revenue;
    private Long reservationCount;
}