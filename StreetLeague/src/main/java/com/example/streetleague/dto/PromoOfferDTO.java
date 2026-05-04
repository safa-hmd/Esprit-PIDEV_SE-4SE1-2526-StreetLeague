package com.example.streetleague.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class PromoOfferDTO {
    private String code;
    private String type; // "PERCENTAGE" ou "FIXED"
    private double value;
    private double minCartAmount;
    private LocalDateTime expirationDate;
    private boolean used;
    private boolean expired;
    private String source;
}