package com.example.streetleague.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuggestedPriceResponse {

    private double suggestedPrice;
    private double basePrice;
    private double deltaPercent;
    private String sport;
    private String fieldName;
}
