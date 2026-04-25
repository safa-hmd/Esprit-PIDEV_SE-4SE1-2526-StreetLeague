package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingRequest {

    @JsonProperty("sport_type")
    private String sportType;

    @JsonProperty("location")
    private String location;

    @JsonProperty("capacity")
    private int capacity;

    @JsonProperty("base_price_per_hour")
    private double basePricePerHour;

    @JsonProperty("duration_hours")
    private int durationHours;

    @JsonProperty("day_of_week")
    private int dayOfWeek;

    @JsonProperty("hour_of_day")
    private int hourOfDay;

    @JsonProperty("is_weekend")
    private int isWeekend;

    @JsonProperty("is_peak_hour")
    private int isPeakHour;

    @JsonProperty("month")
    private int month;

    @JsonProperty("occupation_rate")
    private double occupationRate;

    @JsonProperty("cancellation_rate")
    private double cancellationRate;
}