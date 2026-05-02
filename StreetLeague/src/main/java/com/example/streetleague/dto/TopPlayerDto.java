package com.example.streetleague.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopPlayerDto {
    private String playerName;
    private String email;
    private Double totalSpent;
    private Long paymentCount;
}