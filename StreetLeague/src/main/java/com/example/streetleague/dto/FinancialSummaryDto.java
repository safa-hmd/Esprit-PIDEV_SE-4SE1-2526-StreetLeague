package com.example.streetleague.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialSummaryDto {
    private Double totalRevenue;
    private Long totalPayments;
    private Long totalRefunds;
    private Long pendingCount;
}