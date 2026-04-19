package com.example.streetleague.dto;

import lombok.Data;

@Data
public class RewardDTO {
    private String result;    // "BADGE", "FREE_DELIVERY", "COUPON_5", "COUPON_10", "TRY_AGAIN"
    private String message;
    private boolean canRetry;
    private int segmentIndex;
}
