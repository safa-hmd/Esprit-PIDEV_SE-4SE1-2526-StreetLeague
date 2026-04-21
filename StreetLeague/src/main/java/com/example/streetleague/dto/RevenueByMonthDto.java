package com.example.streetleague.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByMonthDto {
    private String month; // format "2025-03"
    private Double revenue;
}