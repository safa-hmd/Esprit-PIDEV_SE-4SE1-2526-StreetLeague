package com.example.streetleague.dto;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PromoValidationDTO {
    private boolean valid;
    private String code;
    private double discountAmount;
    private double newTotal;
}