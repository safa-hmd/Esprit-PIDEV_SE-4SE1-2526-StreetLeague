package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PricingResponse {

    @JsonProperty("suggested_price")
    private double suggestedPrice;

    @JsonProperty("base_price")
    private double basePrice;

    @JsonProperty("delta_percent")
    private double deltaPercent;

    @JsonProperty("model")
    private String model;
}
